package com.loan.icreditapp.ui.login

import android.graphics.Color
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
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.login.VerifyPhoneNumBean
import com.loan.icreditapp.presenter.PhoneNumPresenter
import com.loan.icreditapp.ui.widget.InputVerifyCodeView
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class SignUpFragment : BaseFragment() {

    private val TAG = "SignUpFragment"

    private var mSpinner: Spinner? = null
    private var mEtPhoneNum: AppCompatEditText? = null
    private var tvPhoneNum: AppCompatTextView? = null
    private var tvVerifyCode: AppCompatTextView? = null
    private var tvResend: AppCompatTextView? = null
    private var ivClear: ImageView? = null
    private var verifyCodeView: InputVerifyCodeView? = null

    private var mPresenter: PhoneNumPresenter? = null
    private var mPhoneNum: String? = null

    private val TYPE_TIME_REDUCE = 1111
    private val MAX_TIME = 60
    private var mCurTime: Int = MAX_TIME

    private var mHandler: Handler? = null

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

        ivClear = view.findViewById(R.id.iv_signup_phonenum_clear)
        verifyCodeView = view.findViewById(R.id.view_input_verify_code_verify_code)

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
        ivClear?.setOnClickListener {
            mEtPhoneNum?.setText("")
            mEtPhoneNum?.setSelection(0)
        }

        verifyCodeView?.setObserver(object : InputVerifyCodeView.Observer {
            override fun onEnd() {
                if (verifyCodeView != null) {
                    val verifyCode: String? = verifyCodeView?.getVerifyCode()
                    if (TextUtils.isEmpty(verifyCode)) {
                        ToastUtils.showShort("verify code = null. please input again.")
                        return
                    }
                    requestVerifySmsCode(mPhoneNum, verifyCode)
                }
            }

            override fun onFocusChange(hasFocus: Boolean) {
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
//            mEtPhoneNum?.setText("")
//            mEtPhoneNum?.setSelection(0)
            checkAndVerifyPhoneNum()
//            requestSendSms()
        }
        initView()
    }

    private fun initView() {
        mPresenter = PhoneNumPresenter(context)
        mPresenter?.initSpinner(mSpinner!!)
    }

    private fun checkAndVerifyPhoneNum(){
        if (mCurTime != MAX_TIME) {
            ToastUtils.showShort("please click later")
            return
        }
        mPhoneNum = mEtPhoneNum?.text.toString()
        if (TextUtils.isEmpty(mPhoneNum)) {
            ToastUtils.showShort(" phone num is null")
            mEtPhoneNum?.setSelection(0)
            return
        }
        val prefix: String? = mPresenter?.getSelectString(mSpinner?.getSelectedItemPosition()!!)
        mPhoneNum = prefix + mPhoneNum
        requestCheckPhoneNum()
    }

    private fun requestCheckPhoneNum() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", mPhoneNum)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d(TAG, "requestCheckPhoneNum = " + jsonObject.toString());
        OkGo.post<String>(Api.CHECK_MOBILE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val verifyPhoneNumBean: VerifyPhoneNumBean? =
                        checkResponseSuccess(response, VerifyPhoneNumBean::class.java)
                    if (verifyPhoneNumBean == null) {
                        ToastUtils.showShort("verify phone num data = null.")
                        return
                    }
                    if (verifyPhoneNumBean.hasRegisted) {
                        ToastUtils.showShort("phone num has registed.")
                        return
                    }
                    requestSendSms()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "verify phone num data error")
                    ToastUtils.showShort("verify phone num data error .")
                }
            })
    }

    //申请发送短信
    private fun requestSendSms() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", mPhoneNum)
            //“1”:注册，“2”：修改密码 3 设备更换
            jsonObject.put("captchaType", "1")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        OkGo.post<String>(Api.GET_SMS_CODE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val baseResponseBean: BaseResponseBean? =
                        checkResponseSuccess(response, BaseResponseBean::class.java)
                    if (baseResponseBean == null) {
                        ToastUtils.showShort("request send sms failure.")
                        return
                    }
                    if (!baseResponseBean.isRequestSuccess()) {
                        ToastUtils.showShort("request send sms failure 2.")
                        return
                    }
                    ToastUtils.showShort(" send sms success.")
                    verifyCodeView?.clearAll()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }

    private fun checkVerifySmsCode(){
        if (mCurTime == 0) {
            ToastUtils.showShort("please click later")
            return
        }
        if (TextUtils.isEmpty(mPhoneNum)) {
            ToastUtils.showShort(" phone num is null")
            mEtPhoneNum?.setSelection(0)
            return
        }
        var verifyCode = verifyCodeView?.verifyCode
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtils.showShort(" phone num is null")
//            verifyCodeView?.clearAll()
            return
        }
        requestVerifySmsCode(mPhoneNum, verifyCode)
    }

    private fun requestVerifySmsCode(mPhoneNum: String?, verifyCode: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("mobile", mPhoneNum)
            jsonObject.put("captchaCode", verifyCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_SMS_CODE).tag(TAG)
            .params("data", jsonObject.toString()) //                .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val baseResponseBean: BaseResponseBean? =
                        checkResponseSuccess(response, BaseResponseBean::class.java)
                    if (baseResponseBean == null) {
                        ToastUtils.showShort("request send sms failure.")
                        return
                    }
                    if (!baseResponseBean.isRequestSuccess()) {
                        ToastUtils.showShort("request send sms failure 2.")
                        return
                    }
                    ToastUtils.showShort(" send sms success.")
                    verifyCodeView?.clearAll()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "request send sms error")
                    ToastUtils.showShort("request send sms error ...")
                }
            })
    }
}