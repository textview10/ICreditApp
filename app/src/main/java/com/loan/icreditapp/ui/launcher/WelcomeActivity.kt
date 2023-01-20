package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.ServerLiveBean
import com.loan.icreditapp.ui.login.SignInActivity
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class WelcomeActivity : BaseActivity() {
    private val TAG = "WelcomeActivity"

    private var tvRegister: AppCompatTextView? = null
    private var tvSignIn: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(android.R.color.transparent))
        BarUtils.setNavBarLightMode(this, false)
        setContentView(R.layout.activity_splash)
        initializeView()

    }

    private fun initializeView() {
        tvRegister = findViewById(R.id.tv_splash_register)
        tvSignIn = findViewById(R.id.tv_splash_signin)

        tvRegister?.setOnClickListener(View.OnClickListener {
            checkServerAvailable(object : CallBack {
                override fun onEnd() {
                    SignUpActivity.startActivity(this@WelcomeActivity, SignUpActivity.SIGNUP_1)
                    finish()
                }
            })
        })
        tvSignIn?.setOnClickListener(View.OnClickListener {
            checkServerAvailable(object : CallBack {
                override fun onEnd() {
                    SignInActivity.startActivity(this@WelcomeActivity)
                    finish()
                }
            })
        })
    }

    private fun checkServerAvailable(callBack: CallBack?) {
        val jsonObject: JSONObject? = BuildRequestJsonUtils.buildRequestJson()
        try {

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        Log.e(TAG, " = " + jsonObject.toString());
        OkGo.post<String>(Api.CHECK_SERVER_ALIVE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val serverLiveBean: ServerLiveBean? =
                        checkResponseSuccess(response, ServerLiveBean::class.java)
                    if (serverLiveBean != null && serverLiveBean.isServerLive()) {
                        Log.d(TAG, "the server is alive")
                        callBack?.onEnd()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "the server is not alive")
                    ToastUtils.showShort("server is not alive .")
                }
            })
    }

    interface CallBack {
        fun onEnd()
    }
}