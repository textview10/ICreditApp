package com.loan.icreditapp.util

import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.global.Constant

object MyAppUtils {
    fun getAppVersionCode() : Int{
        var innerVersionCode = AppUtils.getAppVersionCode()
        if (innerVersionCode < 20000){
            innerVersionCode = Constant.CUR_VERSION_CODE
        }
        return innerVersionCode
    }

    fun getAppVersionName() : String{
        var appName : String = ""
        try {
            appName = AppUtils.getAppVersionName().replace("Version","").trim()
        } catch (e : Exception){
            if (BuildConfig.DEBUG){
                throw e
            }
        }
        if (TextUtils.isEmpty(appName)) {
            appName = Constant.CUR_VERSION_NAME
        }
        return appName
    }
}