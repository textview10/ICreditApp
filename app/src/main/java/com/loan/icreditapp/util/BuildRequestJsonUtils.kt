package com.loan.icreditapp.util

import android.annotation.SuppressLint
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.PhoneUtils
import com.loan.icreditapp.global.Constant
import com.lzy.okgo.model.HttpHeaders
import org.json.JSONException
import org.json.JSONObject

class BuildRequestJsonUtils {

    companion object {

        fun buildRequestJson(): JSONObject {
            var jsonObject:JSONObject = JSONObject()
//            jsonObject.put()
            return jsonObject
        }

        fun buildHttpHeadersNonPermission(): HttpHeaders {
            val httpHeaders = HttpHeaders()
            try {
                httpHeaders.put("platform", "Google play")      //应用平台
                httpHeaders.put("token", Constant.mToken)   //用户token
                httpHeaders.put("device", DeviceUtils.getModel())  //设备型号
                httpHeaders.put("deviceId", DeviceUtils.getUniqueDeviceId())   // 设备唯一标识
                httpHeaders.put("brand", DeviceUtils.getModel())       //手机品牌
                httpHeaders.put("os", "Android")          //操作系统
                var isSimulatorStr = "0"
                if (DeviceUtils.isEmulator()) {
                    isSimulatorStr = "1"
                }
                httpHeaders.put("isSimulator", isSimulatorStr) //是否为模拟器
                httpHeaders.put("lang", LanguageUtils.getSystemLanguage().getDisplayLanguage())
                //语言
                httpHeaders.put("innerVersionCode", AppUtils.getAppVersionName())   //内部版本号
                httpHeaders.put("appVersion", AppUtils.getAppVersionCode().toString())   //APP版本号
                httpHeaders.put("channel", "google play")   //安装包发布的渠道
                httpHeaders.put("utmSource", "google play")   //客户来源
                httpHeaders.put("utmMedium", "google play")   //媒介
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return httpHeaders
        }

        @SuppressLint("MissingPermission")
        fun buildHttpHeadersWithPermission(): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.put("imei", PhoneUtils.getIMEI())        //语言
            httpHeaders.put("longitude", Constant.mToken)   //经度
            httpHeaders.put("latitude", Constant.mToken)   //纬度
            return httpHeaders
        }
    }
}