package com.loan.icreditapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.ui.launcher.SplashActivity

class SignUpActivity : BaseActivity() {

    private val TAG = "SignUpActivity"
    private var mMode = -1
    companion object {
        private val SIGNUP_KEY = "signup_key"
        val SIGNUP_1 = 111
        fun startActivity(context : Context, mode : Int){
            var intent : Intent = Intent(context, SignUpActivity::class.java)
            intent.putExtra(SIGNUP_KEY, mode)
            context.startActivity(intent)

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mMode = intent.getIntExtra(SIGNUP_KEY, -1)
        Log.d(TAG, " mode = " + mMode)
        initData()
        initView()
    }

    private fun initData() {
        if (mMode == SIGNUP_1) {
            var fragment = SignUpFragment()
            toFragment(fragment)
        }
    }

    open fun toSetPwdPage() {
        var setPwdPage = SetPwdFragment()
        toFragment(setPwdPage)
    }

    private fun initView() {
        var ivBack:ImageView = findViewById(R.id.iv_signup_back)
        var tvTitle:TextView = findViewById(R.id.tv_signin_title)

        ivBack.setOnClickListener {
            var intent : Intent = Intent(this@SignUpActivity, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun getFragmentContainerRes(): Int{
        return R.id.fl_signup_container
    }
}