package com.loan.icreditapp.util;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.loan.icreditapp.global.Constant;
import com.loan.icreditapp.global.MyApp;

public class FirebaseUtils {
    /**
     *    注册 fireb_register
     *     资料1 fireb_data1
     *     资料2 fireb_data2
     *     资料3 fireb_data3
     *     银行账号 fireb_bank
     *
     *     fireb_click_sign 点击登录按钮
     *     fireb_send_sms   请求发送短信
     *     fireb_resend_sms 再次发送短信
     *     fireb_register   注册成功
     *     fireb_apply 首次申请
     *     fireb_apply_confirm   首次申请确认成功
     *     fireb_activity   首次放款
     *     fireb_overdue    首次逾期
     *     fireb_apply_all  申请
     *     fireb_apply_confirm_all    申请确认成功
     *     fireb_activity_all    放款
     *     fireb_overdue_all     逾期
     *     fireb_band_card 进入绑卡
     *     fireb_card_submit 提交绑卡
     *     fireb_card_success 绑卡成功
     */

    private static final String TAG = "IcreaditFirebase";

    public static void logEvent(String event) {
        if (MyApp.Companion.getMContext() == null) {
            return;
        }

        Bundle params = new Bundle();
        if (!Constant.IS_AAB_BUILD) {
            Log.e(TAG, " log event = " + event);
            Toast.makeText(MyApp.Companion.getMContext(),"埋点 = " + event, Toast.LENGTH_SHORT).show();
        }
        FirebaseAnalytics.getInstance(MyApp.Companion.getMContext()).logEvent(event, params);
    }

}
