package com.loan.icreditapp.collect

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ThreadUtils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.collect.bean.HardwareResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.global.MyApp
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.util.*


class CollectHardwareMgr {

    companion object {
        private const val TAG = "CollectHardwareMgr"
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectHardwareMgr()
        }
    }

    fun collectHardware(activity: Activity?, observer: Observer?) {
        var startMillions = System.currentTimeMillis()
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<Exception?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Exception? {
                try {
                    val duration = (System.currentTimeMillis() - startMillions)
                    logFile(" start collect hardware start = " + duration)

                    val jsonObject = buildRequestJsonObj(activity)
                    var jsonStr : String? = null
                    try {
                        jsonStr = jsonObject.toString()
                        jsonObject.put("jsonData", jsonStr) //以上参数的json集合
                    } catch ( e : Exception) {

                    }
                    getHardware(jsonObject,jsonStr, observer)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    }
                    return e
                }
                return null
            }


            override fun onSuccess(result: Exception?) {
                if (result != null){
                    observer?.failure("collect data exception = " + result.toString())
                }
            }

        })
    }

    private fun buildRequestJsonObj(context: Activity?) : JSONObject{
        val jsonObject = JSONObject()
        jsonObject.put("accountId", Constant.mAccountId)    //用户ID
        jsonObject.put("uniqDeviceId", DeviceUtils.getUniqueDeviceId()) //用户设备唯一标示
        jsonObject.put("osBoard", Build.BOARD)  //获取设备基板名称
        jsonObject.put("osBootLoader", Build.BOOTLOADER)       //获取设备引导程序版本号
        jsonObject.put("osBrand", Build.BRAND) //获取设备品牌
        val abIs = DeviceUtils.getABIs()
        if (abIs != null) {
            val abi1 = abIs[0]
            jsonObject.put("osCpuAbi", abi1) //获取设备指令集名称（CPU的类型）
            if (abIs.size > 1) {
                jsonObject.put("osCpuAbi2", abIs[1]) //获取第二个指令集名称
            }
        }
        jsonObject.put("osDevice", Build.DEVICE) //获取设备驱动名称
        jsonObject.put("osDisplay", Build.DISPLAY) //获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
        jsonObject.put("osHardware", Build.HARDWARE) //设备硬件名称,一般和基板名称一样（BOARD）
        jsonObject.put("osHost", Build.HOST) //设备主机地址
        jsonObject.put("osId", Build.ID) //设备版本号
        jsonObject.put("osModel", DeviceUtils.getModel()) //获取手机的型号 设备名称
        jsonObject.put("osManufacturer", DeviceUtils.getManufacturer()) //获取设备制造商
        jsonObject.put("osProduct", Build.PRODUCT) //整个产品的名称
        jsonObject.put("osTags", Build.TAGS) //设备标签。如release-keys 或测试的 test-keys
        jsonObject.put("osTime", Build.TIME) //时间
        jsonObject.put("osType", Build.TYPE) //设备版本类型  主要为"user" 或"eng".
        jsonObject.put("osUser", Build.USER) //设备用户名 基本上都为android-build
        jsonObject.put("osTime", Build.TIME) //时间
        jsonObject.put("osvRelease", Build.VERSION.RELEASE) //获取系统版本字符串。如4.1.2 或2.2 或2.3等
        jsonObject.put("osvCodename", Build.VERSION.CODENAME) //设备当前的系统开发代号，一般使用REL代替
        jsonObject.put("osvIncremental", Build.VERSION.INCREMENTAL) //系统源代码控制值，一个数字或者git hash值
        jsonObject.put("osvSdk", Build.VERSION.SDK) //系统的API级别 一般使用下面大的SDK_INT 来查看
        jsonObject.put("osvSdkInt", DeviceUtils.getSDKVersionCode()) //系统的API级别 数字

        try {
            if (context != null) {
                val dm = DisplayMetrics()//屏幕度量
                context?.windowManager?.defaultDisplay?.getMetrics(dm)

                val wm : WindowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay

                val screen_width = display.width
                val screen_height = display.height

                val physicalWidth = screen_width / dm.xdpi
                val physicalHeight = screen_height / dm.ydpi
                val physicalSize = Math.sqrt(Math.pow(physicalWidth.toDouble(), 2.0) + Math.pow(
                    physicalHeight.toDouble(), 2.0
                ))

                jsonObject.put("device_width", screen_width) //分辨率
                jsonObject.put("device_height", screen_height) //分辨率
                jsonObject.put("physical_size", physicalSize)
            }

            val currentLocale = Locale.getDefault()
            val currentLanguage = currentLocale.getLanguage()
            val currentCountry = currentLocale.getCountry()

            jsonObject.put("language", currentLanguage + "_" + currentCountry)

            var is_usb_debug : Boolean = false
            MyApp.mContext?.let {
                is_usb_debug = (Settings.Secure.getInt(MyApp.mContext?.contentResolver, Settings.Secure.ADB_ENABLED, 0) > 0)
            }
            jsonObject.put("is_usb_debug", is_usb_debug)

            jsonObject.put("mac", DeviceUtils.getMacAddress())
            var netWorkType : String = ""
            when (NetworkUtils.getNetworkType()) {
                (NetworkUtils.NetworkType.NETWORK_ETHERNET) -> {
                    netWorkType = "ethernet"
                }
                (NetworkUtils.NetworkType.NETWORK_WIFI) -> {
                    netWorkType = "wifi"
                }
                (NetworkUtils.NetworkType.NETWORK_4G) -> {
                    netWorkType = "4g"
                }
                (NetworkUtils.NetworkType.NETWORK_3G) -> {
                    netWorkType = "3g"
                }
                (NetworkUtils.NetworkType.NETWORK_2G) -> {
                    netWorkType = "2g"
                }
                (NetworkUtils.NetworkType.NETWORK_UNKNOWN) -> {
                    netWorkType = "unknow"
                }
                (NetworkUtils.NetworkType.NETWORK_NO) -> {
                    netWorkType = "no"
                }
            }
            jsonObject.put("network_type", netWorkType)
            var result : String? = HardWareUtils.getSimOperatorName(context)
            if (TextUtils.isEmpty(result)) {
                result = HardWareUtils.getSimOperator(  context)
            }
            jsonObject.put("network_operator_name", result)

            MyApp.mContext?.let {
                val am : ActivityManager = it.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                val outInfo = ActivityManager.MemoryInfo();
                am.getMemoryInfo(outInfo)
                val availMem = outInfo.availMem
                val totalMem = outInfo.totalMem
                jsonObject.put("ram_total_size", totalMem)
                //内存可用大小
                jsonObject.put("ram_usable_size", availMem)
            }

        } catch ( e : Exception) {

        }
        return jsonObject
    }


    @SuppressLint("MissingPermission")
    private fun getHardware(
        jsonObject: JSONObject, jsonStr : String?,  observer: Observer?) {
        logFile(" start upload hardware .")
        val startMillions = System.currentTimeMillis()
        OkGo.post<String>(Api.UPLOAD_HARD_WARE).tag(TAG).upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                        Log.i(TAG, " response success= " + response.body());
                    val totalDur = (System.currentTimeMillis() - startMillions)
                    logFile(" start upload hardware success =  $totalDur")
                    val bean: HardwareResponseBean? = CheckResponseUtils.checkResponseSuccess(
                        response,
                        HardwareResponseBean::class.java
                    )
                    if (bean != null && bean.isSuccess == true) {
                        observer?.success(response)
                        if (Constant.IS_COLLECT) {
                            logFile(jsonStr)
                        }
//                        FirebaseUtils.logEvent("fireb_upload_auth_duration", "uploadAuthDur", totalDur.toString()
//                            ,"totalDur" , (System.currentTimeMillis() - startMillions1).toString())
//                        log2File(originSms, originContract, originAppInfo, "")
                    } else {
                        var errorMsg: String? = null
                        try {
                            if (bean != null) {
                                errorMsg = GsonUtils.toJson(bean)
                            }
                        } catch (e: Exception) {

                        }
                        observer?.failure(errorMsg)
                        if (!TextUtils.isEmpty(errorMsg)) {
                            logFile(errorMsg!!)
                        }
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    var errorMsg: String? = null
                    try {
                        errorMsg = response.body().toString()
                    } catch (e: Exception) {

                    }
                    observer?.failure(errorMsg)
                    logFile("start upload auth failure =  " + (System.currentTimeMillis() - startMillions) + errorMsg)
                }
            })
    }

    interface Observer {
        fun success(response: Response<String>?)
        fun failure(response: String?)
    }

    fun logFile(str : String?){
        if (TextUtils.isEmpty(str)) {
            return
        }
        if (BuildConfig.DEBUG) {
            Log.e("Test", str!!)
        }
        LogSaver.logToFile("Collect hareware  $str")
    }


}