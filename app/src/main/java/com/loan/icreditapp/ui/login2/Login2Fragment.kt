package com.loan.icreditapp.ui.login2

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
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.login.VerifyPhoneNumBean
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
    }

    private var etSignIn : AppCompatEditText? = null
    private var tvCommit : AppCompatTextView? = null
    private var mSpinner : Spinner? = null
    private var ivClear : AppCompatImageView? = null
    private var ivCheckState : AppCompatImageView? = null
    private var flLoading : FrameLayout? = null

    private var mPresenter: PhoneNumPresenter? = null

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

        initView()
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
        tvCommit?.setOnClickListener {
            checkMobile()
        }
        ivClear?.setOnClickListener {
            etSignIn?.setText("")
            etSignIn?.setSelection(0)
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
        FirebaseUtils.logEvent("fireb_click_sign")
        requestSendSms(phoneNum)
    }

    //申请发送短信
    private fun requestSendSms(phoneNum : String) {
        flLoading?.visibility = View.VISIBLE
        tvCommit?.isEnabled = false
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        var finalPhoneNum = phoneNum
        try {
            var mPrex: String? = mPresenter?.getSelectString(0)
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
                        (activity as Login2Activity).toOtpFragment(finalPhoneNum)
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
        super.onDestroy()
    }
}