package com.loan.icreditapp.dialog.selectdata

import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R

class SelectDataAdapter : RecyclerView.Adapter<SelectDataHolder> {


    private var mLists: ArrayList<Pair<String, String>>? = null
    private var mItemClickListener: OnItemClickListener? = null

    constructor(lists: ArrayList<Pair<String, String>>?){
        mLists = lists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectDataHolder {
        return SelectDataHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SelectDataHolder, position: Int) {
        val pair = mLists!![position]
        holder.tvSelectData!!.text = pair.first
        holder.itemView.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(pair, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mLists == null) 0 else mLists!!.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(content: Pair<String, String>?, pos: Int)
    }
}