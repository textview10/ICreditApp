package com.loan.icreditapp.collect.bean

class AppInfoRequest {
    var appname: String? = null
    var pkgname: String? = null

    var installtime: Long = 0
    var installtime_utc: String? = null
    //0 系统app, 1第三方app
    var type : String? = null
}