package com.loan.icreditapp.ui.loan.adapter

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class LoanApplyHolder : RecyclerView.ViewHolder {

    var tvTitle: AppCompatTextView? = null
    var tvOriginAmount: AppCompatTextView? = null
    var tvOrigin: AppCompatTextView? = null
    var tvAmount: AppCompatTextView? = null
    var tvPurpose: AppCompatTextView? = null
    var tvInterest: AppCompatTextView? = null

    constructor(itemView : View) : super(itemView) {
        tvTitle = itemView.findViewById(R.id.tv_item_loan_apply_title)
        tvOriginAmount = itemView.findViewById(R.id.tv_item_loan_apply_amount)
        tvOrigin = itemView.findViewById(R.id.tv_item_loan_apply_origin_fee)
        tvInterest = itemView.findViewById(R.id.tv_item_loan_apply_interest)
        tvAmount = itemView.findViewById(R.id.tv_item_loan_apply_amount_fee)
        tvPurpose = itemView.findViewById(R.id.tv_item_loan_apply_loan_purpose)
    }
}