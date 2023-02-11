package com.loan.icreditapp.ui.pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.ui.launcher.WelcomeActivity
import com.loan.icreditapp.ui.webview.WebViewFragment

class PayActivity : BaseActivity() {

    private var mAmount: String? = null
    private var mOrderId: String? = null

    private var webViewFragment : WebViewFragment? = null
    private var payFragment : PayFragment? = null
    private var flWebView: FrameLayout? = null
    private var ivBack: ImageView? = null

    companion object {
        const val EXTRA_ORDER_ID = "extra_order_id"
        const val EXTRA_AMOUNT = "extra_amount"

        fun showMe(context: Context, orderId : String, amount : String){
            var intent = Intent(context, PayActivity::class.java)
            intent.putExtra(EXTRA_AMOUNT, amount)
            intent.putExtra(EXTRA_ORDER_ID, orderId)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_pay)

        handleIntent()
        initView()
        payFragment = PayFragment()
        webViewFragment = WebViewFragment()
        payFragment?.setData(mOrderId, mAmount)
        toFragment(payFragment)
    }

    private fun handleIntent(){
        mAmount = intent.getStringExtra(EXTRA_AMOUNT)
        mOrderId = intent.getStringExtra(EXTRA_ORDER_ID)
    }

    private fun initView() {
        flWebView = findViewById(R.id.fl_pay_webview_container)
        ivBack = findViewById(R.id.iv_pay_back)
        ivBack?.setOnClickListener(View.OnClickListener {
            backPressInternal()
        })
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_pay_container
    }

    fun toWebView(url : String){
        flWebView?.visibility = View.VISIBLE
        if (webViewFragment == null) {
            webViewFragment = WebViewFragment()
        }
        webViewFragment?.setUrl(url)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_pay_webview_container, webViewFragment!!)
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        backPressInternal()
    }

    fun backPressInternal() {
        if (flWebView != null) {
            if (flWebView!!.visibility == View.VISIBLE) {
                flWebView!!.visibility = View.GONE
                payFragment?.onWebViewEnd()
                return
            }
        }
//        val intent = Intent(this, WelcomeActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left_my, R.anim.slide_out_right_my)
        finish()
    }

}