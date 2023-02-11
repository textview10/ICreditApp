package com.loan.icreditapp.ui.pay.presenter

import android.text.TextUtils
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.pay.RedocluResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class RedoclyPresenter : BasePresenter {

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
                    if (isDestroy()) {
                        return
                    }
                    var redoclueBean = CheckResponseUtils.checkResponseSuccess(
                        response,
                        RedocluResponseBean::class.java
                    )
                    if (redoclueBean == null) {
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.isEmpty(redoclueBean!!.pageURL)) {
                        mObserver?.repayFailure(response, true, " redocly pageUrl = null")
                        return
                    }
                    mObserver?.toWebView(redoclueBean!!.pageURL!!)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    mObserver?.repayFailure(response, true, "request redocly error")
                }
            })
    }

    override fun updateResult() {
        mObserver?.repaySuccess()
    }


}