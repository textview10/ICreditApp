package com.loan.icreditapp.ui.card.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.exceptions.ExpiredAccessCodeException
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.drojian.alpha.toolslib.log.LogSaver
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.bank.AccessCodeResponseBean
import com.loan.icreditapp.bean.bank.UploadCardResponseBean
import com.loan.icreditapp.event.UpdateLoanEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.card.BindNewCardActivity
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.ui.widget.BlankTextWatcher
import com.loan.icreditapp.ui.widget.ExpiryTextWatcher
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Logger
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat


class AddBankNum2Fragment : BaseFragment() {

    private val TAG = "AddBankNum2Fragment"

    private var editBankNum: EditTextContainer? = null
    private var etChooseDate: AppCompatEditText? = null
    private var etCvv: AppCompatEditText? = null

    private var flCommit: FrameLayout? = null
    private var flLoading: FrameLayout? = null

    private val TYPE_SHOW_LOADING = 111
    private val TYPE_HIDE_LOADING = 112
    private val TYPE_SHOW_TOAST = 113
    private var toastMsg : String? = null

    private val mHandler = Handler(Looper.getMainLooper(), object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what){
                TYPE_SHOW_LOADING ->{
                    flLoading?.visibility = View.VISIBLE
                }
                TYPE_HIDE_LOADING ->{
                    flLoading?.visibility = View.GONE
                }
                TYPE_SHOW_TOAST ->{
                    if (!TextUtils.isEmpty(toastMsg)){
                        ToastUtils.showShort(toastMsg)
                    }
                }
            }
           return false
        }

    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_banknum, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editBankNum = view.findViewById(R.id.edit_container_add_bank_card_banknum)
        etChooseDate = view.findViewById(R.id.et_add_bank_card_choose_date)
        etCvv = view.findViewById(R.id.et_add_bank_card_cvv)
        flCommit = view.findViewById(R.id.fl_add_bank_num_commit)
        flLoading = view.findViewById(R.id.fl_add_bank_num_loading)

        var expiryTextWatcher = ExpiryTextWatcher(etChooseDate)
        etChooseDate?.addTextChangedListener(expiryTextWatcher)

        val editText : AppCompatEditText? = editBankNum?.getEditText()
        if (editText != null){
            var blankTextWatcher = BlankTextWatcher(editText)
            editText.addTextChangedListener(blankTextWatcher)
        }
        editBankNum?.setInputNum()
