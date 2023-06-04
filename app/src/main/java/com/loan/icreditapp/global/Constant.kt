package com.loan.icreditapp.global

import com.loan.icreditapp.bean.TextInfoResponse
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.dialog.order.OrderInfoBean

class Constant {
    companion object {

        const val IS_AAB_BUILD = true

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

        var textInfoResponse: TextInfoResponse? = null

        const val KEY_FIREBASE_DATA : String = "key_firebase_data"

        const val KEY_SHOW_RATE_COUNT : String = "key_show_rate_count"
        const val KEY_HAS_SHOW_RATE : String = "key_has_show_rate"

        var IS_FIRST_APPROVE = false

        var IS_FIRST_APPLY = false

        const val SHOW_BIND_CARD : Boolean = true

        var bankList : ArrayList<CardResponseBean.Bank> = ArrayList()

        var imei : String? = null

    }

}