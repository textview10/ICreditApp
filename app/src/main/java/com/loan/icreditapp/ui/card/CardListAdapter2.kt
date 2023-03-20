package com.loan.icreditapp.ui.card

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.util.CardNumUtils

class CardListAdapter2 : RecyclerView.Adapter<CardListHolder2> {

    private var mLists: ArrayList<CardResponseBean.Bank>? = null
    private var mItemClickListener: OnItemClickListener? = null

    var curPos : Int = -1

    constructor(lists: ArrayList<CardResponseBean.Bank>?) {
        mLists = lists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListHolder2 {
        return CardListHolder2(
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_bank2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardListHolder2, position: Int) {
        var bank = mLists!![position]
        if (!TextUtils.isEmpty(bank.cardNumber)) {
            var cardNumber: String? = CardNumUtils.getCardNumHide(bank.cardNumber)
//            Log.e("Okhttp", " 1 = " + bank.cardNumber!!)
//            Log.e("Okhttp", " 2 = " + cardNumber)
            holder.tvBankNum?.text = cardNumber
        }
        if (!TextUtils.isEmpty(bank.expireDate)) {
            holder.tvBankName?.text = "Expire date: " + bank.expireDate
        }
        if (curPos == position){
            holder.flBg?.setBackgroundResource(R.drawable.shape_bank_card_bg)
            holder.tvBankName?.setTextColor(holder.itemView.resources.getColor(R.color.white))
            holder.tvBankNum?.setTextColor(holder.itemView.resources.getColor(R.color.white))
            holder.ivCheck?.visibility = View.VISIBLE
        } else {
            holder.flBg?.background = null
            holder.tvBankName?.setTextColor(holder.itemView.resources.getColor(R.color.black))
            holder.tvBankNum?.setTextColor(holder.itemView.resources.getColor(R.color.black))
            holder.ivCheck?.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(bank, position)
        }
    }

    fun getBankNum() : String?{
        if (curPos == -1){
            return null
        }
       return mLists?.get(curPos)?.cardNumber
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