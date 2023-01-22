package com.loan.icreditapp.bean.loan

class CheckLoanResponseBean {
    //订单号
    var orderId: String? = null

    //已填写个人信息
    var hasProfile: Boolean? = null

    //已填写联系人信息
    var hasContact: Boolean? = null

    //已填写其他信息
    var hasOther: Boolean? = null

    //check成功
    var bvnChecked: Boolean? = null

    //银行账号验证成功
    var accountChecked: Boolean? = null

    //银行卡验证成功
    var cardChecked: Boolean? = null

}