package com.loan.icreditapp.dialog.contact

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class SettingContactHolder : RecyclerView.ViewHolder {


    var tvContact1: TextView? = null
    var tvContact2: TextView? = null
    var tvContactBg: TextView? = null
    var ivContact: ImageView? = null

    constructor(itemView: View) : super(itemView) {
        ivContact = itemView.findViewById(R.id.iv_setting_contact)
        tvContact1 = itemView.findViewById(R.id.tv_setting_contact_1)
        tvContact2 = itemView.findViewById(R.id.tv_setting_contact_2)
        tvContactBg = itemView.findViewById(R.id.tv_setting_contact_bg)
    }

}