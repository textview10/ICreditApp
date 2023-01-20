package com.loan.icreditapp.ui.card

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class CardListHolder : RecyclerView.ViewHolder {

    var flBg : FrameLayout? = null
    var tvBankName : AppCompatTextView? = null
    var tvBankNum : AppCompatTextView? = null

    constructor(itemView: View) : super(itemView) {
        flBg =  itemView.findViewById(R.id.fl_item_bank_card_bg)
        tvBankName =  itemView.findViewById(R.id.tv_card_bank_name)
        tvBankNum =  itemView.findViewById(R.id.tv_card_bank_num)
    }
}