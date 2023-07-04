package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.home.MainActivity
import com.loan.icreditapp.ui.login.Login2Activity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LauncherActivity : BaseActivity() {

    private val TAG = "LauncherActivity"

    private val TO_WELCOME_PAGE = 111

    private val TO_MAIN_PAGE = 112

    private var requestTime: Long = 0

    private var mHandler: Handler? = null

    init {
        mHandler = Handler(
            Looper.getMainLooper()
        ) { message ->
            when (message.what) {
                TO_WELCOME_PAGE -> {
                    mHandler?.removeCallbacksAndMessages(null)
                    OkGo.getInstance().cancelTag(TAG)
//                    val login2Intent = Intent(this@LauncherActivity, WelcomeActivity::class.java)
                    val login2Intent = Intent(this@LauncherActivity, Login2Activity::class.java)
                    startActivity(login2Intent)
                    finish()
                }
                TO_MAIN_PAGE -> {
                    mHandler?.removeCallbacksAndMessages(null)
                    OkGo.getInstance().cancelTag(TAG)
                    val mainIntent = Intent(this@LauncherActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(R.layout.activity_launcher)
        //        boolean openGuide = SPUtils.getInstance().getBoolean(KEY_GUIDE, true);
//        boolean openGuide = false;
//        if (openGuide) {
//            SPUtils.getInstance().put(KEY_GUIDE, false);
//        }
        val httpHeaders = BuildRequestJsonUtils.buildHeadersNonLogin()
        OkGo.getInstance().addCommonHeaders(httpHeaders)
        val accountId = SPUtils.getInstance().getString(Constant.KEY_ACCOUNT_ID)
        val token = SPUtils.getInstance().getString(Constant.KEY_TOKEN)
        val mobile = SPUtils.getInstance().getString(Constant.KEY_MOBILE)
        val lastLoginTime = SPUtils.getInstance().getLong(Constant.KEY_LOGIN_TIME, 0L)
        var canUseToken = true
        if (lastLoginTime > 0 && System.currentTimeMillis() >= lastLoginTime) {
            val deltaTime = System.currentTimeMillis() - lastLoginTime
            if (deltaTime >= 30 * 24 * 60 * 60 * 1000) {
                canUseToken = false
            }
        }
        Constant.mToken = token
//        Log.e(TAG, " token = " + token)

        if (TextUtils.isEmpty(accountId) || TextUtils.isEmpty(token)
            || TextUtils.isEmpty(mobile) || !canUseToken) {
//        if (BuildConfig.DEBUG) {
            if (!canUseToken && BuildConfig.DEBUG) {
                ToastUtils.showShort("token desire")
            }
            LogSaver.logToFile("auto login token has desire")
            mHandler?.sendEmptyMessageDelayed(TO_WELCOME_PAGE, 1000)
        } else {
            val httpHeaders = BuildRequestJsonUtils.buildHeaderToken()
            OkGo.getInstance().addCommonHeaders(httpHeaders)
            requestDetail(accountId!!, token!!, mobile!!)
            mHandler?.sendEmptyMessageDelayed(TO_WELCOME_PAGE, 3000)
        }
    }

    private fun requestDetail(accountId: String, token: String, mobile : String) {
        requestTime = System.currentTimeMillis()
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", accountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " launcher activity ... = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.GET_ORDER_INFO).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    var successEnter = false
                    val body = response.body()
                    if (!TextUtils.isEmpty(body)) {
                        val orderInfo: OrderInfoBean? = CheckResponseUtils.checkResponseSuccess(
                            response, OrderInfoBean::class.java
                        )
                        if (orderInfo != null) {
                            Constant.mLaunchOrderInfo = orderInfo
                            successEnter = true
                            Constant.mAccountId = accountId
                            Constant.mToken = token
                            Constant.mMobile = mobile
                        }
                    }
                    mHandler?.sendEmptyMessageDelayed(if (successEnter) TO_MAIN_PAGE else TO_WELCOME_PAGE,100)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    mHandler?.sendEmptyMessage(TO_WELCOME_PAGE)
                }
            })
    }

    override fun onDestroy() {
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}