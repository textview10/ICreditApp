package com.loan.icreditapp.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.loan.icreditapp.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationMgr {

    private static final String TAG = "LocationMgr";

    private static LocationMgr mManager;

    private Context mContext;

    private LocationMgr() {

    }

    public static LocationMgr getInstance() {
        if (mManager == null) {
            synchronized (LocationMgr.class) {
                if (mManager == null) {
                    mManager = new LocationMgr();
                }
            }
        }
        return mManager;
    }

    public void init(Context context) {
        mContext = context;
    }

    private LocationManager locationManager;


    public void getLocation() {
        //1.获取位置管理器
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers == null) {
            ToastUtils.showShort(" no location can use ...");
            return;
        }
        boolean canUseGps = providers.contains(LocationManager.GPS_PROVIDER);
        boolean canUseNetWork = providers.contains(LocationManager.NETWORK_PROVIDER);
        if (canUseGps) {
            getInfoByUseProvider(LocationManager.GPS_PROVIDER, true);
        }
        if (canUseNetWork) {
            getInfoByUseProvider(LocationManager.NETWORK_PROVIDER, false);
        }
        try {
            getWifiResult();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                throw e;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getInfoByUseProvider(String locationProvider, boolean isGps) {
        MyLocationListener curListener = isGps ? gpsLocationListener : netWorkLocationListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //3.获取上次的位置，一般第一次运行，此值为null
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
//                    Toast.makeText(this, location.getLongitude() + " " +
//                            location.getLatitude() + "",Toast.LENGTH_SHORT).show();
                Log.v(TAG, "获取上次的位置-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
                getAddress(location, isGps);
            } else {
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, curListener);
            }

        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                Log.v(TAG, "获取上次的位置-经纬度：" + location.getLongitude() + "   " + location.getLatitude());
                getAddress(location, isGps);
            } else {
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, curListener);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getWifiResult() {
        NetworkUtils.WifiScanResults wifiScanResult = NetworkUtils.getWifiScanResult();
        if (wifiScanResult == null || wifiScanResult.getAllResults() == null) {
            return;
        }
        mBSSIDs.clear();
        for (int i = 0; i < wifiScanResult.getAllResults().size(); i++) {
            ScanResult scanResult = wifiScanResult.getAllResults().get(i);
            if (scanResult == null || TextUtils.isEmpty(scanResult.BSSID)) {
                continue;
            }
            mBSSIDs.add(scanResult.BSSID);
        }
    }

    private MyLocationListener gpsLocationListener = new MyLocationListener(true);

    private MyLocationListener netWorkLocationListener = new MyLocationListener(false);

    private class MyLocationListener implements LocationListener {
        public boolean mIsGps;

        MyLocationListener(boolean isGps) {
            mIsGps = isGps;
        }

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //如果位置发生变化，重新显示地理位置经纬度
//                Toast.makeText(TestLocationActivity.this, location.getLongitude() + " " +
//                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                getAddress(location, mIsGps);
            }
        }
    }

    private double gpsLongitude = Double.NaN;
    private double gpsLatitude = Double.NaN;
    private double netWorkLongitude = Double.NaN;
    private double netWorkLatitude = Double.NaN;
    private StringBuffer extra = new StringBuffer();
    private ArrayList<String> mBSSIDs = new ArrayList<>();

    //获取地址信息:城市、街道等信息
    private void getAddress(Location location, boolean mIsGps) {
        if (location != null) {
            if (mIsGps) {
                gpsLongitude = location.getLongitude();
                gpsLatitude = location.getLatitude();
                Log.v(TAG, " isGps longitude = " + location.getLongitude() + "latitude " + location.getLatitude());
            } else {
                netWorkLongitude = location.getLongitude();
                netWorkLatitude = location.getLatitude();
                Log.v(TAG, " longitude = " + location.getLongitude() + "latitude " + location.getLatitude());
            }

        }
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(mContext, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (result == null || result.size() == 0) {
                    return;
                }
                extra = new StringBuffer();
                for (int i = 0; i < result.size(); i++) {
                    Address address = result.get(i);
                    extra.append("countryname" + address.getCountryName());
                    extra.append("countryCode" + address.getCountryCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Pair<Double, Double> getLocationInfo() {
        if (!Double.isNaN(gpsLongitude) && !Double.isNaN(gpsLatitude)) {
            return new Pair<>(gpsLongitude, gpsLatitude);
        }
        if (!Double.isNaN(netWorkLongitude) && !Double.isNaN(netWorkLatitude)) {
            return new Pair<>(netWorkLongitude, netWorkLatitude);
        }
        return new Pair<>(0d, 0d);
    }

    public void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(netWorkLocationListener);
            locationManager.removeUpdates(gpsLocationListener);
        }
    }
}
