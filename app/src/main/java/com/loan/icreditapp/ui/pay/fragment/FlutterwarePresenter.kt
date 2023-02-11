package com.loan.icreditapp.ui.pay.fragment

import android.util.Log
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class FlutterwarePresenter : BasePresenter {

    constructor(payFragment: PayFragment) : super(payFragment) {

    }

    override fun requestUrl(orderId: String?, amount: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("amount", amount)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.REDOCLY_REPAY_PAGE).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    Log.e(PayFragment.TAG, " redocly presenter response = " + response.body().toString())
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }

                }
            })
    }

    override fun updateResult() {

    }


}