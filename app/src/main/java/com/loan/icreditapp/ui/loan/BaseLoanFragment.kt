package com.loan.icreditapp.ui.loan

import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.dialog.order.OrderInfoBean

abstract class BaseLoanFragment : BaseFragment() {
    var mOrderInfo : OrderInfoBean? = null

    open fun setOrderInfo(orderInfoBean: OrderInfoBean) {
        mOrderInfo = orderInfoBean
    }
}