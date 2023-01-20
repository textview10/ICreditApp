package com.loan.icreditapp.ui.setting

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class SettingHolder : RecyclerView.ViewHolder {
    var llContainer : LinearLayout? = null
    var flContainer : FrameLayout? = null
    var ivLeftIcon : ImageView? = null
    var tvTitle : TextView? = null

    constructor(itemView :View) : super(itemView) {
        llContainer = itemView.findViewById(R.id.ll_setting_container)
        ivLeftIcon =  itemView.findViewById(R.id.iv_setting_left_icon)
        tvTitle =  itemView.findViewById(R.id.tv_setting_left_title)
        flContainer =  itemView.findViewById(R.id.fl_setting_container)
    }
}