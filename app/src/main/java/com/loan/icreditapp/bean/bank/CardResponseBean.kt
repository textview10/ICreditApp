package com.loan.icreditapp.bean.bank

class CardResponseBean {

    var cardlist : List<Bank>? = null

    class Bank {
        var id : Int? = null
        var accountId : String? = null
        var cardNumber : String? = null
        var expireDate : String? = null
        var cvv : String? = null
        var cardChecked : Boolean? = null
        var status : Int? = null
        var ctime : String? = null
        var utime : String? = null
    }

}