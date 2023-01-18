package com.loan.icreditapp.ui.home

import android.nfc.Tag
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.login.SignInBean
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class SettingFragment : BaseFragment() {

    private val TAG = "SettingFragment"

    private var llMyloan:LinearLayout? = null
    private var llMyProfile:LinearLayout? = null
    private var llCard:LinearLayout? = null
    private var llBankAccount:LinearLayout? = null
    private var llMessage:LinearLayout? = null
    private var llHelp:LinearLayout? = null
    private var llAbout:LinearLayout? = null
    private var llLogout:LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llMyloan = view.findViewById(R.id.ll_setting_my_loan)
        llMyProfile = view.findViewById(R.id.ll_setting_my_profile)
        llCard = view.findViewById(R.id.ll_setting_card)
        llBankAccount = view.findViewById(R.id.ll_setting_bank_account)
        llMessage = view.findViewById(R.id.ll_setting_message)
        llHelp = view.findViewById(R.id.ll_setting_help)
        llAbout = view.findViewById(R.id.ll_setting_about)
        llLogout = view.findViewById(R.id.ll_setting_logout)

        llMyloan?.setOnClickListener(View.OnClickListener {

        })
        llMyProfile?.setOnClickListener(View.OnClickListener {

        })
        llCard?.setOnClickListener(View.OnClickListener {

        })
        llBankAccount?.setOnClickListener(View.OnClickListener {

        })

        llMessage?.setOnClickListener(View.OnClickListener {

        })
        llHelp?.setOnClickListener(View.OnClickListener {

        })
        llAbout?.setOnClickListener(View.OnClickListener {

        })
        llLogout?.setOnClickListener(View.OnClickListener {
            logOut()
        })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    private fun logOut(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        OkGo.post<String>(Api.LOGOUT).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val baseResponseBean: BaseResponseBean? =
                        checkResponseSuccess(response, BaseResponseBean::class.java)
                    if (baseResponseBean == null ) {
                        ToastUtils.showShort("logout failure.")
                        return
                    }
                    if (!baseResponseBean.isRequestSuccess()) {
                        ToastUtils.showShort("request logout failure.")
                        return
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "logout error .")
                }
            })
    }

    private fun requestMessageList(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        OkGo.post<String>(Api.REQUEST_MESSAGE_LIST).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val baseResponseBean: BaseResponseBean? =
                        checkResponseSuccess(response, BaseResponseBean::class.java)
                    if (baseResponseBean == null ) {
                        ToastUtils.showShort("logout failure.")
                        return
                    }
                    if (!baseResponseBean.isRequestSuccess()) {
                        ToastUtils.showShort("request logout failure.")
                        return
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "logout error .")
                }
            })
    }
}