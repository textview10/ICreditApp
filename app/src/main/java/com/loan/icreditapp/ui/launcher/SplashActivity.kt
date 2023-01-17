package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.ui.login.SignUpActivity

class SplashActivity : BaseActivity() {

    private var tvRegister : AppCompatTextView? = null
    private var tvSignIn : AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(android.R.color.transparent))
        BarUtils.setNavBarLightMode(this,false)
        setContentView(R.layout.activity_splash)
        initializeView()
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