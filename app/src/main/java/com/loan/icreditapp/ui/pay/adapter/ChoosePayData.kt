package com.loan.icreditapp.ui.pay.adapter

import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.loan.icreditapp.R

class ChoosePayData() {

    @DrawableRes
    var icon : Int? = null

    var title : String? = null

    var desc : String? = null

    companion object {

        fun buildList(cardNum : String?, showBottom : Boolean) : ArrayList<ChoosePayData>{
            val list = ArrayList<ChoosePayData>()

            val pay1 = ChoosePayData()
            pay1.icon = R.drawable.ic_bank_pay
            pay1.title = "BankCard"
            pay1.desc = if (cardNum == null || TextUtils.isEmpty(cardNum))
                "No card record" else cardNum
            list.add(pay1)

            val pay2 = ChoosePayData()
            pay2.icon = R.drawable.ic_pay_2
            pay2.title = "Online"
            pay2.desc = "Paystack"
            list.add(pay2)

            val pay3 = ChoosePayData()
            pay3.icon = R.drawable.ic_pay_3
            pay3.title = "Online"
            pay3.desc = "Flutterwave"
            list.add(pay3)

            if (showBottom) {
                val pay4 = ChoosePayData()
                pay4.icon = R.drawable.ic_pay_4
                pay4.title = "Bank Transfer"
                pay4.desc = null
                list.add(pay4)
            }
            return list
        }
    }
}