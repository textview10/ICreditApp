package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.dialog.order.OrderInfoBean

abstract class BaseLoanFragment : BaseFragment() {
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

                tvLoanAMount?.text = stage.amount.toString()
                tvOriginFee?.text = stage.fee.toString()
                tvRolloverFee?.text = stage.feePrePaid.toString()
            }
        }
    }
}