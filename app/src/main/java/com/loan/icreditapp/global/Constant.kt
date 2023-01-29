package com.loan.icreditapp.global

import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.dialog.order.OrderInfoBean

class Constant {
    companion object {

        const val KEY_ACCOUNT_ID = "key_sign_in_account_id"

        const val KEY_TOKEN = "key_sign_in_token"

        const val KEY_MOBILE = "key_sign_in_mobile"

        var mToken : String? = null

        var mAccountId : String? = null

        var mMobile : String? = null

        var mNeedRefreshProfile : Boolean = false

        var mLaunchOrderInfo: OrderInfoBean? = null

        var mFirebaseToken: String? = null

        var isNewToken = false
    }

}