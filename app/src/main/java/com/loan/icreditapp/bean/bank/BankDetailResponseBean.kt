package com.loan.icreditapp.bean.bank

class BankDetailResponseBean {

    //{"bankedit":false, "bankdetail":
    // {"accountId":"220330150100000164","bankAccountChecked":true,
    // "bankAccountNumber":"0082832314","bankCode":"044","bankName":"Access Bank",
    // "ctime":"2022-03-30 15:44:59","id":23}}

    var bankedit: Boolean? = false
    var bankdetail: BankDetail? = null


    class BankDetail {
        var accountId: String? = null
        var bankAccountChecked: Boolean? = false
        var bankAccountNumber: String? = null
        var bankCode: String? = null
        var bankName: String? = null
    }
}