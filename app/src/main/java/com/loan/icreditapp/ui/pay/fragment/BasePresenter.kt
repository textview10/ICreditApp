package com.loan.icreditapp.ui.pay.fragment

import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.ui.pay.PayFragment

open abstract class BasePresenter {
    private var mPayFragment: PayFragment? = null

    constructor(payFragment: PayFragment) {
        mPayFragment = payFragment
    }

    abstract fun requestUrl(orderId: String?, amount: String?)

    abstract fun updateResult()

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
}