package com.loan.icreditapp.global

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class FireBaseMgr {

    private val TAG = "FireBaseMgr"

    private var mMgr: FireBaseMgr? = null

    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            FireBaseMgr()
        }
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Constant.mFirebaseToken = token
            })
    }

    fun reportFcmToken() {
        if (TextUtils.isEmpty(Constant.mFirebaseToken)) {
            return
        }
        if (!Constant.isNewToken) {
            return
        }
        object : Thread() {
            override fun run() {
                super.run()
                try {
//                    Constant.mAdvertId = getGoogleAdId(context)
                    uploadInfo()
                } catch (e: Exception) {
                }
            }
        }.start()
    }

    private fun uploadInfo() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("fcmToken", Constant.mFirebaseToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_FCM_TOKEN).tag(TAG).headers("token", Constant.mToken)
            .params("fcmToken", Constant.mFirebaseToken)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    Log.e(TAG, " report fcm token success = " + response.body().toString())
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, " report fcm token failure = ")
                }
            })
    }

    @Throws(Exception::class)
    fun getGoogleAdId(context: Context?): String? {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return "Cannot call in the main thread, You must call in the other thread"
        }
        var idInfo: AdvertisingIdClient.Info? = null
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        }
        var adid: String? = null
        try {
            if (idInfo != null) {
                adid = idInfo.id
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return adid
    }
}