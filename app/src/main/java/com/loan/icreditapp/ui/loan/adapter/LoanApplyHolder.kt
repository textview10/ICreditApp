package com.loan.icreditapp.ui.loan.adapter

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class LoanApplyHolder : RecyclerView.ViewHolder {

    var tvTitle: AppCompatTextView? = null
    var tvOrigin: AppCompatTextView? = null
    var tvAmount: AppCompatTextView? = null
    var tvPurpose: AppCompatTextView? = null

    constructor(itemView : View) : super(itemView) {
        tvTitle = itemView.findViewById(R.id.tv_item_loan_apply_title)
        tvOrigin = itemView.findViewById(R.id.tv_item_loan_apply_origin_fee)
        tvAmount = itemView.findViewById(R.id.tv_item_loan_apply_amount_fee)
        tvPurpose = itemView.findViewById(R.id.tv_item_loan_apply_loan_purpose)
    }
}