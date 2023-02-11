package com.loan.icreditapp.ui.pay.fragment

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.pay.PayStackResponseBean
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

    constructor(payFragment: PayFragment) : super(payFragment) {

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
                        return
                    }
                    mPayStackBean = payStackBean
                    if (!TextUtils.isEmpty(mPayStackBean!!.authorizationURL)){
                        mObserver?.toWebView(mPayStackBean!!.authorizationURL!!)
                        Log.e(PayFragment.TAG, " pay stack presenter response = " + response.body().toString())
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    ToastUtils.showShort("pay stack failure " + response.body().toString())
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

                    Log.e(PayFragment.TAG, " pay stack presenter response = " + response.body().toString())
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }

                }
            })
    }


}