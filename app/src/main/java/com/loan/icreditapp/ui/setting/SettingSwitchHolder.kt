package com.loan.icreditapp.ui.setting

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class SettingSwitchHolder : RecyclerView.ViewHolder {

    var tvTitle : AppCompatTextView? = null
    var switch : SwitchCompat? = null


    constructor(itemView : View) : super(itemView) {
        tvTitle = itemView.findViewById(R.id.tv_switch_container)
        switch =  itemView.findViewById(R.id.switch_setting_item)

    }
}