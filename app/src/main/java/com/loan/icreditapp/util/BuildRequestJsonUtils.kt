package com.loan.icreditapp.util

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.PhoneUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.global.Constant
import com.lzy.okgo.model.HttpHeaders
import org.json.JSONException
import org.json.JSONObject

class BuildRequestJsonUtils {

    companion object {

        fun buildRequestJson(): JSONObject {
            var httpHeaders: JSONObject = JSONObject()

            return httpHeaders
        }

        fun buildHeadersNonLogin(): HttpHeaders {
            val httpHeaders = HttpHeaders()
            try {
                // device	String	Y	设备型号
                httpHeaders.put("APP-Name", BuildConfig.APPLICATION_ID)  //设备型号
                httpHeaders.put("APP-ID", "2")  //设备型号
                //platform	String	Y	应用平台
                httpHeaders.put("platform", "Google play")      //应用平台
                // device	String	Y	设备型号
                httpHeaders.put("device", DeviceUtils.getModel())  //设备型号
                //  deviceId	String	Y	设备唯一标识
                httpHeaders.put("deviceId", DeviceUtils.getUniqueDeviceId())   // 设备唯一标识
                //brand	String	Y	手机品牌
                httpHeaders.put("brand", DeviceUtils.getManufacturer())       //手机品牌
                // os	String	Y	操作系统
                httpHeaders.put("os", "Android")          //操作系统
                // isSimulator	String	Y	是否为模拟器
                httpHeaders.put("isSimulator", if (DeviceUtils.isEmulator()) "1" else "0") //是否为模拟器
                // lang	String	Y	语言
                httpHeaders.put("lang", "en")
                //  innerVersionCode	Integer	Y	内部版本号
                var innerVersionCode = AppUtils.getAppVersionCode()
                if (innerVersionCode <= 20000){
                    innerVersionCode = 20083
                }
                httpHeaders.put("innerVersionCode", innerVersionCode.toString())   //内部版本号
                //   appVersion	String	Y	APP版本号
                var appName : String? = null
                try {
                    appName = AppUtils.getAppVersionName().replace("Version","").trim()
                } catch (e : Exception){
                    if (BuildConfig.DEBUG){
                        throw e
                    }
                }
                if (TextUtils.isEmpty(appName)) {
                    appName = "2.2.1"
                }
                httpHeaders.put("appVersion", appName)   //APP版本号
                //  channel	String	Y	安装包发布的渠道
                httpHeaders.put("channel", "google play")   //安装包发布的渠道
                //H5,google play
                // utmSource	String	Y	客户来源
//                httpHeaders.put("utmSource", "")   //客户来源
                //banner,click,search words
                //  utmMedium	String	Y	媒介
//                httpHeaders.put("utmMedium", "")   //媒介
                //   imei
                httpHeaders.put("imei", if (!TextUtils.isEmpty(Constant.imei)) Constant.imei
                else DeviceUtils.getAndroidID())        //imei
                // longitude	String	Y	经度
//                httpHeaders.put("longitude", "")   //经度
                //latitude	String	Y	纬度
//                httpHeaders.put("latitude", "")
            } catch (e: JSONException) {
                Log.e("BuildRequestJsonUtils", e.toString())
                e.printStackTrace()
            }
            return httpHeaders
        }

        fun buildHeaderToken(): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.put("token", Constant.mToken)
            return httpHeaders
        }
        fun clearHeaderToken(): HttpHeaders{
            val httpHeaders = HttpHeaders()
            httpHeaders.put("token", "")
            return httpHeaders
        }
//        @SuppressLint("MissingPermission")
//        fun buildHeaderImei(): HttpHeaders {
//            val httpHeaders = HttpHeaders()
//            Constant.imei = PhoneUtils.getIMEI()
//            httpHeaders.put("imei", if (!TextUtils.isEmpty(Constant.imei)) Constant.imei
//                else DeviceUtils.getAndroidID())        //imei
//            return httpHeaders
//        }

        fun buildUtmSource(utmSource : String): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.put("utmSource", utmSource)
//            Log.e("Test", " build app id = " + appId)
            return httpHeaders
        }

        fun buildUtmMedium(utmMedium : String): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.put("utmMedium", utmMedium)
//            Log.e("Test", " build app id = " + appId)
            return httpHeaders
        }

        @SuppressLint("MissingPermission")
        fun buildHeaderLocation(longitude: String, latitude: String): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.put("longitude", longitude)   //经度
            httpHeaders.put("latitude", latitude)   //纬度
            return httpHeaders
        }
    }
}