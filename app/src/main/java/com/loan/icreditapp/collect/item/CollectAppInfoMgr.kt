package com.loan.icreditapp.collect.item

import android.content.pm.ApplicationInfo
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.collect.BaseCollectDataMgr
import com.loan.icreditapp.collect.bean.AppInfoRequest
import com.loan.icreditapp.util.EncodeUtils
import java.util.ArrayList

class CollectAppInfoMgr {
    companion object {
        private const val TAG = "CollectAppInfoMgr"

        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectAppInfoMgr()
        }
    }

    init {
        getAppInfoAesStr()
    }

    var aesAppInfoStr :String? = null

    fun getAppInfoAesStr() : String{
        if (aesAppInfoStr == null){
            getAppInfoAesStrInternal()
            if (BuildConfig.DEBUG) {
                Log.e("Test", "cache appinfo success = " + aesAppInfoStr?.length)
            }
        }
        return aesAppInfoStr!!
    }

    private fun getAppInfoAesStrInternal(){
        val originAppInfo = GsonUtils.toJson(readAllAppInfo())
        val tempAppInfo = EncodeUtils.encryptAES(originAppInfo)
        aesAppInfoStr = if (TextUtils.isEmpty(tempAppInfo)) "" else tempAppInfo
    }

    private fun readAllAppInfo(): ArrayList<AppInfoRequest> {
        val list: ArrayList<AppInfoRequest> = ArrayList<AppInfoRequest>()
        try {
            val pm = Utils.getApp().packageManager ?: return list
            val installedPackages = pm.getInstalledPackages(0)
            for (i in installedPackages.indices) {
                val packageInfo = installedPackages[i]
                val appInfoRequest = AppInfoRequest()
                appInfoRequest.pkgname = BaseCollectDataMgr.encodeData(packageInfo.packageName)
                appInfoRequest.installtime = packageInfo.firstInstallTime
                appInfoRequest.installtime_utc =
                    BaseCollectDataMgr.local2UTC(packageInfo.firstInstallTime)
                val ai = packageInfo.applicationInfo
                if (ai != null) {
                    val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
                    appInfoRequest.type = if (isSystem) "0" else "1"
                    try {
                        appInfoRequest.appname =
                            BaseCollectDataMgr.encodeData1(ai.loadLabel(pm).toString())
                    } catch (e: Exception) {
                    }
                }
                list.add(appInfoRequest)
            }
        }catch (e : Exception ){
            if (BuildConfig.DEBUG){
                throw e
            }
        }
        return list
    }
}