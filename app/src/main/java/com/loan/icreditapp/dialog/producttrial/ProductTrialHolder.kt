package com.loan.icreditapp.dialog.producttrial

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class ProductTrialHolder : RecyclerView.ViewHolder {

    var tvRepayDate: AppCompatTextView? = null
    var tvAmount: AppCompatTextView? = null
    var flCancel: FrameLayout? = null
    var flSubmit: FrameLayout? = null

    constructor(itemView: View) : super(itemView) {
        tvRepayDate = itemView.findViewById(R.id.tv_product_trial_repay_date)
        tvRepayDate = itemView.findViewById(R.id.tv_product_trial_amount)
        flCancel = itemView.findViewById(R.id.fl_product_trial_cancel)
        flSubmit = itemView.findViewById(R.id.fl_product_trial_submit)
    }
}