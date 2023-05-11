package com.loan.icreditapp.ui.setting

import androidx.annotation.IntDef
import com.loan.icreditapp.ui.setting.PageType.Companion.ABOUT
import com.loan.icreditapp.ui.setting.PageType.Companion.BANK_ACCOUNT
import com.loan.icreditapp.ui.setting.PageType.Companion.CARD
import com.loan.icreditapp.ui.setting.PageType.Companion.CONTACT_US
import com.loan.icreditapp.ui.setting.PageType.Companion.FEED_BACK
import com.loan.icreditapp.ui.setting.PageType.Companion.HELP
import com.loan.icreditapp.ui.setting.PageType.Companion.LOGOUT
import com.loan.icreditapp.ui.setting.PageType.Companion.MESSAGE
import com.loan.icreditapp.ui.setting.PageType.Companion.MY_LOAN
import com.loan.icreditapp.ui.setting.PageType.Companion.MY_PROFILE
import com.loan.icreditapp.ui.setting.PageType.Companion.OFFLINE_REPAY
import com.loan.icreditapp.ui.setting.PageType.Companion.RATE_US
import com.loan.icreditapp.ui.setting.PageType.Companion.TEST_TO_PROFILE
import com.loan.icreditapp.ui.setting.PageType.Companion.TEST_TO_PROFILE2
import com.loan.icreditapp.ui.setting.PageType.Companion.TEST_TO_PROFILE3


@IntDef(MY_LOAN, MY_PROFILE, CARD, BANK_ACCOUNT, MESSAGE, HELP, ABOUT, LOGOUT, TEST_TO_PROFILE, RATE_US, CONTACT_US, OFFLINE_REPAY, FEED_BACK, TEST_TO_PROFILE2, TEST_TO_PROFILE3)
@Retention(AnnotationRetention.SOURCE)
annotation class PageType {
    companion object {
        const val MY_LOAN = 0
        const val MY_PROFILE = 1
        const val CARD = 2
        const val BANK_ACCOUNT = 3
        const val MESSAGE = 4
        const val HELP = 5
        const val ABOUT = 6
        const val RATE_US = 7
        const val CONTACT_US = 8
        const val OFFLINE_REPAY = 9
        const val FEED_BACK = 10

        const val LOGOUT = 111
        const val TEST_TO_PROFILE = 112
        const val TEST_TO_PROFILE2 = 113
        const val TEST_TO_PROFILE3 = 114
    }
}