package com.loan.icreditapp.collect;

import android.content.Context;
import android.telephony.TelephonyManager;

public class HardWareUtils {
    public static String getSimOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //返回 中国联通或China Unicom，中国电信或China Telecom，中国移动或China Mobile 返回什么根据当前设备使用的数据卡而定
        return tm.getSimOperatorName();
    }

    //获取当前SIM卡的MCC(移动国家码,前3位数)和MNC(移动网络码,后两位数)  加起来共5位
    public static String getSimOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //比如我的联通卡返回46001，电信卡返回46011
        return tm.getSimOperator();
    }
}
