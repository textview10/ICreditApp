package com.loan.icreditapp.collect.bean

class CallRecordRequest {

    var num : String? = null
    var date : Long? = null
    var type : Int? = null

    constructor(num : String, date : Long, type : Int){
        this.num = num
        this.date = date
        this.type = type
    }
}