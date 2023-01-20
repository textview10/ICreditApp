package com.loan.icreditapp.ui.loan.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.loan.ProductResponseBean
import com.loan.icreditapp.dialog.order.OrderInfoBean

class LoanApplyAdapter : RecyclerView.Adapter<LoanApplyHolder> {

    private var mList: ArrayList<ProductResponseBean.Product>? = null

    private var mListener: OnItemClickListener? = null

    private var mSelectPos = 0

    constructor(list: ArrayList<ProductResponseBean.Product>){
        mList = list
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanApplyHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_apply_1, parent, false)
        return LoanApplyHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: LoanApplyHolder, position: Int) {
        val product: ProductResponseBean.Product = mList!![position]
        if (!TextUtils.isEmpty(product.amount)) {
            holder.tvAmount?.text = product.amount
        }
        holder.flBg?.setBackgroundResource(if (mSelectPos == position) R.drawable.shape_round_grey_10 else R.drawable.shape_round_white_10)
        @ColorRes val res: Int = if (mSelectPos == position) R.color.white else R.color.black
        holder.tvAmount!!.setTextColor(ColorUtils.getColor(res))
        holder.itemView.setOnClickListener { view: View? ->
            val oldPos = mSelectPos
            mSelectPos = position
            notifyItemChanged(oldPos)
            notifyItemChanged(mSelectPos)
            mListener?.onClick(product, position)
        }
    }


    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    fun setItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onClick(product: ProductResponseBean.Product, pos: Int)
    }

    fun setSelectPos(pos: Int) {
        mSelectPos = pos
    }

}