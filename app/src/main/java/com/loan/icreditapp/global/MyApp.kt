package com.loan.icreditapp.global

import android.R
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import co.paystack.android.PaystackSdk
import com.blankj.utilcode.util.LanguageUtils
import com.loan.icreditapp.collect.LocationMgr
import com.loan.icreditapp.util.EncodeUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.model.HttpHeaders
import com.newton.utils.GooglePlaySdk
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class MyApp : Application() {

    companion object {
        var mContext: Context? = null
    }
    override fun onCreate() {
        super.onCreate()
        mContext = this
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            //                layout.setPrimaryColorsId(R.color.bg_color, android.R.color.white);//全局设置主题颜色
            layout.setPrimaryColorsId(R.color.white, R.color.white) //全局设置主题颜色
            MaterialHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20f)
        }
//        FirebaseApp.initializeApp(this)
        initOkGo()
        //        CrashHandler.getInstance().init(this);  //捕获异常, 并杀掉app
        initializeData()
        LanguageUtils.applyLanguage(Locale.ENGLISH, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private fun initOkGo() {
        val builder = OkHttpClient.Builder()
        //全局的读取超时时间
        builder.readTimeout(20000, TimeUnit.MILLISECONDS)
        //全局的写入超时时间
        builder.writeTimeout(20000, TimeUnit.MILLISECONDS)
        //全局的连接超时时间
        builder.connectTimeout(20000, TimeUnit.MILLISECONDS)
        //使用数据库保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(CookieJarImpl(DBCookieStore(this)))
        val loggingInterceptor = HttpLoggingInterceptor("OkHttpClient")
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        OkGo.getInstance().init(this).okHttpClient = builder.build()
        val httpHeaders = HttpHeaders()
//        httpHeaders.put("APP-Language", "en")
//        httpHeaders.put("APP-ID", "1111")
        httpHeaders.put("Accept", "application/json")
        httpHeaders.put("User-Agent", "retrofit")
        OkGo.getInstance().addCommonHeaders(httpHeaders)
//        EncodeUtils.mainTest()
        GooglePlaySdk.getInstance(this)?.start()
    }


    private fun initializeData() {
        LocationMgr.getInstance().init(this)
        PaystackSdk.initialize(applicationContext)
        PaystackSdk.setPublicKey("pk_live_eaeaf5fb4ed183a337f40008b7a9b3d663f7b34e")
        FireBaseMgr.sInstance.getToken()
    }

    override fun onTerminate() {
//        LocationMgr.getInstance().onDestroy();
        super.onTerminate()
    }
}