package com.loan.icreditapp.collect

import android.Manifest
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.global.Constant
import java.util.regex.Matcher
import java.util.regex.Pattern


object ReadSmsMgr {

    private var isExecuting = false
    private val TYPE_1 = 1111

    private var mHandler : Handler? = null
    private var mObserver : Observer? = null

    init {
        val thread = HandlerThread("ReadSmsThread")
        thread.start()
        mHandler = Handler(thread.looper,  object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                if (!isExecuting){
                    return false
                }
                var hasPermissions: Boolean = PermissionUtils.isGranted(Manifest.permission.READ_SMS)
                if (Constant.IS_COLLECT) {
                    LogSaver.logToFile(" has permissions " + hasPermissions)
                }
                if (!hasPermissions) {
                    return false
                }
                when (msg.what) {
                    (TYPE_1) -> {
//                        Log.e("Test", " 111 = " + Thread.currentThread())

                        val authCode = readSms()
                        if (!TextUtils.isEmpty(authCode)){
                            mObserver?.onReceiveAuthCode(authCode)
                        }
                        mHandler?.removeMessages(TYPE_1)
                        mHandler?.sendEmptyMessageDelayed(TYPE_1, 2000)
                    }
                }
                return false
            }

        })
    }

    fun onResume() {
        isExecuting = true
        mHandler?.removeMessages(TYPE_1)
        mHandler?.sendEmptyMessage(TYPE_1)
    }

    fun setObserver(observer: Observer){
        mObserver = observer
    }

    fun onDestroy() {
        isExecuting = false
        mHandler?.removeCallbacksAndMessages(null)
    }

    interface Observer {
        fun onReceiveAuthCode(authCode : String)
    }

    private fun readSms(): String {
        val uri = Uri.parse("content://sms/")
        val projection =
            arrayOf("_id", "address", "person", "body", "date", "type", "status", "read")
        val resolver = Utils.getApp().contentResolver
        val cursor = resolver.query(uri, projection, null, null, null)
        try {
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                val authCode = handleSms(cursor)
                if (!TextUtils.isEmpty(authCode)){
                    return authCode
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
            LogSaver.logToFile("read sms exception = " + e.toString())
        } finally {
            cursor?.close()
        }
        return ""
    }

    private fun handleSms(cursor: Cursor): String {
        val address = cursor.getString(1) //电话号码
        val body = cursor.getString(3) //短信内容
        val date = cursor.getLong(4)
        val type = cursor.getInt(5)
        val status = cursor.getInt(6)
        val read = cursor.getInt(7)

        val TEMPLATE = "Your authentication number is"
        if (!TextUtils.isEmpty(body) && body.contains(TEMPLATE)) {
            val pattern: Pattern = Pattern.compile("[^0-9]") //正则表达式.

            val matcher: Matcher = pattern.matcher(body.toString())
            val result = matcher.replaceAll("")
            if (result.length == 6) {
                try {
                    Integer.parseInt(result)
                    val delta = Math.abs(System.currentTimeMillis() - date)
                    //十分钟内的短信才行
                    if (delta < 10 * 60 * 1000){
                        return result
                    }
//                   Log.e("Test"," date = " +date)
//                   Log.e("Test"," cur time = " +System.currentTimeMillis())

                } catch (e: Exception) {

                }
            }
        }

        return ""
    }

}