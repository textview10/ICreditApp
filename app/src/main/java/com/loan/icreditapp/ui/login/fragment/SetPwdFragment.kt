package com.loan.icreditapp.ui.login.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.login.RegisterBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class SetPwdFragment : BaseFragment() {

    private val TAG = "SetPwdFragment"

    private var flCommit: FrameLayout? = null
    private var etCreatePwd: EditTextContainer? = null
    private var etConfirmPwd: EditTextContainer? = null

    private var mPhoneNum: String? = null

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
            register(strPassCode2)
        }
    }

    fun setPhoneNum(phoneNum: String) {
        mPhoneNum = phoneNum
    }

    private fun register(pwd: String) {
        //password	String
        //mobile	String
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        jsonObject.put("mobile", mPhoneNum)
        jsonObject.put("password", pwd)
        OkGo.post<String>(Api.REGISTER).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val registerBean: RegisterBean? =
                        checkResponseSuccess(response, RegisterBean::class.java)
                    if (registerBean == null || TextUtils.isEmpty(registerBean.token)) {
                        ToastUtils.showShort("register failure.")
                        return
                    }
                    Constant.mToken = registerBean.token
                    if (activity is SignUpActivity) {
                        var signUpActivity : SignUpActivity = activity as SignUpActivity
                        signUpActivity.toHomePage()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, " register error ... ")
                    ToastUtils.showShort("register error.")
                }
            })
    }
}