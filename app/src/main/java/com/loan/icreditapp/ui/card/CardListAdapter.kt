package com.loan.icreditapp.ui.card

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.bank.CardResponseBean

class CardListAdapter : RecyclerView.Adapter<CardListHolder> {

    private var mLists: ArrayList<CardResponseBean.Bank>? = null
    private var mItemClickListener: OnItemClickListener? = null


    constructor(lists: ArrayList<CardResponseBean.Bank>?) {
        mLists = lists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListHolder {
        return CardListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_bank, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardListHolder, position: Int) {
        var bank = mLists!![position]
        if (!TextUtils.isEmpty(bank.cardNumber)) {
            holder.tvBankNum?.text = bank.cardNumber
        }
        if (!TextUtils.isEmpty(bank.cvv)) {
            holder.tvBankName?.text = bank.cvv
        }

        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(bank, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mLists == null) 0 else mLists!!.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bank: CardResponseBean.Bank, pos: Int)
    }
}