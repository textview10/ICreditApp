package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.DiscountAmountBean
import com.loan.icreditapp.event.RateUsEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.FirebaseUtils
import com.loan.icreditapp.util.MyAppUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

class LoanActiveFragment2 : BaseLoanFragment() {

    private val TAG = "LoanActiveFragment2"

    private var tvLoanRepay : AppCompatTextView? = null
    private var llBtnTop : LinearLayout? = null
    private var viewBottom : View? = null
    private var tvLoanPay : AppCompatTextView? = null
    private var tvTotalAmount: AppCompatTextView? = null

    private var flCommit: FrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_loan_active_2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvLoanRepay = view.findViewById(R.id.tv_discount_loan_repay)
        llBtnTop = view.findViewById(R.id.ll_loan_repay)
        viewBottom = view.findViewById(R.id.view_loan_repay)
        tvLoanPay = view.findViewById(R.id.tv_discount_loan_repay)
        flCommit = view.findViewById(R.id.fl_loan_paid_commit)
        tvTotalAmount = view.findViewById(R.id.tv_loan_paid_total_amount)

        tvTotalAmount?.text = mOrderInfo?.totalAmount.toString()
        if (checkNeedShowLog()){
            if (Constant.IS_FIRST_APPLY) {
                FirebaseUtils.logEvent( "fireb_activity")
            }
            FirebaseUtils.logEvent( "fireb_activity_all")
            EventBus.getDefault().post(RateUsEvent())
        }

        flCommit?.setOnClickListener {
            clickRepayLoad()
        }

        discountAmount()
    }

//    token	String	Y	header中的token必须传入
//    innerVersionCode	String	Y	header中的innerVersionCode必须传入
//    accountId	String	Y	客户ID
//    orderId	String	Y	订单ID
    private fun discountAmount() {
        if (mOrderInfo == null){
            return
        }
        val orderId = mOrderInfo!!.orderId
        if (TextUtils.isEmpty(orderId)){
            return
        }
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("token", Constant.mToken)
            jsonObject.put("innerVersionCode", MyAppUtils.getAppVersionCode())
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        Log.e(TAG, "111 id = " + Constant.mAccountId);
        OkGo.post<String>(Api.DISCOUNT_AMOUNT).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val discountAmountBean: DiscountAmountBean? =
                        checkResponseSuccess(response, DiscountAmountBean::class.java)
                    if (discountAmountBean == null) {
                        Log.e(TAG, " order info error ." + response.body())
                        return
                    }
                    updateLoanRepaying(discountAmountBean)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "order info failure = " + response.body())
                    }
                    ToastUtils.showShort("order info failure")
                }
            })
    }

    private fun updateLoanRepaying(bean: DiscountAmountBean) {
        if (TextUtils.equals(bean.discountSwitch, "0")) {
            //关闭
            llBtnTop?.visibility = View.GONE
            viewBottom?.visibility = View.GONE
        } else {
            llBtnTop?.visibility = View.VISIBLE
            viewBottom?.visibility = View.VISIBLE

            val discountAccountStr = resources.getString(R.string.discount_account)
            val discountAmount = String.format(discountAccountStr, bean.discountAmount)
            tvLoanPay?.text = discountAmount
        }
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}