package com.loan.icreditapp.ui.login.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.login.UssdBean
import com.loan.icreditapp.bean.login.VerifyPhoneNumBean
import com.loan.icreditapp.bean.login.VerifySmsCodeBean
import com.loan.icreditapp.presenter.PhoneNumPresenter
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.ui.widget.BlankTextWatcher
import com.loan.icreditapp.ui.widget.InputVerifyCodeView
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

class SignUpFragment : BaseFragment() {

    private val TAG = "SignUpFragment"

    private var mSpinner: Spinner? = null
    private var mEtPhoneNum: AppCompatEditText? = null
    private var tvPhoneNum: AppCompatTextView? = null
    private var tvVerifyCode: AppCompatTextView? = null
    private var tvResend: AppCompatTextView? = null
    private var ivClear: ImageView? = null
    private var verifyCodeView: InputVerifyCodeView? = null
    private var flCommit: FrameLayout ? = null
    private var ivAgree: AppCompatImageView ? = null
    private var tvTerm: AppCompatTextView ? = null
    private var tvPrivacy: AppCompatTextView ? = null
    private var logoContainer: ViewGroup ? = null
    private var flSendCode2: FrameLayout ? = null
    private var flLoading: FrameLayout ? = null
    private var tvSendCode3: TextView ? = null

    private var mPresenter: PhoneNumPresenter? = null

    private val TYPE_TIME_REDUCE = 1111
    private val MAX_TIME = 60
    private var mCurTime: Int = MAX_TIME

    private var mHandler: Handler? = null
    //是否是新修改密码
    private var mIsModify : Boolean  = false
    private var mPhoneNum : String?  = ""
    private var mPrex : String?  = ""

    private var isAgree : Boolean  = false
    private var isCheckSms : Boolean  = false