//
        flCommit?.setOnClickListener(OnClickListener {
            if (checkBankNum()) {
                val cardNum = editBankNum?.getText()?.replace(" ", "")
                val expireDate = etChooseDate?.text
                val cvv = etCvv?.text
                val expireList = expireDate?.split("/")
                if (expireList?.size == 2) {
                    var first : Int? = null
                    var second : Int? = null
                    try {
                        first = Integer.parseInt(expireList[0])
                        second = Integer.parseInt(expireList[1])
                    } catch (e : Exception) {

                    }
                    if (first == null || second == null) {
                        ToastUtils.showShort("expiry date is not correct")
                        return@OnClickListener
                    }
                    Log.e(TAG, " first = " + first + " second = " + second
                            + " cardNum = " + cardNum)
                    bindCardAccess(cardNum.toString(), cvv.toString(), first, second)
                }
            }
        })
    }

    private fun bindCardAccess(
        cardNum: String, cvc: String,
        mouth: Int, year: Int
    ) {
        val card: Card = Card.Builder(cardNum, mouth, year, cvc).build()
        Log.e(TAG, " is valid = " + card.isValid)
        val charge = Charge()
        charge.card = card
        getAccessCode(charge, cardNum, cvc, mouth, year)
    }

    private fun checkBankNum(): Boolean {
        if (editBankNum == null || TextUtils.isEmpty(editBankNum?.getText())) {
            ToastUtils.showShort("card num is empty")
            return false
        }
        if (etChooseDate == null || TextUtils.isEmpty(etChooseDate?.getText())) {
            ToastUtils.showShort("bank num is empty")
            return false
        }
        if (etCvv == null || TextUtils.isEmpty(etCvv?.text)) {
            ToastUtils.showShort("bank cvv is empty")
            return false
        }

//        expiryMonth : Int, expiryYear : Int
        return true
    }

    fun getAccessCode(charge: Charge,  cardNum: String, cvc: String,
                      mouth: Int, year: Int) {
        mHandler?.sendEmptyMessage(TYPE_SHOW_LOADING)
        var jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_ACCESS_CODE).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    mHandler?.sendEmptyMessage(TYPE_HIDE_LOADING)
                    val responseBean: AccessCodeResponseBean? =
                        checkResponseSuccess(response, AccessCodeResponseBean::class.java)
                    if (responseBean == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get access code ." + response.body())
                        }
                        errorMsg("get access code failure", response, jsonObject)
                        return
                    }
                    charge.accessCode = responseBean.accessCode
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "access code = " + charge.accessCode)
                    }
                    chargeCard(charge, cardNum, cvc, mouth, year)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    mHandler?.sendEmptyMessage(TYPE_HIDE_LOADING)
                    errorMsg("get access code error", response, jsonObject)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "get bank list = " + response.body())
                    }
                }
            })
    }

    private fun chargeCard(charge: Charge , cardNum: String, cvc: String,
                           first: Int, second: Int) {
//        PaystackSdk.setPublicKey()
        mHandler?.sendEmptyMessage(TYPE_SHOW_LOADING)
        PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {
            override fun onSuccess(transaction: Transaction) {
                mHandler?.sendEmptyMessage(TYPE_HIDE_LOADING)
                Log.e(TAG, "onSuccess:    " + transaction.getReference())
//                verifyOnServer(transaction.getReference())
                uploadCard(transaction.getReference(), cardNum, cvc, first, second)
            }

            //此函数只在请求OTP保存引用之前调用，如果需要，可以解除otp验证
            override fun beforeValidate(transaction: Transaction) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "beforeValidate: " + transaction.getReference())
                }
            }

            override fun onError(error: Throwable, transaction: Transaction?) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                mHandler?.sendEmptyMessage(TYPE_HIDE_LOADING)
                errorMsg("bind card failure pay stack error " + error.message, error.message)
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, " = " + transaction.toString(), error)
                }
            }
        })
    }

    private fun uploadCard(reference : String, cardNum: String, cvc: String,
                           expiryMonth: Int, expiryYear: Int){
//                expireDate	String	Y	过期日	格式 MM/YY
//                	String	Y	CVV-卡片背面后三位数字
//        reference	String	Y	交易号
        mHandler?.sendEmptyMessage(TYPE_SHOW_LOADING)
        var jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("cardNumber", cardNum)
            //卡号
            jsonObject.put("expireDate", expiryMonth.toString() + "/" + expiryYear)
            jsonObject.put("cvv", cvc)
            jsonObject.put("reference", reference)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.UPLOAD_CARD).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    mHandler?.sendEmptyMessage(TYPE_HIDE_LOADING)
                    val responseBean: UploadCardResponseBean? =
                        checkResponseSuccess(response, UploadCardResponseBean::class.java)
                    if (responseBean == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get access code ." + response.body())
                        }
                        errorMsg("upload bank card failure " , response, jsonObject)
                        return
                    }
                    if (responseBean.hasUpload != true){
                        ToastUtils.showShort("verify bank card not correct")
                        errorMsg("verify bank card not correct " , response, jsonObject)
                        return
                    }
                    if (activity is BindNewCardActivity) {
                        var bindNewCardActivity : BindNewCardActivity = activity as BindNewCardActivity
                        bindNewCardActivity.toStep(BindNewCardActivity.BIND_BINK_CARD_SUCCESS)
                    }
                }

                override fun onError(response: Response<String>) {
                    mHandler?.sendEmptyMessage(TYPE_HIDE_LOADING)
                    super.onError(response)
                    errorMsg("upload bank card error " , response, jsonObject)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "get bank list = " + response.body())
                    }
                }
            })
    }

    private fun errorMsg(errorMsg : String, response: Response<String>, requestJson : JSONObject){
        toastMsg = errorMsg;
        mHandler.removeMessages(TYPE_SHOW_TOAST)
        mHandler.sendEmptyMessage(TYPE_SHOW_TOAST)
        val result = StringBuffer()
        if (response != null){
            result.append(response.body())
        }
        result.append(requestJson.toString())
       LogSaver.logToFile("errorMsg = " + errorMsg + " data = " + result.toString())
    }

    private fun errorMsg(errorMsg : String, error : String?){
        toastMsg = errorMsg;
        mHandler.removeMessages(TYPE_SHOW_TOAST)
        mHandler.sendEmptyMessage(TYPE_SHOW_TOAST)
        val result = StringBuffer()
        if (!TextUtils.isEmpty(error)){
            result.append(error)
        }
        LogSaver.logToFile("errorMsg = " + errorMsg + " data = " + result.toString())
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}