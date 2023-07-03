package com.loan.icreditapp.ui.login2

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.ServerLiveBean
import com.loan.icreditapp.bean.login.VerifyPhoneNumBean
import com.loan.icreditapp.dialog.term.TermsDialog
import com.loan.icreditapp.presenter.PhoneNumPresenter
import com.loan.icreditapp.ui.login.Login2Activity
import com.loan.icreditapp.ui.widget.BlankTextWatcher
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class Login2Fragment : BaseFragment() {

    companion object {
        const val TAG = "Login2Fragment"

        val KEY_PHONE_NUM_2 = "key_sign_in_phone_num_2"

        private const val KEY_SHOW_TERM_2 = "key_show_term_2"
    }

    private var etSignIn : AppCompatEditText? = null
    private var tvCommit : AppCompatTextView? = null
    private var mSpinner : Spinner? = null
    private var ivClear : AppCompatImageView? = null
    private var ivCheckState : AppCompatImageView? = null
    private var flLoading : FrameLayout? = null
    private var tvTerm : AppCompatTextView? = null
    private var tvPrivacy : AppCompatTextView? = null
    private var ivAgree : AppCompatImageView? = null

    private var mPresenter: PhoneNumPresenter? = null

    private var hasShowTerm = false
    private var dialog : TermsDialog? = null

    private var mPhoneNum : String?= null
    private var isAgree : Boolean  = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSignIn = view.findViewById<AppCompatEditText>(R.id.et_signin_phone_num)
        tvCommit = view.findViewById<AppCompatTextView>(R.id.tv_login2_commit)
        mSpinner = view.findViewById<Spinner>(R.id.spinner_signin_input)
        ivClear = view.findViewById<AppCompatImageView>(R.id.iv_signin_phonenum_clear)
        ivCheckState = view.findViewById<AppCompatImageView>(R.id.iv_login2_agree_state)
        flLoading = view.findViewById<FrameLayout>(R.id.fl_login2_loading)
        tvTerm = view.findViewById<AppCompatTextView>(R.id.tv_login2_terms_privacy_2)
        tvPrivacy = view.findViewById<AppCompatTextView>(R.id.tv_login2_terms_privacy_4)
        ivAgree = view.findViewById<AppCompatImageView>(R.id.iv_login2_agree_state)

        initView()

        hasShowTerm = SPUtils.getInstance().getBoolean(KEY_SHOW_TERM_2, false)
        if (!hasShowTerm) {
            showTermDialog(object : TermsDialog.OnClickAgreeListener {
                override fun onClickAgree() {
                    SPUtils.getInstance().put(KEY_SHOW_TERM_2, true)
                    hasShowTerm = true
                    dialog?.dismiss()
                }
            })
        }
    }

    private fun initView(){
        mPresenter = PhoneNumPresenter(context)
        mPresenter?.initSpinner(mSpinner!!)

        var et = BlankTextWatcher(etSignIn!!)
        etSignIn?.addTextChangedListener(et)

        etSignIn?.addTextChangedListener(object : TextWatcher {
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

        var phoneNum = SPUtils.getInstance().getString(KEY_PHONE_NUM_2, "")
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = mPhoneNum
        }
        if (etSignIn != null && !TextUtils.isEmpty(phoneNum)) {
            etSignIn!!.setText(phoneNum)
        }
        updateState()

        tvCommit?.setOnClickListener {
            if (!isAgree){
                ToastUtils.showShort(resources.getString(R.string.must_agree_term))
                return@setOnClickListener
            }
            var hasPermissions: Boolean = PermissionUtils.isGranted(Manifest.permission.READ_SMS)
            if (hasPermissions) {
                checkMobile()
            } else {
                PermissionUtils.permission(Manifest.permission.READ_SMS)
                    .callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            checkMobile()
                        }

                        override fun onDenied() {
                            checkMobile()
                        }
                    }).request()
            }
        }
        ivClear?.setOnClickListener {
            etSignIn?.setText("")
            etSignIn?.setSelection(0)
        }

        ivAgree?.setOnClickListener {
            isAgree = !isAgree
            updateState()
        }

        tvTerm?.setOnClickListener {
            if (activity is Login2Activity) {
                if (checkClickFast(false)){
                    return@setOnClickListener
                }
                var login2Activity : Login2Activity = activity as Login2Activity
                login2Activity.toWebView(Api.GET_TERMS)
            }
        }
        tvPrivacy?.setOnClickListener{
            if (activity is Login2Activity) {
                if (checkClickFast(false)){
                    return@setOnClickListener
                }
                var login2Activity : Login2Activity = activity as Login2Activity
                login2Activity.toWebView(Api.GET_POLICY)
            }
        }
    }

    private fun checkMobile(){
        var phoneNum: String = etSignIn?.getText().toString()
        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNum = phoneNum?.replace(" ", "")
        }
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtils.showShort("phone num = null")
            return
        }
        if (checkClickFast()){
            return
        }
        mPhoneNum = phoneNum
        FirebaseUtils.logEvent("fireb_click_sign")
        checkServerAvailable(object : CallBack {
            override fun onEnd() {
                if (isDestroy()){
                    return
                }
                requestSendSms(phoneNum)
            }
        })
    }

    //申请发送短信
    private fun requestSendSms(phoneNum : String) {
        flLoading?.visibility = View.VISIBLE
        tvCommit?.isEnabled = false
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        val mPrex: String? = mPresenter?.getSelectString(0)
        var finalPhoneNum = phoneNum
        try {
            if (!TextUtils.isEmpty(mPrex)) {
                var realNum = phoneNum
                if (phoneNum.startsWith("0")){
                    realNum = phoneNum.substring(1, phoneNum.length)
                }
                finalPhoneNum = mPrex + realNum
            }
            jsonObject.put("mobile", finalPhoneNum)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //“1”:注册，“2”：修改密码 3 设备更换
        jsonObject.put("captchaType",  "1")

        OkGo.post<String>(Api.GET_SMS_CODE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    tvCommit?.isEnabled = true
                    var responseBean: BaseResponseBean? = null
                    try {
                        responseBean = com.alibaba.fastjson.JSONObject.parseObject(
                            response.body().toString(),
                            BaseResponseBean::class.java
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                    if (response == null){
                        ToastUtils.showShort(" send sms failure.")
                        return
                    }
                    if (responseBean!!.isRequestSuccess() != true) {
                        ToastUtils.showShort("" + responseBean.getMessage())
                        return
                    }
                    if (activity is Login2Activity) {
                        (activity as Login2Activity).toOtpFragment(mPrex!!, phoneNum)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    tvCommit?.isEnabled = true
                    Log.e(TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }

    private fun checkServerAvailable(callBack: CallBack?) {
        val jsonObject: JSONObject? = BuildRequestJsonUtils.buildRequestJson()
        try {

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        Log.e(TAG, " = " + jsonObject.toString());
        OkGo.post<String>(Api.CHECK_SERVER_ALIVE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val serverLiveBean: ServerLiveBean? =
                        checkResponseSuccess(response, ServerLiveBean::class.java)
                    if (serverLiveBean != null && serverLiveBean.isServerLive()) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "the server is alive")
                        }
                        callBack?.onEnd()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "the server is not alive")
                    }
                    if (isDestroy()){
                        return
                    }
                    ToastUtils.showShort(resources.getString(R.string.server_is_not_alive))
                }
            })
    }

    interface CallBack {
        fun onEnd()
    }

    private fun requestCheckMobile(phoneNum : String){
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
            jsonObject.put("mobile", finalPhoneNum)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.CHECK_MOBILE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val verifyPhoneNumBean: VerifyPhoneNumBean? = checkResponseSuccess(response, VerifyPhoneNumBean::class.java)
                    if (verifyPhoneNumBean == null) {
//                        ToastUtils.showShort("sign in failure.")
                        return
                    }
                    if (verifyPhoneNumBean.hasRegisted) {
                        ToastUtils.showShort("phone num has registed.")
                        return
                    }
                    Log.e(TAG, "sign in error")
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, "sign in error")
                    ToastUtils.showShort("sign in error..")
                }
            })
    }
    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        if (dialog != null && dialog!!.isShowing){
            dialog?.dismiss()
        }
        super.onDestroy()
    }
    private fun showTermDialog(listener: TermsDialog.OnClickAgreeListener) {
        if (dialog == null){
            dialog = TermsDialog(requireContext())
        }
        dialog?.setOnClickListener(listener)
        if (dialog?.isShowing == true){
            return
        }
        dialog?.show()
    }

    private fun updateState(){
        ivAgree?.setImageResource(if (isAgree) R.drawable.btn_agree else R.drawable.btn_disagree)
    }
}