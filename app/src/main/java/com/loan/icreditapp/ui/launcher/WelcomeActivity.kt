package com.loan.icreditapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.bean.ServerLiveBean
import com.loan.icreditapp.dialog.term.TermsDialog
import com.loan.icreditapp.ui.login.SignInActivity
import com.loan.icreditapp.ui.login.SignUpActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.ToGooglePlayUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class WelcomeActivity : BaseActivity() {
    private val TAG = "WelcomeActivity"

    private var tvRegister: AppCompatTextView? = null
    private var tvSignIn: AppCompatTextView? = null
    private var dialog : TermsDialog? = null

    private val KEY_SHOW_TERM = "key_show_term"

    private var hasShowTerm = false

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
        hasShowTerm = SPUtils.getInstance().getBoolean(KEY_SHOW_TERM)

//        ToGooglePlayUtils.transferToGooglePlay(WelcomeActivity@this)
        dialog = TermsDialog(this)
        tvRegister?.setOnClickListener(View.OnClickListener {
            checkServerAvailable(object : CallBack {
                override fun onEnd() {
                    if (isFinishing || isDestroyed){
                        return
                    }
                    if (hasShowTerm){
                        SignUpActivity.startActivity(this@WelcomeActivity, SignUpActivity.SIGNUP_NEW)
                        finish()
                        return
                    }
                    showTermDialog( object :TermsDialog.OnClickAgreeListener{
                        override fun onClickAgree() {
                            SPUtils.getInstance().put(KEY_SHOW_TERM, true)
                            hasShowTerm = true
                            SignUpActivity.startActivity(this@WelcomeActivity, SignUpActivity.SIGNUP_NEW)
                            finish()
                        }
                    })
                }
            })
        })
        tvSignIn?.setOnClickListener(View.OnClickListener {
            checkServerAvailable(object : CallBack {
                override fun onEnd() {
                    if (isFinishing || isDestroyed){
                        return
                    }
                    if (hasShowTerm){
                        SignInActivity.startActivity(this@WelcomeActivity)
                        finish()
                        return
                    }
                    showTermDialog( object :TermsDialog.OnClickAgreeListener{
                        override fun onClickAgree() {
                            SPUtils.getInstance().put(KEY_SHOW_TERM, true)
                            hasShowTerm = true
                            SignInActivity.startActivity(this@WelcomeActivity)
                            finish()
                        }
                    })
                }
            })
        })
    }

    private fun showTermDialog(listener: TermsDialog.OnClickAgreeListener) {
        if (dialog == null){
            dialog = TermsDialog(this)
        }
        dialog?.setOnClickListener(listener)
        if (dialog?.isShowing == true){
            return
        }
        dialog?.show()
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

    override fun onDestroy() {
        if (dialog != null){
            dialog?.onDestroyDialog()
        }
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}