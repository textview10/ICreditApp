package com.loan.icreditapp.ui.loan.adapter

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class LoanApplyHolder : RecyclerView.ViewHolder {

    var tvAmount: AppCompatTextView? = null
    var flBg: FrameLayout? = null

    constructor(itemView : View) : super(itemView) {
        tvAmount = itemView.findViewById(R.id.tv_item_load_apply_amount)
        flBg = itemView.findViewById(R.id.fl_item_load_apply_bg)
    }
}