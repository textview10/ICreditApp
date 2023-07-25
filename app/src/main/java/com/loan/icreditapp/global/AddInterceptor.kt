package com.loan.icreditapp.global

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.DeviceUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.util.MyAppUtils
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONException

class AddInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            val newRequest = request.newBuilder()
                .addHeader("APP-Name", BuildConfig.APPLICATION_ID)
                .addHeader("APP-ID", "2")
                .addHeader("platform", "Google play")
                .addHeader("device", DeviceUtils.getModel())
                .addHeader("deviceId", DeviceUtils.getUniqueDeviceId())
                .addHeader("brand", DeviceUtils.getManufacturer())
                .addHeader("os", "Android")
                .addHeader("isSimulator",if (DeviceUtils.isEmulator()) "1" else "0")
                .addHeader("lang", "en")
                .addHeader("innerVersionCode", MyAppUtils.getAppVersionCode().toString())
                .addHeader("appVersion", MyAppUtils.getAppVersionName())
                .addHeader("channel", "google play")
                .addHeader("imei", if (!TextUtils.isEmpty(Constant.imei)) Constant.imei!!
                        else DeviceUtils.getAndroidID()!!)
                .build()
            return chain.proceed(newRequest)
        } catch (e: JSONException) {
            Log.e("BuildRequestJsonUtils", e.toString())
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
        return chain.proceed(request.newBuilder().build())
    }
}