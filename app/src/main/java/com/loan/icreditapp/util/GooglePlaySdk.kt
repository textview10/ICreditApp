package com.newton.utils

import android.content.Context
import android.os.RemoteException
import android.text.TextUtils
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo

class GooglePlaySdk {
    var referrerClient: InstallReferrerClient? = null
    fun start() {
        referrerClient = InstallReferrerClient.newBuilder(mContext).build()
        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        var response: ReferrerDetails? = null
                        try {
                            if (referrerClient != null) {
                                response = referrerClient!!.installReferrer
                            }
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }
                        if (response != null) {
                            val referrerUrl = response.installReferrer
                            if (!TextUtils.isEmpty(referrerUrl)) {
                                var refererMap = getSplitData(referrerUrl)
                                if (refererMap != null) {
                                    var map = refererMap.get("utm_medium")
                                    if (!TextUtils.isEmpty(map)) {
                                        OkGo.getInstance().addCommonHeaders(BuildRequestJsonUtils.buildAppId(map!!))
                                    }
                                }
                            }
                        }
                        referrerClient?.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                start()
            }
        })
    }

    /**
     * 把格式：utm_source=testSource&utm_medium=testMedium&utm_term=testTerm&utm_content=11
     * 这种格式的数据切割成key,value的形式并put进JSONObject对象，用于上传
     *
     * @param referer
     * @return
     */
    private fun getSplitData(referer: String) : MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        if (referer.length > 2 && referer.contains("&")) {
            for (data in referer.split("&".toRegex()).toTypedArray()) {
                if (data.length > 2 && data.contains("=")) {
                    val split = data.split("=".toRegex()).toTypedArray()
                    for (i in split.indices) {
                        map[split[0]] = split[1]
                    }
                }
            }
        }
        return map
    }

    companion object {
        private var instance: GooglePlaySdk? = null
        var mContext: Context? = null
        fun getInstance(context: Context?): GooglePlaySdk? {
            if (instance == null) {
                instance = GooglePlaySdk()
            }
            mContext = context
            return instance
        }
    }
}