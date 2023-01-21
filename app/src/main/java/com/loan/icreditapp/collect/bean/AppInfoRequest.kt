package com.loan.icreditapp.collect.bean

class AppInfoRequest {
    var name: String? = null
    var packageName: String? = null

    // INSTALL TIME
    var it: Long = 0
    // LAST UPDATE TIME
    var lu: Long = 0
    //0 系统app, 1第三方app
    var type : Int = 0
}