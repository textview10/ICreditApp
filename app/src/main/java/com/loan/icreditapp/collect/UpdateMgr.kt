package com.loan.icreditapp.collect

import android.text.TextUtils
import android.util.Log
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.UpdateResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class UpdateMgr {

    private val TAG = "UpdateMgr"

    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            UpdateMgr()
        }
    }

    fun checkUpdate() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.CHECK_UPDATE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val updateResponse: UpdateResponseBean? =
                        CheckResponseUtils.checkResponseSuccess(response, UpdateResponseBean::class.java)
                    if (updateResponse == null) {
                        try {
                            Log.e(TAG, " update apk failure ." + response.body())
                        } catch (e : Exception) {

                        }
                        return
                    }
                    if (TextUtils.equals(updateResponse.updateType, "0")){
                        return
                    }
                    mListener?.onShowDialog(updateResponse)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update apk failure = " + response.body())
                    }
                    Log.e(TAG, " update apk failure 1 ")
                }
            })
    }

    fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
    }

    private var mListener : OnShowUpdateListener? = null

    fun setOnShowUpdateListener( listener : OnShowUpdateListener){
        mListener = listener
    }

    interface OnShowUpdateListener {
       fun onShowDialog(updateBean : UpdateResponseBean)
    }
}