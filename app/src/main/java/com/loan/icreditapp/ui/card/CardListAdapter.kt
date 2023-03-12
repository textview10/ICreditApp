package com.loan.icreditapp.ui.card

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
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
            val length : Int? = bank.cardNumber?.length
            var cardNumber: String = bank.cardNumber!!
            length?.let {
                if (length > 4){
                    try {
                        val temp = cardNumber!!.substring(cardNumber.length - 4, cardNumber.length)
                        var prex = StringBuffer()
                        for (i in 0 until (length - 4)) {
                            prex.append("*")
                        }
                        val result = prex.append(temp).toString()
                        val tempResult = StringBuffer()
                        for (i in 0 until result.length) {
                            if (i % 4 == 3) {
                                tempResult.append(result[i]).append(" ")
                            } else {
                                tempResult.append(result[i])
                            }
                        }
                        cardNumber = tempResult.toString()
                    } catch (_: java.lang.Exception){

                    }
                }
            }
//            Log.e("Okhttp", " 1 = " + bank.cardNumber!!)
//            Log.e("Okhttp", " 2 = " + cardNumber)
            holder.tvBankNum?.text = cardNumber
        }
        if (!TextUtils.isEmpty(bank.expireDate)) {
            holder.tvBankName?.text = "Expire date: " + bank.expireDate
        }
        holder.flBg?.setBackgroundResource(getBgResByPos(position))
        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(bank, position)
        }
    }

    private fun getBgResByPos(pos : Int) : Int{
        val resultPos : Int = pos % 4
        when (resultPos) {
            (0) -> {
                return R.drawable.shape_bank_card_2
            }
            (1) -> {
                return R.drawable.shape_bank_card_1
            }
            (2) -> {
                return R.drawable.shape_bank_card_3
            }
            (3) -> {
                return R.drawable.shape_bank_card_4
            }
        }
        return R.drawable.shape_bank_card_3
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