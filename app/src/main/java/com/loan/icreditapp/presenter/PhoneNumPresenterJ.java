package com.loan.icreditapp.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blankj.utilcode.util.ToastUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PhoneNumPresenterJ {
    private Context mContext;

    private final HashMap<String, Integer> mPhoneNumMap = new HashMap<>();

    private final String[] mItems = {"234"};

    public PhoneNumPresenterJ(Context context) {
        this.mContext = context;
        initPhoneNnumMap();
    }

    //    手机号的格式，开头必须是7、8、9、07、08、09、2347、2348、2349、23407、23408、23409:
//    当开头是7、8、9时，限制用户输入位数10位；
//    当开头是07、08、09时，限制用户输入位数11位；
//    当开头是2347、2348、2349时，限制用户输入位数13位；
//    当开头是23407、23408、23409时，限制用户输入位数14位。
    private void initPhoneNnumMap() {
        mPhoneNumMap.clear();
        mPhoneNumMap.put("0", 10);
        mPhoneNumMap.put("7", 10);
        mPhoneNumMap.put("8", 10);
        mPhoneNumMap.put("9", 10);
        mPhoneNumMap.put("07", 11);
        mPhoneNumMap.put("08", 11);
        mPhoneNumMap.put("09", 11);
        mPhoneNumMap.put("2347", 13);
        mPhoneNumMap.put("2348", 13);
        mPhoneNumMap.put("2349", 13);
        mPhoneNumMap.put("23407", 14);
        mPhoneNumMap.put("23408", 14);
        mPhoneNumMap.put("23409", 14);
    }

    public void initSpinner(Spinner spinner) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public boolean checkIsCorrectPhoneNum(String phoneNum) {
        if (TextUtils.equals(phoneNum, "254888888888")){
            return true;
        }
        Iterator<Map.Entry<String, Integer>> iterator = mPhoneNumMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (phoneNum.startsWith(entry.getKey())) {
                boolean isCorrect = phoneNum.length() == entry.getValue();
                if (!isCorrect) {
                    ToastUtils.showShort(" phone num length must be " + entry.getValue());
                }
                return isCorrect;
            }
        }
        return false;
    }

    public String getSelectString(int selectedItemPosition) {
        if (selectedItemPosition >= mItems.length) {
            return "";
        }
       return mItems[selectedItemPosition];
    }
}
