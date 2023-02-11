package com.loan.icreditapp.util;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.loan.icreditapp.global.MyApp;

public class FirebaseUtils {
    /**
     *    注册 firebase_register
     *     资料1 firebase_data1
     *     资料2 firebase_data2
     *     资料3 firebase_data3
     *     银行账号 firebase_bank
     *     申请 firebase_apply
     *     申请确认 firebase_apply_confirm
     *     放款 firebase_activity
     *     逾期 firebase_overdue
     */

    private static final String TAG = "IcreaditFirebase";

    public static void logEvent(String event) {
        if (MyApp.Companion.getMContext() == null) {
            return;
        }

        Bundle params = new Bundle();
        Log.e(TAG, " log event = " + event);
        FirebaseAnalytics.getInstance(MyApp.Companion.getMContext()).logEvent(event, params);
    }

}
