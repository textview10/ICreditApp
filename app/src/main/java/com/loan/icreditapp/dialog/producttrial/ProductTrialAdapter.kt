package com.loan.icreditapp.dialog.producttrial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.loan.TrialResponseBean

class ProductTrialAdapter : RecyclerView.Adapter<ProductTrialHolder> {

    private var mList: List<TrialResponseBean.Trial>? = null

    constructor(list: List<TrialResponseBean.Trial>?) {
        mList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductTrialHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.dialog_product_trial, parent, false)
        return ProductTrialHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    override fun onBindViewHolder(holder: ProductTrialHolder, position: Int) {
        val trial: TrialResponseBean.Trial = mList!![position]
        if (trial != null) {
            holder.tvAmount?.text = trial.totalAmount
            holder.tvRepayDate?.text = trial.repayDate
            holder.flSubmit?.setOnClickListener(View.OnClickListener {

            })
            holder.flCancel?.setOnClickListener(View.OnClickListener {

            })
        }
    }

}