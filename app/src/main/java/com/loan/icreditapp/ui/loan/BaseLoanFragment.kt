package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.data.FirebaseData
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayActivity
import com.lzy.okgo.OkGo

abstract class BaseLoanFragment : BaseFragment() {

    private val TAG = "base_loan_fragment"

    var mOrderInfo : OrderInfoBean? = null

    private var tvDisburseDate: AppCompatTextView? = null
    private var tvDueDate: AppCompatTextView? = null
    private var tvLoanAMount: AppCompatTextView? = null
    private var tvOriginFee: AppCompatTextView? = null
    private var tvRolloverFee: AppCompatTextView? = null
    private var tvOverdueFee: AppCompatTextView? = null
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
        tvOverdueFee = view.findViewById(R.id.tv_loan_process_loan_overdue_fee)
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

                tvLoanAMount?.text = "??? " + stage.amount.toString()
                tvOriginFee?.text = "??? " + stage.fee.toString()
                tvRolloverFee?.text = "??? " + stage.interest.toString()
                if (tvOverdueFee != null) {
                    tvOverdueFee?.text = "??? " + stage.penalty.toString()
                }
            }
        }
        tvTotalAmountDue?.text = "??? " + mOrderInfo?.totalAmount.toString()
    }

    //????????????
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

    fun checkNeedShowLog() : Boolean{
        if (mOrderInfo == null){
            return false
        }
        var orderId : String? = mOrderInfo!!.orderId
        var dataStr = SPUtils.getInstance().getString(Constant.KEY_FIREBASE_DATA)
        if (!TextUtils.isEmpty(dataStr)){
            var firebaseData = GsonUtils.fromJson(dataStr, FirebaseData::class.java)
            if (firebaseData != null){
                if (TextUtils.equals(firebaseData.orderId, orderId)) {
                    if (firebaseData.status == 1){
                        SPUtils.getInstance().put(Constant.KEY_FIREBASE_DATA, "")
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}