package com.loan.icreditapp.bean

class ServerLiveBean {

    var status :String? = null

    fun isServerLive(): Boolean {
        return status == "live"
    }

}