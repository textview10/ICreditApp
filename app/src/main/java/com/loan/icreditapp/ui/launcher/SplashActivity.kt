package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity

class SplashActivity : BaseActivity() {

    private var tvRegister : AppCompatTextView? = null
    private var tvSignIn : AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initializeView()
    }

    private fun initializeView() {
        tvRegister =  findViewById(R.id.tv_splash_register)
        tvSignIn =  findViewById(R.id.tv_splash_signin)

        tvRegister?.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SplashActivity, SplashActivity::class.java))
            finish()
        })
        tvSignIn?.setOnClickListener(View.OnClickListener {

        })
    }
}