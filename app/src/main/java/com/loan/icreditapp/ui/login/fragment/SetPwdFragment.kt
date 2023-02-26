package com.loan.icreditapp.ui.login.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.icredit.loancash.utils.Md5Mgr
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.login.ModifyPwdBean
import com.loan.icreditapp.bean.login.RegisterBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class SetPwdFragment : BaseFragment() {

    private val TAG = "SetPwdFragment"

    private var flCommit: FrameLayout? = null
    private var etCreatePwd: EditTextContainer? = null
    private var etConfirmPwd: EditTextContainer? = null
    private var flLoading: FrameLayout? = null
    private var ivCreatePwd: AppCompatImageView? = null
    private var ivModifyPwd: AppCompatImageView? = null
    private var logoContainer: ViewGroup ? = null

    private var mPhoneNum: String? = null
    private var mPrefix: String? = null
    private var mIsModify: Boolean? = null

    private var pwMode1 = true
    private var pwMode2 = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_setpwd, container, false)
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flCommit = view.findViewById(R.id.fl_setpwd_commit)
        etCreatePwd = view.findViewById(R.id.edittext_create_pwd)
        etConfirmPwd = view.findViewById(R.id.edittext_confirm_pwd)
        ivCreatePwd = view.findViewById(R.id.iv_create_pwd)
        ivModifyPwd = view.findViewById(R.id.iv_confirm_pwd)

        flLoading = view.findViewById(R.id.fl_set_pwd_loading)
        logoContainer = view.findViewById(R.id.include_logo_container)

        flCommit?.setOnClickListener {
            val strPassCode1: String = etCreatePwd?.getText().toString()
            val strPassCode2: String = etConfirmPwd?.getText().toString()
            if (TextUtils.isEmpty(strPassCode1) || TextUtils.isEmpty(strPassCode2)) {
                ToastUtils.showShort("password is null")
                return@setOnClickListener
            }
            if (!TextUtils.equals(strPassCode1, strPassCode2)) {
                ToastUtils.showShort(" two passwords not equal")
                etConfirmPwd?.requestFocus()
                etConfirmPwd?.setSelectionLast()
                return@setOnClickListener
            }
            if (mIsModify == true){
                modifyPwd(strPassCode2)
            } else {
                register(strPassCode2)
            }
        }
        ivCreatePwd?.setOnClickListener(View.OnClickListener {
            pwMode1 = !pwMode1
            etCreatePwd?.setPassWordMode(pwMode1)
            if (ivCreatePwd != null) {
                var icLogo:Int = if (pwMode1) R.drawable.ic_show_pwd else R.drawable.ic_hide_pwd
                ivCreatePwd?.setImageResource(icLogo)
            }
        })
        etCreatePwd?.setPassWordMode(pwMode1)

        ivModifyPwd?.setOnClickListener(View.OnClickListener {
            pwMode2 = !pwMode2
            etConfirmPwd?.setPassWordMode(pwMode2)
            if (ivModifyPwd != null) {
                var icLogo:Int = if (pwMode2) R.drawable.ic_show_pwd else R.drawable.ic_hide_pwd
                ivModifyPwd?.setImageResource(icLogo)
            }
        })
        etConfirmPwd?.setPassWordMode(pwMode2)
        if (mIsModify == true){
            logoContainer?.visibility = View.GONE
        }
    }

    fun setPhoneNum(phoneNum: String, prefix: String, isModify: Boolean) {
        mPhoneNum = phoneNum
        mPrefix = prefix
        mIsModify = isModify
    }

    private fun register(pwd: String) {
        flLoading?.visibility = View.VISIBLE
        //password	String
        //mobile	String
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        jsonObject.put("mobile", mPrefix + mPhoneNum)
        jsonObject.put("password", Md5Mgr.encodeMd5(pwd))
        OkGo.post<String>(Api.REGISTER).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val registerBean: RegisterBean? =
                        checkResponseSuccess(response, RegisterBean::class.java)
                    if (registerBean == null ) {
                        return
                    }
                    if (TextUtils.isEmpty(registerBean.token)){
                        ToastUtils.showShort("register failure. token = null")
                        return
                    }
                    Constant.mToken = registerBean.token
                    Constant.mAccountId = registerBean.accountId
                    Constant.mMobile = registerBean.mobile
                    SPUtils.getInstance().put(SignInFragment.KEY_PHONE_NUM, mPhoneNum)
                    SPUtils.getInstance().put(SignInFragment.KEY_PASS_CODE, pwd)
                    FirebaseUtils.logEvent("fireb_register")
                    if (activity is SignUpActivity) {
                        var signUpActivity : SignUpActivity = activity as SignUpActivity
                        signUpActivity.toHomePage()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, " register error ... ")
                    ToastUtils.showShort("register error.")
                }
            })
    }

    private fun modifyPwd(pwd: String){
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        jsonObject.put("mobile", mPrefix + mPhoneNum)
        jsonObject.put("newPassword", Md5Mgr.encodeMd5(pwd))
        OkGo.post<String>(Api.MODIFY_PSD).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val modifyBean: ModifyPwdBean? =
                        checkResponseSuccess(response, ModifyPwdBean::class.java)
                    if (modifyBean == null ) {
                        return
                    }
                    if (TextUtils.isEmpty(modifyBean.token)){
                        ToastUtils.showShort("register failure. token = null")
                        return
                    }
//                    Constant.mToken = modifyBean.token
//                    Constant.mAccountId = modifyBean.accountId
                    SPUtils.getInstance().put(SignInFragment.KEY_PHONE_NUM, mPhoneNum)
                    SPUtils.getInstance().put(SignInFragment.KEY_PASS_CODE, pwd)
                    if (activity is SignUpActivity) {
                        var signUpActivity : SignUpActivity = activity as SignUpActivity
                        signUpActivity.toSignInPage()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, " register error ... ")
                    ToastUtils.showShort("register error.")
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}