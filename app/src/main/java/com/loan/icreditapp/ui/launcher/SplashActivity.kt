package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import org.json.JSONObject

class SplashActivity : BaseActivity() {

    private var tvRegister : AppCompatTextView? = null
    private var tvSignIn : AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(android.R.color.transparent))
        BarUtils.setNavBarLightMode(this,false)
        setContentView(R.layout.activity_splash)
        initializeView()

        val httpHeaders = BuildRequestJsonUtils.buildHttpHeadersNonPermission()
        OkGo.getInstance().addCommonHeaders(httpHeaders)
        login()
    }

    private fun requestUpdate() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        OkGo.post<String>(Api.UPDATE_DETAIL).tag("Test")
            .params("data", jsonObject.toString()) //                .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)

                }
            })
    }

    private fun login() {
        //password	String
        //mobile	String
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        jsonObject.put("mobile", "18518511461")
        jsonObject.put("password", "123456")
        OkGo.post<String>(Api.REGISTER).tag("Test")
            .params("data", jsonObject.toString()) //                .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)

                }
            })
    }

    private fun initializeView() {
        tvRegister =  findViewById(R.id.tv_splash_register)
        tvSignIn =  findViewById(R.id.tv_splash_signin)

        tvRegister?.setOnClickListener(View.OnClickListener {
           SignUpActivity.startActivity(this@SplashActivity, SignUpActivity.SIGNUP_1)
        })
        tvSignIn?.setOnClickListener(View.OnClickListener {

        })
    }


}