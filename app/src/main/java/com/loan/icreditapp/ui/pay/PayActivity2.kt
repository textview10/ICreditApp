package com.loan.icreditapp.ui.pay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blankj.utilcode.util.BarUtils
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.google.gson.Gson
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.ui.webview.WebViewFragment
import net.entity.bean.FlutterWaveResult
import org.greenrobot.eventbus.EventBus

class PayActivity2 : BaseActivity() {

    private var mAmount: String? = null
    private var mOrderId: String? = null

    private var webViewFragment: WebViewFragment? = null
    private var payFragment: ChoosePayFragment? = null
    private var flWebView: FrameLayout? = null
    private var rlTopContainer: RelativeLayout? = null
    private var ivBack: ImageView? = null

    companion object {
        private const val TAG = "PayActivity2"

        const val EXTRA_ORDER_ID = "extra_order_id"
        const val EXTRA_AMOUNT = "extra_amount"

        const val RESULT_CODE_SELECT_BANK_TRANFER = 1112

        const val REQUEST_CODE_TO_PAY = 2111
        fun launchPayActivity(context: Activity, orderId: String, amount: String) {
            var intent = Intent(context, PayActivity2::class.java)
            intent.putExtra(EXTRA_AMOUNT, amount)
            intent.putExtra(EXTRA_ORDER_ID, orderId)
            context.startActivityForResult(intent, REQUEST_CODE_TO_PAY)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(android.R.color.transparent))
        BarUtils.setNavBarLightMode(this, false)
        setContentView(R.layout.activity_pay2)

        handleIntent()
        initView()
        payFragment = ChoosePayFragment()
        webViewFragment = WebViewFragment()
        payFragment?.setData(mOrderId, mAmount)
        toFragment(payFragment)
    }

    private fun handleIntent() {
        mAmount = intent.getStringExtra(EXTRA_AMOUNT)
        mOrderId = intent.getStringExtra(EXTRA_ORDER_ID)
    }

    private fun initView() {
        flWebView = findViewById(R.id.fl_pay_webview_container)
        rlTopContainer = findViewById(R.id.rl_top_container)
        ivBack = findViewById(R.id.iv_pay_back)
        ivBack?.setOnClickListener(View.OnClickListener {
            backPressInternal()
        })
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_pay_container
    }

    fun toWebView(url: String) {
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

    fun payStackSuccess(){
        if (flWebView != null) {
            if (flWebView!!.visibility == View.VISIBLE) {
                flWebView!!.visibility = View.GONE
                payFragment?.onWebViewEnd()
            }
        }
    }

    fun backPressInternal() {
        if (flWebView != null) {
            if (flWebView!!.visibility == View.VISIBLE) {
                flWebView!!.visibility = View.GONE
                payFragment?.onWebViewEnd()
                return
            }
        }
        EventBus.getDefault().post(UpdateLoanEvent())
//        val intent = Intent(this, WelcomeActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left_my, R.anim.slide_out_right_my)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            var mJsonString = data.getStringExtra("response")
            val bean = Gson().fromJson(
                mJsonString, FlutterWaveResult::class.java
            )
            var resultFlag : Boolean = false
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                resultFlag = true
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {

            }
            payFragment?.onFlutterWaveResult(resultFlag, bean)
        } else if (requestCode == PayBankListActivity.TO_PAYBANK_LIST_RESULT) {
            payFragment?.onUpdateBindCard()
        }
    }
}