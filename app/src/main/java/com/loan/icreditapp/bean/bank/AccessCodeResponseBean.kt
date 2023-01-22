package com.loan.icreditapp.bean.bank

class AccessCodeResponseBean {

    //验证银行卡首先获取 access code
    var accessCode : String? = null
    //验证路径
    var authorizationURL : String? = null
    //交易流水号
    var reference : String? = null

}