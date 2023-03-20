package com.loan.icreditapp.ui.pay.presenter

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.loan.RepayLoanResponseBean
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject

class NorLoanPresenter : BasePresenter {
    private var bankNum : String? = null

    constructor(payFragment: PayFragment) : super(payFragment) {

    }

    //订单ID ,申请金额
    override fun requestUrl(orderId: String?, amount: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("amount", amount.toString())
            jsonObject.put("cardNumber", bankNum)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.LOAN_REPAY).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val loanResponse: RepayLoanResponseBean? =
                        CheckResponseUtils.checkResponseSuccess(
                            response,
                            RepayLoanResponseBean::class.java
                        )
                    if (loanResponse == null) {
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (!TextUtils.equals(loanResponse.status, "1")) {
                        mObserver?.repayFailure(response, true, "nor loan repay status ! = 1")
                        return
                    }
                    mObserver?.repaySuccess()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    mObserver?.repayFailure(response, true, "nor repay loan error")
                }
            })
    }

    override fun updateResult() {

    }

    fun getCurBankNum() : String?{
        return bankNum
    }

    fun setCurBankNum(bankNum: String?) {
        this.bankNum = bankNum
    }
}