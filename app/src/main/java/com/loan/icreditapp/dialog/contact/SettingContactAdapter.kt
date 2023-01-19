package com.loan.icreditapp.dialog.contact

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.profile.ContactBean

class SettingContactAdapter : RecyclerView.Adapter<SettingContactHolder> {
    private val TAG = "SettingContactAdapter"

    private var mList: ArrayList<ContactBean>? = null

    constructor(list: ArrayList<ContactBean>){
        mList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingContactHolder {
        return SettingContactHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_item_setting_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingContactHolder, position: Int) {
        val contactBean: ContactBean = mList!![position]
        holder.tvContact1?.setText(contactBean.contactName)
        holder.tvContact2?.setText(contactBean.number)
        if (TextUtils.isEmpty(contactBean.photoUri)) {
            holder.ivContact?.setVisibility(View.GONE)
            holder.tvContactBg?.setVisibility(View.VISIBLE)
            val str: String? = contactBean.contactName?.substring(0, 1)
            if (!TextUtils.isEmpty(str)) {
                holder.tvContactBg?.setText(str)
            }
        } else {
            holder.ivContact?.setVisibility(View.VISIBLE)
            holder.tvContactBg?.setVisibility(View.GONE)
            Glide.with(holder.ivContact!!).load(contactBean.photoUri).into(holder.ivContact!!)
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            if (mListener != null) {
                mListener!!.onClick(position)
            }
        })
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(pos: Int)
    }
}