package com.loan.icreditapp.ui.pay.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.bank.CardResponseBean

class ChoosePayAdapter(val list: ArrayList<ChoosePayData>) : RecyclerView.Adapter<ChoosePayAdapter.ChoosePayHolder>() {
    private var mItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoosePayHolder {
        return ChoosePayHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_pay, parent, false))
    }

    override fun onBindViewHolder(holder: ChoosePayHolder, position: Int) {
        val data = list[position]
        Glide.with(holder.itemView.context).load(data.icon).into( holder.icIcon)
        holder.tvTitle.text = data.title
        if (TextUtils.isEmpty(data.desc)) {
            holder.tvDesc.visibility = View.GONE
        } else {
            holder.tvDesc.visibility = View.VISIBLE
            holder.tvDesc.text = data.desc
        }

        holder.itemView.setOnClickListener{
            mItemClickListener?.onItemClick(data, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ChoosePayHolder(itemView: View) : ViewHolder(itemView) {
        var icIcon : AppCompatImageView
        var tvTitle : AppCompatTextView
        var tvDesc : AppCompatTextView

        init {
            icIcon = itemView.findViewById(R.id.iv_item_choose_pay_icon)
            tvTitle = itemView.findViewById(R.id.tv_item_choose_pay_title)
            tvDesc = itemView.findViewById(R.id.tv_item_choose_pay_desc)

        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bank: ChoosePayData, pos: Int)
    }
}