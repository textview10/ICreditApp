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
import com.loan.icreditapp.bean.loan.TrialResponseBean
import com.loan.icreditapp.dialog.order.OrderInfoBean

class LoanApplyAdapter : RecyclerView.Adapter<LoanApplyHolder> {

    private var mList: ArrayList<TrialResponseBean.Trial>? = null

    private var mListener: OnItemClickListener? = null


    constructor(list: ArrayList<TrialResponseBean.Trial>){
        mList = list
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanApplyHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_apply_detail, parent, false)
        return LoanApplyHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: LoanApplyHolder, position: Int) {
        val trial: TrialResponseBean.Trial = mList!![position]

        holder.tvTitle?.text = getTitleByPos(position)

        if (!TextUtils.isEmpty(trial.disburseAmount)) {
            holder.tvOrigin?.text = trial.disburseAmount
        }
        if (!TextUtils.isEmpty(trial.totalAmount)) {
            holder.tvAmount?.text = trial.totalAmount
        }
        if (!TextUtils.isEmpty(trial.repayDate)) {
            holder.tvPurpose?.text = trial.repayDate
        }
    }

    private fun getTitleByPos(pos : Int) : String{
        if (mList != null && mList!!.size == 1){
            return "Bills details"
        }
        if (pos == 0){
            return "1st Installment"
        } else {
            return "2st Installment"
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

}