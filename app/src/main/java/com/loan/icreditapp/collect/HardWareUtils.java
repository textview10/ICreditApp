package com.loan.icreditapp.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getAllApps(@NonNull Context context) {
        List<String> apps = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> packlist = pManager.getInstalledApplications(0);
        for (int i = 0, len = packlist.size(); i<len; i++) {
            ApplicationInfo pak = (ApplicationInfo) packlist.get(i);
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak.packageName);
            }
//            apps.add(pak);
        }
        return apps;
    }
}
