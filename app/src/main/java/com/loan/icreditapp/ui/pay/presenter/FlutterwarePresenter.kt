package com.loan.icreditapp.ui.pay.presenter

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.GsonUtils
import com.flutterwave.raveandroid.RaveUiManager
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.pay.FlutterwareResponse1Bean
import com.loan.icreditapp.bean.pay.FlutterwareResponse2Bean
import com.loan.icreditapp.bean.pay.FlutterwareResultBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.PayFragment
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import net.entity.bean.FlutterWaveResult
import org.json.JSONException
import org.json.JSONObject

class FlutterwarePresenter : BasePresenter {

    private var mTxRef : String? = null
    private var mBean : FlutterWaveResult? = null

    constructor(payFragment: Fragment) : super(payFragment) {

    }

    override fun requestUrl(orderId: String?, amount: String?) {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("token", Constant.mToken)
            jsonObject.put("orderId", orderId)
            jsonObject.put("chargeType", "2")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_TEXT_REF).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var flutterware1Bean = CheckResponseUtils.checkResponseSuccess(response, FlutterwareResponse1Bean::class.java)
                    if (flutterware1Bean == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.isEmpty(flutterware1Bean.txRef)){
                        mObserver?.repayFailure(response, true, "request getTextRef = null")
                        return
                    }
                    var card = flutterware1Bean.card
                    if (card == null){
                        card = true
                    }
                    var account = flutterware1Bean.account
                    if (account == null){
                        account = false
                    }
                    var transfer = flutterware1Bean.transfer
                    if (transfer == null){
                        transfer = false
                    }
                    var ussd = flutterware1Bean.ussd
                    if (ussd == null){
                        ussd = false
                    }
                    mTxRef = flutterware1Bean.txRef
                    RaveUiManager(mPayFragment!!.activity).acceptAccountPayments(account).acceptCardPayments(card)
                        .acceptBankTransferPayments(transfer).acceptUssdPayments(ussd)
                        .setAmount(flutterware1Bean.amount!!.toDouble()).setCurrency(flutterware1Bean.currency).setfName(flutterware1Bean.firstName)
                        .setlName(flutterware1Bean.lastName).setEmail(flutterware1Bean.email).setPublicKey(flutterware1Bean.publicKey)
                        .setEncryptionKey(flutterware1Bean.encryptionKey).setTxRef(flutterware1Bean.txRef).onStagingEnv(false).
                        withTheme(R.style.MyCustomTheme).initialize()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "request getTextRef failure")
                }
            })
    }


    fun setFlutterwareBean(bean: FlutterWaveResult) {
        mBean = bean
        uploadJson()
    }

    private fun uploadJson(){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("chargeType", "2")
            jsonObject.put("txRef", mTxRef)
            jsonObject.put("jsonStr", GsonUtils.toJson(mBean))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_JSON).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var flutterware2Bean = CheckResponseUtils.checkResponseSuccess(response, FlutterwareResponse2Bean::class.java)
                    if (flutterware2Bean == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    val isSuccess = TextUtils.equals(flutterware2Bean.status, "1") || TextUtils.equals(flutterware2Bean.status, "success")
                    if (isSuccess){
                        updateResult()
                    } else {
                        mObserver?.repayFailure(response, true, "flutterwave uploadJson status != 1")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "flutter uploadJson error")
                }
            })
    }

    override fun updateResult() {
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("txRef", mTxRef)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_FLUTTER_WAVE_RESULT).tag(PayFragment.TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()){
                        return
                    }
                    var flutterwareResult = CheckResponseUtils.checkResponseSuccess(response, FlutterwareResultBean::class.java)
                    if (flutterwareResult == null){
                        mObserver?.repayFailure(response, false, null)
                        return
                    }
                    if (TextUtils.equals(flutterwareResult.status, "1")){
                        mObserver?.repaySuccess()
                    } else {
                        mObserver?.repayFailure(response, true, "flutterwave status != 1")
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()){
                        return
                    }
                    mObserver?.repayFailure(response, true, "request flutterwave error")
                }
            })
    }
}