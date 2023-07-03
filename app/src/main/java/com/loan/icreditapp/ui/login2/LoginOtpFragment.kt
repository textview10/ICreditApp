package com.loan.icreditapp.ui.login2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.KeyboardUtils.OnSoftInputChangedListener
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.login.RegLoginBean
import com.loan.icreditapp.bean.login.UssdBean
import com.loan.icreditapp.bean.login.VerifySmsCodeBean
import com.loan.icreditapp.collect.ReadSmsMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.login.Login2Activity
import com.loan.icreditapp.ui.widget.InputVerifyCodeView
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoginOtpFragment : BaseFragment(){
    private var mPhoneNum : String? = null
    private var mPrex : String? = null

    private var ivBack : AppCompatImageView? = null
    private var tvCommit : AppCompatTextView? = null
    private var tvDesc1 : AppCompatTextView? = null
    private var tvReceive : AppCompatTextView? = null
    private var verifyCodeView : InputVerifyCodeView? = null
    private var flLoading : FrameLayout? = null

    private var mHandler: Handler? = null
    private var mAuthCode : String? = null

    private val MAX_TIME = 60
    private var mCurTime: Int = MAX_TIME

    companion object {
        private const val TYPE_TIME_REDUCE = 1111

        const val TAG = "LoginOtpFragment"
    }

    init {
        mHandler = Handler(Looper.getMainLooper()) { message ->
            when (message.what) {
                TYPE_TIME_REDUCE -> {
                    mCurTime--
                    if (tvCommit != null) {
                        if (mCurTime == MAX_TIME) {
                            tvCommit?.text = StringUtils.getString(R.string.resend)
                            tvCommit?.isEnabled = true
//                            tvCommit?.setTextColor(Color.parseColor("#0EC6A2"))
                            mHandler?.sendEmptyMessageDelayed(TYPE_TIME_REDUCE, 1000)
                        } else if (mCurTime == 0) {
                            mHandler?.removeMessages(TYPE_TIME_REDUCE)
                            tvCommit?.text = StringUtils.getString(R.string.resend)
                            tvCommit?.isEnabled = true
//                            tvCommit?.setTextColor(Color.parseColor("#0EC6A2"))
                            mCurTime = MAX_TIME
                        } else {
                            val text = ( mCurTime.toString() + "s")
                            tvCommit?.isEnabled = false
                            tvCommit?.text = text
                            mHandler?.sendEmptyMessageDelayed(TYPE_TIME_REDUCE, 1000)
                        }
                    }
                }
            }
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_otp_login, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBack = view.findViewById<AppCompatImageView>(R.id.iv_title_back)
        tvCommit = view.findViewById<AppCompatTextView>(R.id.tv_otp_login_commit)
        verifyCodeView = view.findViewById<InputVerifyCodeView>(R.id.input_verify_code_otp)
        flLoading = view.findViewById<FrameLayout>(R.id.fl_otp_login_loading)
        tvDesc1 = view.findViewById<AppCompatTextView>(R.id.tv_login_otp_desc1)
        tvReceive = view.findViewById<AppCompatTextView>(R.id.tv_can_not_recevie)

        verifyCodeView?.setObserver(object : InputVerifyCodeView.Observer {
            override fun onEnd() {
                if (isDestroy()) {
                    return
                }
                if (verifyCodeView != null) {
                    val verifyCode: String? = verifyCodeView?.getVerifyCode()
                    if (TextUtils.isEmpty(verifyCode)) {
                        ToastUtils.showShort("verify code = null. please input again.")
                        return
                    }
                    checkVerifySmsCode(false)
                }
            }

            override fun onFocusChange(hasFocus: Boolean) {
                if (isDestroy()) {
                    return
                }
                var colorRes: Int
                if (hasFocus) {
                    colorRes = R.color.verify_sms_select
                } else {
                    colorRes = R.color.signin_unselect
                }
//                tvVerifyCode?.setTextColor(resources.getColor(colorRes))
            }
        })

        ivBack?.setOnClickListener{
            if (activity is Login2Activity) {
                (activity as Login2Activity).toLoginFragment()
                if (KeyboardUtils.isSoftInputVisible(requireActivity())){
                    KeyboardUtils.hideSoftInput(requireActivity())
                }
            }
        }
        tvCommit?.setOnClickListener{
            if (TextUtils.isEmpty(getFinalPhoneNum())){
                ToastUtils.showShort("phone num is null")
            }
            requestSendSms(getFinalPhoneNum())
            mHandler?.sendEmptyMessage(TYPE_TIME_REDUCE)
        }
        mHandler?.sendEmptyMessage(TYPE_TIME_REDUCE)
        verifyCodeView?.post(Runnable {
            if (isDestroy() || verifyCodeView == null){
                return@Runnable
            }
            verifyCodeView!!.getEditText()?.let {
                KeyboardUtils.showSoftInput(it)
            }
        })

        tvReceive?.setOnClickListener{
            toSendCodeActivity()
        }

        val text = resources.getString(R.string.login_otp)
        val textDesc = String.format(text, getFinalPhoneNum())
        tvDesc1?.text = textDesc

        KeyboardUtils.registerSoftInputChangedListener(requireActivity(), object : OnSoftInputChangedListener {
            override fun onSoftInputChanged(height: Int) {
                if (tvReceive == null){
                    return
                }
                val marginBottom = ConvertUtils.dp2px(18f)
                val layoutParams = tvReceive!!.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomMargin = (height + marginBottom)
                tvReceive!!.layoutParams = layoutParams
            }

        })
        ReadSmsMgr.setObserver(object : ReadSmsMgr.Observer {
            override fun onReceiveAuthCode(authCode: String) {
                if (TextUtils.equals(mAuthCode, authCode)) {
                    return
                }
                mAuthCode = authCode
                if (TextUtils.isEmpty(mAuthCode)) {
                    return
                }
                mHandler?.postDelayed(Runnable {
                    if (isDestroy()){
                        return@Runnable
                    }
                    verifyCodeView?.setVerifyCode(mAuthCode!!)
                    Log.e("Test", " authCode = " + mAuthCode)
                }, 100)

            }

        })

    }
    fun setPhoneNum(prex : String , phoneNum: String){
        mPrex = prex
        mPhoneNum = phoneNum
    }

    private fun checkVerifySmsCode(needTip : Boolean){
        if (TextUtils.isEmpty(getFinalPhoneNum())) {
            if (BuildConfig.DEBUG) {
                throw java.lang.IllegalArgumentException("checkVerifySmsCode")
            }
            return
        }
        var verifyCode = verifyCodeView?.getVerifyCode()
        if (TextUtils.isEmpty(verifyCode)) {
            if (needTip) {
                ToastUtils.showShort(" verify code is null")
            }
//            verifyCodeView?.clearAll()
            return
        }
        if (checkClickFast()){
            return
        }
//        FirebaseUtils.logEvent("fireb_register_start")
        requestVerifySmsCode(verifyCode)
    }

    private fun requestVerifySmsCode(verifyCode: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", getFinalPhoneNum())
            jsonObject.put("captchaCode", verifyCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        flLoading?.visibility = View.VISIBLE
        OkGo.post<String>(Api.CHECK_SMS_CODE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    flLoading?.visibility = View.GONE
                    val baseResponseBean: VerifySmsCodeBean? = checkResponseSuccess(response, VerifySmsCodeBean::class.java)
                    if (baseResponseBean == null) {
                        return
                    }
                    if (!baseResponseBean.verifyed) {
                        ToastUtils.showShort(resources.getString(R.string.check_sms_code_verify_failure))
                        return
                    }
                    regOrLogin()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }

    private fun regOrLogin(){
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            var finalPhoneNum = getFinalPhoneNum()
            jsonObject.put("mobile", finalPhoneNum)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.REG_LOGIN_V2).tag(Login2Fragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val regLoginBean: RegLoginBean? =
                        checkResponseSuccess(response, RegLoginBean::class.java)
                    if (regLoginBean == null) {
                        return
                    }
                    if (TextUtils.isEmpty(regLoginBean.token)){
                        return
                    }
                    toHomePage(regLoginBean)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(Login2Fragment.TAG, "sign in error")
                    var errorStr = ""
                    if (response != null){
                        try {
                            errorStr = response.body()
                        } catch (e : Exception) {

                        }
                    }
                    ToastUtils.showShort("sign in error..$errorStr")
                }
            })
    }

    private var startFlag = false

    override fun onResume() {
        super.onResume()
        ReadSmsMgr.onResume()
        if (startFlag) {
            startFlag = false
            ussdLogin()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ReadSmsMgr.onDestroy()
    }

    private fun toSendCodeActivity(){
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        val tel = Uri.encode("*347*8#")
        intent.data = Uri.parse("tel:$tel")
        startActivity(intent)
        startFlag = true
    }

    private fun requestSendSms(phoneNum : String) {
        flLoading?.visibility = View.VISIBLE
        tvCommit?.isEnabled = false
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        jsonObject.put("mobile", phoneNum)
        //“1”:注册，“2”：修改密码 3 设备更换
        jsonObject.put("captchaType", "1")
        OkGo.post<String>(Api.GET_SMS_CODE).tag(Login2Fragment.TAG)
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
                    if (response == null) {
                        ToastUtils.showShort(" send sms failure.")
                        return
                    }
                    if (responseBean!!.isRequestSuccess() != true) {
                        ToastUtils.showShort("" + responseBean.getMessage())
                        return
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    tvCommit?.isEnabled = true
                    Log.e(Login2Fragment.TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }

    private fun ussdLogin(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", getFinalPhoneNum())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        flLoading?.visibility = View.VISIBLE
        Log.d(TAG, "ussd login  = " + jsonObject.toString());
        OkGo.post<String>(Api.USSD_CHECK).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val ussdBean: UssdBean? =
                        checkResponseSuccess(response, UssdBean::class.java)
                    if (ussdBean == null) {
                        return
                    }
                    try {
                        LogSaver.logToFile("ussd login response = " + GsonUtils.toJson(ussdBean)
                                + "   mobile = " + (jsonObject.optString("mobile")))
                    } catch (e : Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                    if (TextUtils.equals(ussdBean.verify, "1")){
                        regOrLogin()
                    } else {
                        ToastUtils.showShort(resources.getString(R.string.ussd_login_failure))
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "ussd login error")
                    }
                    ToastUtils.showShort(resources.getString(R.string.ussd_login_error))
                }
            })
    }

    private fun toHomePage(bean: RegLoginBean){
        Constant.mAccountId = bean.accountId
        Constant.mToken = bean.token
        Constant.mMobile = bean.mobile
        SPUtils.getInstance().put(Login2Fragment.KEY_PHONE_NUM_2, mPhoneNum)
        if (KeyboardUtils.isSoftInputVisible(requireActivity())){
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        if (activity is Login2Activity) {
            var signIn : Login2Activity = activity as Login2Activity
            signIn.toHomePage()
        }
    }

    private fun getFinalPhoneNum() : String{
        return mPrex + mPhoneNum
    }
}