    private var intCount : Int = 0
    init {
        mHandler = Handler(Looper.getMainLooper()) { message ->
            when (message.what) {
                TYPE_TIME_REDUCE -> {
                    mCurTime--
                    if (tvResend != null) {
                        if (mCurTime == MAX_TIME) {
                            tvResend?.setText(StringUtils.getString(R.string.Resent_now))
                            tvResend?.setTextColor(Color.parseColor("#0EC6A2"))
                            mHandler?.sendEmptyMessageDelayed(TYPE_TIME_REDUCE, 1000)
                        } else if (mCurTime == 0) {
                            isCheckSms = false
                            mHandler?.removeMessages(TYPE_TIME_REDUCE)
                            tvResend?.setText(StringUtils.getString(R.string.Resent_now))
                            tvResend?.setTextColor(Color.parseColor("#0EC6A2"))
                            mCurTime = MAX_TIME
                        } else {
                            val text = (StringUtils.getString(R.string.Resent_after) + " (" + mCurTime + ")")
                            tvResend?.setText(text)
                            tvResend?.setTextColor(Color.parseColor("#999999"))
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
        var view = inflater.inflate(R.layout.fragment_signup, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSpinner = view.findViewById(R.id.spinner_signup_input)
        mEtPhoneNum = view.findViewById(R.id.et_signup_phone_num)
        tvPhoneNum = view.findViewById(R.id.tv_signup_phone_num)
        tvVerifyCode = view.findViewById(R.id.tv_signup_verify_code)
        tvResend = view.findViewById(R.id.tv_signin_resend)
        flCommit = view.findViewById(R.id.fl_signup_commit)
        logoContainer = view.findViewById(R.id.include_logo_container)

        ivClear = view.findViewById(R.id.iv_signup_phonenum_clear)
        verifyCodeView = view.findViewById(R.id.view_input_verify_code_verify_code)
        ivAgree = view.findViewById(R.id.iv_signup_agree_state)
        tvTerm = view.findViewById(R.id.tv_signup_term)
        tvPrivacy = view.findViewById(R.id.tv_signup_privact)
        flSendCode2 = view.findViewById(R.id.fl_send_code_2)
        tvSendCode3 = view.findViewById(R.id.tv_send_code_3)
        flLoading = view.findViewById(R.id.fl_signup_loading)

        mEtPhoneNum?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            run {
                var colorRes: Int
                if (b) {
                    colorRes = R.color.verify_sms_select
                } else {
                    colorRes = R.color.signin_unselect
                }
                tvPhoneNum?.setTextColor(resources.getColor(colorRes))
            }
        })
        mEtPhoneNum?.addTextChangedListener(object : TextWatcher {
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
        var tw1 = BlankTextWatcher(mEtPhoneNum!!)
        mEtPhoneNum?.addTextChangedListener(tw1)

        ivClear?.setOnClickListener {
            mEtPhoneNum?.setText("")
            mEtPhoneNum?.setSelection(0)
        }

        verifyCodeView?.setObserver(object : InputVerifyCodeView.Observer {
            override fun onEnd() {
                if (isDestroy()) {
                    return
                }
                if (isAgree != true){
                    return
                }
                if (verifyCodeView != null) {
                    val verifyCode: String? = verifyCodeView?.getVerifyCode()
                    if (TextUtils.isEmpty(verifyCode)) {
                        ToastUtils.showShort("verify code = null. please input again.")
                        return
                    }
                    fillPhoneOrPrefix()
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
                tvVerifyCode?.setTextColor(resources.getColor(colorRes))
            }
        })
        tvResend?.setOnClickListener {
            if (checkClickFast()){
                return@setOnClickListener
            }
            if (!mIsModify && intCount >= 0){
                flSendCode2?.visibility = View.VISIBLE
                tvSendCode3?.visibility = View.VISIBLE
            }
            if (intCount == 0){
                FirebaseUtils.logEvent("fireb_send_sms")
            } else {
                FirebaseUtils.logEvent("fireb_resend_sms")
            }
            intCount++
            if (isCheckSms){
                ToastUtils.showShort(resources.getString(R.string.please_send_msg_later))
                return@setOnClickListener
            }
            fillPhoneOrPrefix()
            if (mIsModify){
                requestSendSms()
            } else {
                checkAndVerifyPhoneNum()
            }
        }
        mEtPhoneNum?.requestFocus()

        flCommit?.setOnClickListener {
            if (!isAgree){
                ToastUtils.showShort(resources.getString(R.string.must_agree_term))
                return@setOnClickListener
            }
            fillPhoneOrPrefix()
            if (TextUtils.isEmpty(mPhoneNum)) {
                ToastUtils.showShort("must input phone num.")
                return@setOnClickListener
            }
            checkVerifySmsCode(true)
        }

        ivAgree?.setOnClickListener {
            isAgree = !isAgree
            updateState()
        }
        tvTerm?.setOnClickListener {
            if (activity is SignUpActivity) {
                if (checkClickFast()){
                    return@setOnClickListener
                }
                var signUpActivity : SignUpActivity = activity as SignUpActivity
                signUpActivity.toWebView(Api.GET_TERMS)
            }
        }
        tvPrivacy?.setOnClickListener{
            if (activity is SignUpActivity) {
                if (checkClickFast()){
                    return@setOnClickListener
                }
                var signUpActivity : SignUpActivity = activity as SignUpActivity
                signUpActivity.toWebView(Api.GET_POLICY)
            }
        }
        flSendCode2?.setOnClickListener{
            if (checkClickFast()){
                return@setOnClickListener
            }
            toSendCode()
        }
        tvSendCode3?.setOnClickListener {
            if (checkClickFast()){
                return@setOnClickListener
            }
            toSendCode()
        }
        initView()
    }

    private fun toSendCode(){
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        val tel = Uri.encode("*347*8#")
        intent.data = Uri.parse("tel:$tel")
        startActivity(intent)
        startFlag = true
    }

    var startFlag = false
    override fun onResume() {
        super.onResume()
        if (startFlag) {
            startFlag = false
            fillPhoneOrPrefix()
            ussdLogin()
        }
    }

    private fun ussdLogin(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            var realNum = mPhoneNum
            if (mPhoneNum!!.startsWith("0")){
                realNum = mPhoneNum!!.substring(1, mPhoneNum!!.length)
            }
            if (TextUtils.isEmpty(realNum)) {
                ToastUtils.showShort(resources.getString(R.string.enter_correct_phone_num))
                return
            }
            jsonObject.put("mobile", mPrex + realNum)
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
                        verifySuccess()
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

    private fun isValidPhone(str: String?): Boolean {
        if (str == null || TextUtils.isEmpty(str)){
            return false
        }
//        String regExp = "^((13[0-9])|(14[4-9])|(15[^4])|(16[6-7])|(17[^9])|(18[0-9])|(19[0|1|2|6|7|8|9]))\\d{8}$";
        val regExp = "^2340\\d{10}$" //（^254\d{9}$)
        val bregExp = "^234\\d{10}$" //（^254\d{9}$)
        val p = Pattern.compile(regExp)
        val m = p.matcher(str.replace(" ".toRegex(), ""))
        val p1 = Pattern.compile(bregExp)
        val m1 = p1.matcher(str.replace(" ".toRegex(), ""))
        return m.matches() || m1.matches()
    }

    private fun initView() {
        mPresenter = PhoneNumPresenter(context)
        mPresenter?.initSpinner(mSpinner!!)
        if (!TextUtils.isEmpty(mPhoneNum)){
            mEtPhoneNum?.setText(mPhoneNum)
        }
        updateState()
        if (mIsModify){
            if (activity is SignUpActivity) {
                var signUpActivity = activity as SignUpActivity
                signUpActivity.setTitle("Password Reset")
            }
            logoContainer?.visibility = View.GONE
        }
    }

    private fun updateState(){
        ivAgree?.setImageResource(if (isAgree) R.drawable.btn_agree else R.drawable.btn_disagree)
    }

    fun setIsModify(phoneNum : String){
        mIsModify = true
        mPhoneNum = phoneNum
        if (!TextUtils.isEmpty(mPhoneNum)) {
            mEtPhoneNum?.setText(mPhoneNum)
        }
    }

    private fun checkAndVerifyPhoneNum(){
        if (mCurTime != MAX_TIME) {
            ToastUtils.showShort("please click later")
            return
        }
        if (TextUtils.isEmpty(mPhoneNum) || TextUtils.isEmpty(mPrex)){
            ToastUtils.showShort("phone num == null, need send sms")
            return
        }
        requestCheckPhoneNum()
    }

    private fun fillPhoneOrPrefix(){
        mPhoneNum = mEtPhoneNum?.text.toString()
        if (!TextUtils.isEmpty(mPhoneNum)) {
            mPhoneNum = mPhoneNum?.replace(" ", "")
        }
        mPrex = mPresenter?.getSelectString(mSpinner?.getSelectedItemPosition()!!)
    }

    private fun requestCheckPhoneNum() {
        isCheckSms = true
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            var realNum = mPhoneNum
            if (mPhoneNum!!.startsWith("0")){
                realNum = mPhoneNum!!.substring(1, mPhoneNum!!.length)
            }
            jsonObject.put("mobile", mPrex + realNum)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        flLoading?.visibility = View.VISIBLE
        Log.d(TAG, "requestCheckPhoneNum = " + jsonObject.toString());
        OkGo.post<String>(Api.CHECK_MOBILE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val verifyPhoneNumBean: VerifyPhoneNumBean? =
                        checkResponseSuccess(response, VerifyPhoneNumBean::class.java)
                    if (verifyPhoneNumBean == null) {
                        isCheckSms = false
                        return
                    }
                    if (verifyPhoneNumBean.hasRegisted) {
                        isCheckSms = false
                        ToastUtils.showShort("phone num has registed.")
                        return
                    }
                    requestSendSms()
                }

                override fun onError(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    isCheckSms = false
                    super.onError(response)
                    Log.e(TAG, "verify phone num data error")
                    ToastUtils.showShort("verify phone num data error .")
                }
            })
    }

    //申请发送短信
    private fun requestSendSms() {
        isCheckSms = true
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            var realNum = mPhoneNum
            if (mPhoneNum!!.startsWith("0")){
                realNum = mPhoneNum!!.substring(1, mPhoneNum!!.length)
            }
            jsonObject.put("mobile", mPrex + realNum)
            //“1”:注册，“2”：修改密码 3 设备更换
            jsonObject.put("captchaType", if (mIsModify) "2" else "1")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        flLoading?.visibility = View.VISIBLE
        OkGo.post<String>(Api.GET_SMS_CODE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
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
                        isCheckSms = false
                        return
                    }
                    ToastUtils.showShort(" send sms success.")
                    verifyCodeView?.clearAll()
                    mHandler?.sendEmptyMessage(TYPE_TIME_REDUCE)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    isCheckSms = false
                    Log.e(TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }

    private fun checkVerifySmsCode(needTip : Boolean){
        if (TextUtils.isEmpty(mPhoneNum)) {
            if (needTip) {
                ToastUtils.showShort(" phone num is null")
                mEtPhoneNum?.setSelection(0)
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
        FirebaseUtils.logEvent("fireb_register_start")
        requestVerifySmsCode(mPhoneNum, verifyCode)
    }

    private fun requestVerifySmsCode(mPhoneNum: String?, verifyCode: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", mPrex + mPhoneNum)
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
                    val baseResponseBean: VerifySmsCodeBean? =
                        checkResponseSuccess(response, VerifySmsCodeBean::class.java)
                    if (baseResponseBean == null) {
                        return
                    }
                    if (!baseResponseBean.verifyed) {
                        ToastUtils.showShort(resources.getString(R.string.check_sms_code_verify_failure))
                        return
                    }
                    verifySuccess()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }

    private fun verifySuccess(){
        if (activity is SignUpActivity) {
            var signUpActivity : SignUpActivity = activity as SignUpActivity
            signUpActivity.toSetPwdPage(mPhoneNum!!, mPrex!!, mIsModify)
        }
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}