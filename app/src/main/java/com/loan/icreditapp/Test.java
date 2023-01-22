package com.loan.icreditapp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test {

    private void test1() {
        String[] type = new String[5];
        Spinner spinner = null;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

//    private void chargeCard() {
//        PaystackSdk.chargeCard(this, mCharge, new Paystack.TransactionCallback() {
//            @Override
//            public void onSuccess(Transaction transaction) {
//                Log.d("TAG", "onSuccess:    " + transaction.getReference());
//                verifyOnServer(transaction.getReference());
//            }
//
//            //此函数只在请求OTP保存引用之前调用，如果需要，可以解除otp验证
//            @Override
//            public void beforeValidate(Transaction transaction) {
//                Log.d("TAG", "beforeValidate: " + transaction.getReference());
//            }
//
//            @Override
//            public void onError(Throwable error, Transaction transaction) {
//                // If an access code has expired, simply ask your server for a new one
//                // and restart the charge instead of displaying error
//                count++;
//                dismissProgressDialogFragment();
//                if (count == 1) {
//                    showToast(error.getMessage());
//                    if (error instanceof ExpiredAccessCodeException) {
//                        //再次获取 access code
//                        startAFreshCharge();
//                        chargeCard();
//                        return;
//                    }
//                } else {
//                    showError(error.getMessage());
//                }
//
//            }
//
//
//        });
//    }
//
//    /**
//     * Method to validate the form, and set errors on the edittexts.
//     */
//
//        private void startAFreshCharge() {
//            Card card = loadCard();
//            if (card == null) {
//                return;
//            }
//            Charge mCharge = new Charge();
//            mCharge.setCard(card);
//            fetchAccessCode();
//        }
}
