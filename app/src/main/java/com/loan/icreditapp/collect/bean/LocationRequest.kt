package com.loan.icreditapp.collect.bean

class LocationRequest {
    var gpsLongitude = 0.0
    var gpsLatitude = 0.0
    var netWorkLongitude = 0.0
    var netWorkLatitude = 0.0
    var extra: String? = null

    var bssid: List<String>? = null
}