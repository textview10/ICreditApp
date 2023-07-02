package com.loan.icreditapp;

import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.loan.icreditapp.ui.pay.presenter.BasePresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public void test3(String content){
        String TEMPLATE = "Your authentication number is";
        if (!TextUtils.isEmpty(content) && content.contains(TEMPLATE)){
            Pattern pattern = Pattern.compile("[^0-9]"); //正则表达式.
            Matcher matcher = pattern.matcher(content);
            String result = matcher.replaceAll("");
            Log.e("Test", " result = " + result);
        }
    }

    public void test1() {
       String str = "    com.internationalloanspk\n" ;
       StringBuffer sb = new StringBuffer();
      String[] strss =  str.split("\n");
      for (int i = 0; i < strss.length; i++){
          String item = strss[i].replace(" ", "");
//          <package android:name="loanapp.india.personal.loanassist" />
          sb.append("<package android:name=\"").append(item).append("\" />").append("\n");
      }
       String temp =  sb.toString();
        Log.e("Test", temp);
//        String[] type = new String[5];
//        Spinner spinner = null;
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        EditText et = null;
//        et.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                return false;
//            }
//        });
//
//        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
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
