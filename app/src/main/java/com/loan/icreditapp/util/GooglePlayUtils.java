package com.loan.icreditapp.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loan.icreditapp.BuildConfig;

/**
 * 和google play相关的类
 *
 * @author Richard
 */
public class GooglePlayUtils {
    public final static String PACKAGE_GOOGLE_PLAY = "com.android.vending";
    public final static String MY_APP_MARKTE_URL = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

    private static GooglePlayUtils utils;

    private GooglePlayUtils() {

    }

    public synchronized static GooglePlayUtils getInstance() {
        if (utils == null) {
            utils = new GooglePlayUtils();
        }
        return utils;
    }

    /**
     * 前往google play打开应用
     */
    public void goToGooglePlay(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (hasGooglePlay(context)) {
                intent.setPackage(PACKAGE_GOOGLE_PLAY);
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测是否有google play
     *
     * @param context
     * @return
     */
    public boolean hasGooglePlay(Context context) {
        try {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Intent gotoPlay(Context context, String url) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.vending");
        } catch (Exception e) {
            e.printStackTrace();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public static Intent goRateToExplore(Context context, String url) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            e.printStackTrace();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }
}
