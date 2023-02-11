package com.loan.icreditapp.ui.pay.presenter

import android.text.TextUtils
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.pay.MonifyResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class MonifyPresenter : BasePresenter {

    constructor(payFragment: PayFragment) : super(payFragment) {

    }

    override fun requestUrl(orderId: String?, amount: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_RESERVED_ACCOUNT).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    var monifyBean = CheckResponseUtils.checkResponseSuccess(
                        response,
                        MonifyResponseBean::class.java
                    )
                    if (monifyBean == null) {
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.equals(monifyBean.reserved, "1")) {
                        mObserver?.showMonifyPage(monifyBean)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    mObserver?.repayFailure(response, false, "monify error")
                }
            })
    }

    override fun updateResult() {

    }


}