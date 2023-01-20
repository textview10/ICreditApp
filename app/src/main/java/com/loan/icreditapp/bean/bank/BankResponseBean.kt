package com.loan.icreditapp.bean.bank

class BankResponseBean {

    var cardlist : List<Bank>? = null

    class Bank {
        var bankCode: String? = null
        var bankName: String? = null
    }
}