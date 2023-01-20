package com.loan.icreditapp.event

import android.util.Pair
import com.loan.icreditapp.bean.bank.BankResponseBean

class BankListEvent {

    var mData: BankResponseBean.Bank? = null

    constructor(data: BankResponseBean.Bank?) {
        mData = data
    }
}