package com.loan.icreditapp.ui.login

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.ui.login2.Login2Fragment
import com.loan.icreditapp.ui.login2.LoginOtpFragment
import com.loan.icreditapp.ui.webview.WebViewFragment

class Login2Activity : BaseActivity() {
    private var ivBack : AppCompatImageView? = null
    private var llWebView : LinearLayout? = null

    private var webViewFragment : WebViewFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarLightMode(this@Login2Activity, true)
        BarUtils.setStatusBarColor(this@Login2Activity, android.graphics.Color.TRANSPARENT)
        setContentView(R.layout.activity_login2)

        ivBack = findViewById(R.id.iv_login2_back)
        llWebView = findViewById(R.id.ll_login2_webview)

        ivBack?.setOnClickListener{
            backPressInternal()
        }

        toLoginFragment()
    }

    fun toOtpFragment(prex : String ,phoneNum : String){
        val loginOtp = LoginOtpFragment()
        loginOtp.setPhoneNum(prex, phoneNum)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.add(R.id.fl_login2_container, loginOtp, LoginOtpFragment.TAG)
        transaction.commitAllowingStateLoss()
    }

    fun toLoginFragment() {
        var login2Fragment = supportFragmentManager.findFragmentByTag(Login2Fragment.TAG)
        if (login2Fragment == null) {
            login2Fragment = Login2Fragment()
        }
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_login2_container, login2Fragment, Login2Fragment.TAG)
        transaction.commitAllowingStateLoss()
    }

    fun toWebView(url : String){
        llWebView?.visibility = View.VISIBLE
        if (webViewFragment == null) {
            webViewFragment = WebViewFragment()
        }
        webViewFragment?.setUrl(url)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_login2_webview, webViewFragment!!)
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        backPressInternal()
    }

   private fun backPressInternal() {
        if (llWebView != null) {
            if (llWebView!!.visibility == View.VISIBLE) {
                llWebView!!.visibility = View.GONE
                return
            }
        }
//        val intent = Intent(this, WelcomeActivity::class.java)
//        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left_my, R.anim.slide_out_right_my)
        finish()
    }
}