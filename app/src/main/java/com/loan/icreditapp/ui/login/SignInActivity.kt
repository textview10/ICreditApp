package com.loan.icreditapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.ui.home.MainActivity
import com.loan.icreditapp.ui.launcher.SplashActivity
import com.loan.icreditapp.ui.login.fragment.SignInFragment
import com.loan.icreditapp.ui.login.fragment.SignUpFragment

class SignInActivity : BaseActivity(){

    companion object {
        fun startActivity(context: Context) {
            var intent: Intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siginin)
        initView()
        initData()
    }

    private fun initView() {
        var ivBack: ImageView = findViewById(R.id.iv_signin_back)
        var tvTitle: TextView = findViewById(R.id.tv_signin_title)

        ivBack.setOnClickListener {
            var intent = Intent(this@SignInActivity, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initData(){
        var fragment = SignInFragment()
        toFragment(fragment)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_signin_container
    }

    fun toHomePage() {
        var intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}