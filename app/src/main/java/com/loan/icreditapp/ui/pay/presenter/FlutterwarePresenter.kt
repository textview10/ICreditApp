package com.loan.icreditapp.ui.pay.presenter

import android.text.TextUtils
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.pay.FlutterwareResponse1Bean
import com.loan.icreditapp.bean.pay.FlutterwareResponse2Bean
import com.loan.icreditapp.bean.pay.FlutterwareResultBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class FlutterwarePresenter : BasePresenter {

    private var txRef : String? = null

    constructor(payFragment: PayFragment) : super(payFragment) {

    }

    override fun requestUrl(orderId: String?, amount: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("token", Constant.mToken)
            jsonObject.put("orderId", orderId)
            jsonObject.put("chargeType", "2")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_TEXT_REF).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var flutterware1Bean = CheckResponseUtils.checkResponseSuccess(response, FlutterwareResponse1Bean::class.java)
                    if (flutterware1Bean == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.isEmpty(flutterware1Bean.txRef)){
                        mObserver?.repayFailure(response, true, "request getTextRef = null")
                        return
                    }
                    uploadJson(flutterware1Bean!!.txRef!!, "")
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "request getTextRef failure")
                }
            })
    }

    private fun uploadJson(txRef : String, jsonStr : String){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        this.txRef = txRef
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("chargeType", "2")
            jsonObject.put("txRef", txRef)
            jsonObject.put("jsonStr", jsonStr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_JSON).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var flutterware2Bean = CheckResponseUtils.checkResponseSuccess(response, FlutterwareResponse2Bean::class.java)
                    if (flutterware2Bean == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.equals(flutterware2Bean.status, "1")){
                        // TODO
//                        mObserver?.repaySuccess()
                    } else {
                        mObserver?.repayFailure(response, true, "flutterware uploadJson status != 1")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "flutter uploadJson error")
                }
            })
    }

    override fun updateResult() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("txRef", txRef)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_FLUTTER_WAVE_RESULT).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var flutterwareResult = CheckResponseUtils.checkResponseSuccess(response, FlutterwareResultBean::class.java)
                    if (flutterwareResult == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.equals(flutterwareResult.status, "1")){
                        mObserver?.repaySuccess()
                    } else {
                        mObserver?.repayFailure(response, true, "flutterware status != 1")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "request flutterware error")
                }
            })
    }


}