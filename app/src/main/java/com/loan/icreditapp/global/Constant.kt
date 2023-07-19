package com.loan.icreditapp.global

import com.loan.icreditapp.bean.TextInfoResponse
import com.loan.icreditapp.bean.bank.CardResponseBean
import com.loan.icreditapp.bean.pay.MonifyResponseBean
import com.loan.icreditapp.dialog.order.OrderInfoBean

class Constant {
    companion object {

        const val IS_AAB_BUILD = false

        const val CUR_VERSION_CODE = 20089
        const val CUR_VERSION_NAME = "2.2.9"

        const val IS_COLLECT= true

        const val KEY_ACCOUNT_ID = "key_sign_in_account_id"

        const val KEY_TOKEN = "key_sign_in_token"

        const val KEY_MOBILE = "key_sign_in_mobile"

        const val KEY_LOGIN_TIME = "key_sign_in_login_time"

        const val TEST_KEY_NOT_AUTO_LOGIN_EXECUTE = "test_key_not_auto_login_execute"

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

        var monifyBean : MonifyResponseBean? = null
    }

}