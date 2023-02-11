package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.loan.RepayLoanResponseBean
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

abstract class BaseLoanFragment : BaseFragment() {

    private val TAG = "base_loan_fragment"

    var mOrderInfo : OrderInfoBean? = null

    private var tvDisburseDate: AppCompatTextView? = null
    private var tvDueDate: AppCompatTextView? = null
    private var tvLoanAMount: AppCompatTextView? = null
    private var tvOriginFee: AppCompatTextView? = null
    private var tvRolloverFee: AppCompatTextView? = null
    private var tvTotalAmountDue: AppCompatTextView? = null

    open fun setOrderInfo(orderInfoBean: OrderInfoBean) {
        mOrderInfo = orderInfoBean
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDisburseDate = view.findViewById(R.id.tv_loan_process_disburse_date)
        tvDueDate = view.findViewById(R.id.tv_loan_process_due_date)
        tvLoanAMount = view.findViewById(R.id.tv_loan_process_loan_amount)
        tvOriginFee = view.findViewById(R.id.tv_loan_process_loan_origin_fee)
        tvRolloverFee = view.findViewById(R.id.tv_loan_process_loan_fee)
        tvTotalAmountDue = view.findViewById(R.id.tv_loan_process_loan_total_amount_due)
        bindDataInternal()
    }

    private fun bindDataInternal() {
        if (mOrderInfo == null) {
            return
        }
        if (mOrderInfo?.stageList != null) {
            var stage: OrderInfoBean.Stage? = mOrderInfo?.stageList!![0]
            if (stage != null){
                tvDisburseDate?.text = stage.disburseTime.toString()
                tvDueDate?.text =  stage.repayDate.toString()

                tvLoanAMount?.text = "₦ " + stage.amount.toString()
                tvOriginFee?.text = "₦ " + stage.fee.toString()
                tvRolloverFee?.text = "₦ " + stage.interest.toString()
            }
        }
        tvTotalAmountDue?.text = "₦ " + mOrderInfo?.totalAmount.toString()
    }

    //点击还款
    fun clickRepayLoad(){
        if (checkClickFast()){
            return
        }
        if (mOrderInfo == null){
            ToastUtils.showShort("repay load failure .")
            return
        }
        if (TextUtils.isEmpty(mOrderInfo?.orderId)){
            ToastUtils.showShort("repay load orderId == null .")
            return
        }
        if (mOrderInfo?.totalAmount == null){
            ToastUtils.showShort("repay load total amount == null .")
            return
        }
        var amount:String? = null
        if (mOrderInfo?.stageList != null) {
            var stage: OrderInfoBean.Stage? = mOrderInfo?.stageList!![0]
            if (stage != null) {
                amount = stage.amount.toString()
            }
        }
        PayActivity.showMe(requireContext(), mOrderInfo?.orderId!!, amount!!)
//        if (!TextUtils.isEmpty(amount)) {
//            uploadReplayLoad(mOrderInfo?.orderId!!, amount!!)
//        }
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}