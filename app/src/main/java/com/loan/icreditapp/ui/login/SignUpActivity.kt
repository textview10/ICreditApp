package com.loan.icreditapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.ui.home.MainActivity
import com.loan.icreditapp.ui.launcher.WelcomeActivity
import com.loan.icreditapp.ui.login.fragment.SetPwdFragment
import com.loan.icreditapp.ui.login.fragment.SignUpFragment
import com.loan.icreditapp.ui.webview.WebViewFragment

class SignUpActivity : BaseActivity() {

    private val TAG = "SignUpActivity"
    private var mMode = -1
    private var mPhoneNum : String? = ""
    private var flWebView : FrameLayout? = null

    private var webViewFragment : WebViewFragment? = null

    companion object {
        private val SIGNUP_KEY = "signup_key"
        private val SIGNUP_MODIFY_PHONE_NUM_KEY = "signup_modify_phonenum"
        val SIGNUP_NEW = 111
        val SIGNUP_MODIFY = 112
        fun startActivity(context: Context, mode: Int) {
            var intent: Intent = Intent(context, SignUpActivity::class.java)
            intent.putExtra(SIGNUP_KEY, mode)
            context.startActivity(intent)

        }

        fun startActivity(context: Context, mode: Int, phoneNum: String) {
            var intent: Intent = Intent(context, SignUpActivity::class.java)
            intent.putExtra(SIGNUP_KEY, mode)
            intent.putExtra(SIGNUP_MODIFY_PHONE_NUM_KEY, phoneNum)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mMode = intent.getIntExtra(SIGNUP_KEY, -1)
        mPhoneNum = intent.getStringExtra(SIGNUP_MODIFY_PHONE_NUM_KEY)
        Log.d(TAG, " mode = " + mMode + " phoneNum = " +  mPhoneNum)
        initData()
        initView()
    }

    private fun initData() {
        if (mMode == SIGNUP_NEW) {
            var fragment = SignUpFragment()
            toFragment(fragment)
        } else if (mMode == SIGNUP_MODIFY){
            toSignUpModify()
        }
    }

   fun toSignUpModify(){
       var fragment = SignUpFragment()
       fragment.setIsModify(mPhoneNum!!)
       toFragment(fragment)
    }

    fun toSetPwdPage(phoneNum: String, isModify : Boolean) {
        var setPwdPage = SetPwdFragment()
        setPwdPage.setPhoneNum(phoneNum, isModify)
        toFragment(setPwdPage)
    }

    fun toHomePage() {
        var intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initView() {
        var ivBack: ImageView = findViewById(R.id.iv_signup_back)
        var tvTitle: TextView = findViewById(R.id.tv_signin_title)
        flWebView = findViewById(R.id.fl_signup_webview)

        ivBack.setOnClickListener {
            var intent: Intent = Intent(this@SignUpActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun toWebView(){
        flWebView?.visibility = View.VISIBLE
        if (webViewFragment == null) {
            webViewFragment = WebViewFragment()
        }
        webViewFragment?.setUrl(Api.GET_POLICY)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_signup_webview, webViewFragment!!)
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        backPress()
    }

    fun backPress() {
        if (flWebView != null) {
            if (flWebView!!.visibility == View.VISIBLE) {
                flWebView!!.visibility = View.GONE
                return
            }
        }
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

    }



    override fun getFragmentContainerRes(): Int {
        return R.id.fl_signup_container
    }
}