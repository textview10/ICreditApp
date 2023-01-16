package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.ServerLiveBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.login.SignInActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LauncherActivity : BaseActivity() {

    private val TAG = "LauncherActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        checkServerAvailable(object : CallBack {
            override fun onEnd() {
//                startActivity(Intent(this@LauncherActivity, SignInActivity::class.java))
//                finish()
            }
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
            .params("data", jsonObject.toString()) //                .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val serverLiveBean: ServerLiveBean? =
                        checkResponseSuccess(response, ServerLiveBean::class.java)
                    if (serverLiveBean != null && serverLiveBean.isServerLive()) {
                        Log.d(TAG, "the server is alive")
                    }
                    callBack?.onEnd()
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