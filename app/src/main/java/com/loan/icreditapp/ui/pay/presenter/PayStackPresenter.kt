package com.loan.icreditapp.ui.pay.presenter

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.pay.PayStackResponseBean
import com.loan.icreditapp.bean.pay.PayStackResultBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class PayStackPresenter : BasePresenter {

    private var mPayStackBean: PayStackResponseBean? = null

    constructor(payFragment: Fragment) : super(payFragment) {

    }

    override fun requestUrl(orderId: String?, amount: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.PAY_STACK).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var payStackBean = CheckResponseUtils.checkResponseSuccess(response,  PayStackResponseBean::class.java)
                    if (payStackBean == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    mPayStackBean = payStackBean
                    if (!TextUtils.isEmpty(mPayStackBean!!.authorizationURL)){
                        mObserver?.toWebView(mPayStackBean!!.authorizationURL!!)
                    } else {
                        mObserver?.repayFailure(response, true, "request payStack failure")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "request payStack error")
                }
            })
    }

    override fun updateResult() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            if (mPayStackBean != null) {
                jsonObject.put("reference", mPayStackBean!!.reference)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.PAY_STACK_RESULT).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var payStackResult = CheckResponseUtils.checkResponseSuccess(response, PayStackResultBean::class.java)
                    if (payStackResult == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.isEmpty(payStackResult.status)){
                        mObserver?.repayFailure(response, true, "update payStack result failure" )
                        LogSaver.logToFile(" pay stack error = " + jsonObject.toString())
                        return
                    }
                    val isSuccess = TextUtils.equals(payStackResult.status, "1") || TextUtils.equals(payStackResult.status, "success")
                    if (!isSuccess){
                        mObserver?.repayFailure(response, true, "update payStack result status not correct, try again.")
                        LogSaver.logToFile(" pay stack not correct = " + jsonObject.toString())
                        return
                    }
                    mObserver?.repaySuccess()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "update payStack result error")
                }
            })
    }


}