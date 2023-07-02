package com.loan.icreditapp.ui.pay.presenter

import androidx.fragment.app.Fragment
import com.loan.icreditapp.bean.pay.MonifyResponseBean
import com.loan.icreditapp.ui.pay.PayFragment
import com.lzy.okgo.model.Response

open abstract class BasePresenter {
    var mPayFragment: Fragment? = null

    var orderId: String? = null
    var amount: String? = null

    var mObserver : Observer? = null

    constructor(fragment: Fragment) {
        mPayFragment = fragment
    }

    abstract fun requestUrl(orderId: String?, amount: String?)

    abstract fun updateResult()

    fun setData(orderId: String?, amount: String?) {
        this.orderId = orderId
        this.amount = amount
    }

    fun isDestroy(): Boolean {
        if (mPayFragment == null) {
            return true
        }
        return mPayFragment!!.isRemoving || mPayFragment!!.isDetached
        var activity = mPayFragment!!.activity
        if (activity == null) {
            return true
        }
        return activity.isFinishing || activity.isDestroyed
    }

    interface Observer {
        fun toWebView(url : String)

        fun repaySuccess()

        fun repayFailure(response : Response<String>, needTip : Boolean, desc : String?)

        fun showMonifyPage(bean : MonifyResponseBean)
    }

    fun setObserver(observer: Observer){
        mObserver = observer
    }
}