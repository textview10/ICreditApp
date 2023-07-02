package com.loan.icreditapp.util

import com.blankj.utilcode.util.AppUtils

object MyAppUtils {
    fun getAppVersionCode() : Int{
        var innerVersionCode = AppUtils.getAppVersionCode()
        if (innerVersionCode < 20000){
            innerVersionCode = 20086
        }
        return innerVersionCode
    }
}