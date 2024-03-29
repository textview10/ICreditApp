package com.loan.icreditapp.ui.login.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.icredit.loancash.utils.Md5Mgr
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.login.CaptchaBean
import com.loan.icreditapp.bean.login.SignInBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.presenter.PhoneNumPresenter
import com.loan.icreditapp.ui.login.SignInActivity
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.ui.widget.BlankTextWatcher
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class SignInFragment : BaseFragment() {

    private val TAG = "SignInFragment"

    private var etSignInPwd: EditTextContainer? = null
    private var flCommit: FrameLayout? = null
    private var tvPhoneNumTitle: AppCompatTextView? = null
    private var mSpinner: Spinner? = null
    private var etPhoneNum: AppCompatEditText? = null
    private var ivClear: ImageView? = null
    private var ivShowPwd: ImageView? = null
    private var flLoading: FrameLayout? = null
    private var tvForgetPsd: AppCompatTextView? = null
    private var flCaptcha: FrameLayout? = null
    private var etCaptcha: EditTextContainer? = null
    private var mPresenter: PhoneNumPresenter? = null

    private var passwordMode = true

    companion object {
        val KEY_PHONE_NUM = "key_sign_in_phone_num"
        val KEY_PASS_CODE = "key_sign_in_pass_code"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_signin, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSignInPwd = view.findViewById(R.id.et_signin_pwd)
        flCommit = view.findViewById(R.id.fl_signin_commit)

        tvPhoneNumTitle = view.findViewById(R.id.tv_signin_phone_num)
        mSpinner = view.findViewById(R.id.spinner_signin_input)
        etPhoneNum = view.findViewById(R.id.et_signin_phone_num)
        ivClear = view.findViewById(R.id.iv_signin_phonenum_clear)
        ivShowPwd = view.findViewById(R.id.iv_signin_show_pwd)
        flLoading = view.findViewById(R.id.fl_siginin_loading)
        tvForgetPsd = view.findViewById(R.id.tv_signin_forgot_password)
        flCaptcha = view.findViewById(R.id.fl_signin_captcha)
        etCaptcha = view.findViewById(R.id.et_signin_captcha)

        etPhoneNum?.setOnFocusChangeListener(View.OnFocusChangeListener { view, b ->
            run {
                var colorRes: Int
                if (b) {
                    colorRes = R.color.verify_sms_select
                } else {
                    colorRes = R.color.signin_unselect
                }
                tvPhoneNumTitle?.setTextColor(resources.getColor(colorRes))
            }
        })
        etPhoneNum?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val text: String = editable.toString()
                if (!TextUtils.isEmpty(text)) {
                    ivClear?.visibility = View.VISIBLE
                } else {
                    ivClear?.visibility = View.GONE
                }
            }
        })
        var et = BlankTextWatcher(etPhoneNum!!)
        etPhoneNum?.addTextChangedListener(et)
        ivClear?.setOnClickListener {
            etPhoneNum?.setText("")
            etPhoneNum?.setSelection(0)
        }

        flCommit?.setOnClickListener {
            checkAndSignIn()
        }
        ivShowPwd?.setOnClickListener {
            passwordMode = !passwordMode
            etSignInPwd?.setPassWordMode(passwordMode)
            if (ivShowPwd != null) {
                var icLogo:Int = if (passwordMode) R.drawable.ic_show_pwd else R.drawable.ic_hide_pwd
                ivShowPwd?.setImageResource(icLogo)
            }
        }
        tvForgetPsd?.setOnClickListener(View.OnClickListener {
            if (activity?.isDestroyed == true ||
                activity?.isFinishing == true){
                return@OnClickListener
            }
            if (activity is SignInActivity) {
                var phoneNum: String = etPhoneNum?.getText().toString()
                if (!TextUtils.isEmpty(phoneNum)) {
                    SignUpActivity.startActivity(
                        requireContext(),
                        SignUpActivity.SIGNUP_MODIFY,
                        phoneNum
                    )
                    requireActivity().finish()
                }
            }
        })
        initView()
    }

    private fun initView() {
        mPresenter = PhoneNumPresenter(context)
        mPresenter?.initSpinner(mSpinner!!)

        val phoneNum = SPUtils.getInstance().getString(KEY_PHONE_NUM)
        val passCode = SPUtils.getInstance().getString(KEY_PASS_CODE)
        if (!TextUtils.isEmpty(phoneNum)) {
            etPhoneNum?.setText(phoneNum)
            etPhoneNum?.setSelection(phoneNum.length - 1)
        }
        if (!TextUtils.isEmpty(passCode)) {
            etSignInPwd?.setEditTextAndSelection(passCode)
        }

        etSignInPwd?.setPassWordMode(passwordMode)
    }

    private fun checkAndSignIn(){
        var phoneNum: String = etPhoneNum?.getText().toString()
        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNum = phoneNum?.replace(" ", "")
        }
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtils.showShort("phone num = null")
            return
        }
        val pwd: String = etSignInPwd?.getText().toString()
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("password = null")
            return
        }
        var authCode : String? = null
        if ((flCaptcha?.visibility == View.VISIBLE) && mSignInBean != null){
            authCode = etCaptcha?.getText().toString()
        }
        if (checkClickFast()){
            return
        }
        FirebaseUtils.logEvent("fireb_click_sign")
        signIn(phoneNum, pwd, authCode)
    }

    private var mSignInBean: SignInBean? = null
    private fun signIn(phoneNum : String, password : String, authCode : String?){
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            var finalPhoneNum = phoneNum
            var temp: String? = mPresenter?.getSelectString(0)
            if (!TextUtils.isEmpty(temp)) {
                var realNum = phoneNum
                if (phoneNum.startsWith("0")){
                    realNum = phoneNum.substring(1, phoneNum.length)
                }
                finalPhoneNum = temp + realNum
            }
//            2348888888888
            jsonObject.put("mobile", finalPhoneNum)
            jsonObject.put("password", Md5Mgr.encodeMd5(password))
            if (!TextUtils.isEmpty(authCode)) {
                jsonObject.put("authCode", authCode)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.LOGIN).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val signInBean: SignInBean? =
                        checkResponseSuccess(response, SignInBean::class.java)
                    if (signInBean == null) {
//                        ToastUtils.showShort("sign in failure.")
                        return
                    }
                    if (TextUtils.isEmpty(signInBean.token)){
                        return
                    }
                    deviceCheck(signInBean.mobile!!, signInBean, phoneNum, password)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, "sign in error")
                    ToastUtils.showShort("sign in error..")
                }
            })
    }

    private fun showOrHide(showFlag : Boolean){
        flCaptcha?.visibility = if (showFlag) View.VISIBLE else View.GONE
    }

    private fun deviceCheck(mobile : String, signInBean: SignInBean, phoneNum : String, password : String){
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", mobile)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        this.mSignInBean = signInBean
        OkGo.post<String>(Api.DEVICE_CHECK).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    val captchaBean: CaptchaBean? =
                        checkResponseSuccess(response, CaptchaBean::class.java)
                    if (captchaBean == null) {
                        return
                    }
                    if (captchaBean.verify == 0) {
                        showOrHide(true)
                        ToastUtils.showShort(resources.getString(R.string.device_check_failure))
                        sendDeviceCaptcha(mobile)
                        return
                    }
                    toHomePage(mSignInBean!!, phoneNum, password)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    showOrHide(true)
                    ToastUtils.showShort("device check error..")
                }
            })
    }

    private fun toHomePage(signInBean: SignInBean, phoneNum : String, password : String){
        Constant.mAccountId = signInBean.accountId
        Constant.mToken = signInBean.token
        Constant.mMobile = signInBean.mobile
        SPUtils.getInstance().put(KEY_PHONE_NUM, phoneNum)
        SPUtils.getInstance().put(KEY_PASS_CODE, password)
        if (activity is SignInActivity) {
            var signIn : SignInActivity = activity as SignInActivity
            signIn.toHomePage()
        }
    }

    private fun sendDeviceCaptcha(phoneNum : String){
        etCaptcha?.getEditText()?.requestFocus()
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", phoneNum)
            jsonObject.put("captchaType", "3")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.DEVICE_CAPTCHA).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isRemoving || isDetached) {
                        return
                    }
                    ToastUtils.showShort(resources.getString(R.string.send_sms_error))
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}