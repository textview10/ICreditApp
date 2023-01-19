package com.loan.icreditapp.util

import android.annotation.SuppressLint
import android.util.Log
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
            var httpHeaders:JSONObject = JSONObject()

            return httpHeaders
        }

        fun buildHttpHeadersNonPermission(): HttpHeaders {
            val httpHeaders = HttpHeaders()
            try {
                //platform	String	Y	应用平台
                httpHeaders.put("platform", "Google play")      //应用平台
                // token	String	Y	用户token
                httpHeaders.put("token", Constant.mToken)   //用户token
                httpHeaders.put("token", "")   //用户token
                // device	String	Y	设备型号
                httpHeaders.put("device", DeviceUtils.getModel())  //设备型号
                //  deviceId	String	Y	设备唯一标识
                httpHeaders.put("deviceId", DeviceUtils.getUniqueDeviceId())   // 设备唯一标识
                //brand	String	Y	手机品牌
                httpHeaders.put("brand", "xiaomi")       //手机品牌
                // os	String	Y	操作系统
                httpHeaders.put("os", "Android")          //操作系统
                var isSimulatorStr = "0"
                if (DeviceUtils.isEmulator()) {
                    isSimulatorStr = "0"
//                    isSimulatorStr = "1"
                }
                // isSimulator	String	Y	是否为模拟器
                httpHeaders.put("isSimulator", isSimulatorStr) //是否为模拟器
                // lang	String	Y	语言
                httpHeaders.put("lang", "en")
               //  innerVersionCode	Integer	Y	内部版本号
                httpHeaders.put("innerVersionCode", "12")   //内部版本号
                //   appVersion	String	Y	APP版本号
//                httpHeaders.put("appVersion", AppUtils.getAppVersionCode().toString())   //APP版本号
                httpHeaders.put("appVersion", "1.2.01")   //APP版本号
                //  channel	String	Y	安装包发布的渠道
                httpHeaders.put("channel", "google play")   //安装包发布的渠道
                //H5,google play
                // utmSource	String	Y	客户来源
                httpHeaders.put("utmSource", "google play")   //客户来源
                //banner,click,search words
                //  utmMedium	String	Y	媒介
                httpHeaders.put("utmMedium", "banner")   //媒介
                //   imei	String	Y	语言
                httpHeaders.put("imei", "en")        //imei
                // longitude	String	Y	经度
                httpHeaders.put("longitude", "")   //经度
                //latitude	String	Y	纬度
                httpHeaders.put("latitude", "")
            } catch (e: JSONException) {
                Log.e("BuildRequestJsonUtils", e.toString())
                e.printStackTrace()
            }
            return httpHeaders
        }

        @SuppressLint("MissingPermission")
        fun buildHttpHeadersWithPermission(): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.put("imei", PhoneUtils.getIMEI())        //imei
            httpHeaders.put("longitude", Constant.mToken)   //经度
            httpHeaders.put("latitude", Constant.mToken)   //纬度
            return httpHeaders
        }
    }
}