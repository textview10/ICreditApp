package com.loan.icreditapp.global

import com.loan.icreditapp.dialog.order.OrderInfoBean

class Constant {
    companion object {

        const val KEY_ACCOUNT_ID = "key_sign_in_account_id"

        const val KEY_TOKEN = "key_sign_in_token"

        var mToken : String? = null

        var mAccountId : String? = null

        var mLaunchOrderInfo: OrderInfoBean? = null

        var mFirebaseToken: String? = null

        val TEST_SEND_MSG = true
    }

}