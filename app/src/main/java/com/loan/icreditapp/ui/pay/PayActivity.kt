package com.loan.icreditapp.ui.pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity

class PayActivity : BaseActivity() {

    private var mAmount: String? = null
    private var mOrderId: String? = null

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
        var payFragment = PayFragment()
        toFragment(payFragment)
    }

    private fun handleIntent(){
        mAmount = intent.getStringExtra(EXTRA_AMOUNT)
        mOrderId = intent.getStringExtra(EXTRA_ORDER_ID)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_container_pay
    }
}