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
import com.loan.icreditapp.ui.login.SignInActivity
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

        val httpHeaders = BuildRequestJsonUtils.buildHeadersNonLogin()
        OkGo.getInstance().addCommonHeaders(httpHeaders)
    }

    private fun initializeView() {
        tvRegister =  findViewById(R.id.tv_splash_register)
        tvSignIn =  findViewById(R.id.tv_splash_signin)

        tvRegister?.setOnClickListener(View.OnClickListener {
           SignUpActivity.startActivity(this@SplashActivity, SignUpActivity.SIGNUP_1)
            finish()
        })
        tvSignIn?.setOnClickListener(View.OnClickListener {
            SignInActivity.startActivity(this@SplashActivity)
            finish()
        })
    }


}