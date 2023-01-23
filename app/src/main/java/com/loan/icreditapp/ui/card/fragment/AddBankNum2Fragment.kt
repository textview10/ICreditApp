package com.loan.icreditapp.ui.card.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
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
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.bank.AccessCodeResponseBean
import com.loan.icreditapp.bean.bank.UploadCardResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat


class AddBankNum2Fragment : BaseFragment() {

    private val TAG = "AddBankNum2Fragment"

    private var editBankNum: EditTextContainer? = null
    private var flChooseDate: FrameLayout? = null
    private var tvChooseDate: AppCompatTextView? = null
    private var etCvv: AppCompatEditText? = null

    private var flCommit: FrameLayout? = null

    private var expireDate: String? = null

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
        flChooseDate = view.findViewById(R.id.fl_add_bank_card_choose_date)
        tvChooseDate = view.findViewById(R.id.tv_add_bank_card_choose_date)
        etCvv = view.findViewById(R.id.et_add_bank_card_cvv)
        flCommit = view.findViewById(R.id.fl_add_bank_num_commit)

        flChooseDate?.setOnClickListener(OnClickListener {
            showTimePicker { date, v ->
                val sdf = SimpleDateFormat("yy-MM")
                val datef = sdf.format(date)
                expireDate = datef
                tvChooseDate?.text = expireDate
            }
        })
        flCommit?.setOnClickListener(OnClickListener {
            if (checkBankNum()) {
                var cardNum = editBankNum?.text
                if (BuildConfig.DEBUG) {
                    cardNum = "5399412019805634"
                }
                var cvv = etCvv?.text
                var expireList = expireDate?.split("-")
                if (expireList?.size == 2) {
                    var year = Integer.parseInt(expireList[0])
                    var month = Integer.parseInt(expireList[1])
                    Log.e(TAG, " year = " + year + " month = " + month)
                    bindCardAccess(cardNum.toString(), cvv.toString(), month, year)
                }
            }
        })
    }

    private fun bindCardAccess(
        cardNum: String, cvc: String,
        expiryMonth: Int, expiryYear: Int
    ) {
        val card: Card = Card.Builder(cardNum, expiryMonth, expiryYear, cvc).build()
        val charge = Charge()
        charge.card = card
        getAccessCode(charge, cardNum, cvc, expiryMonth, expiryYear)
    }

    private fun checkBankNum(): Boolean {
        if (editBankNum == null || TextUtils.isEmpty(editBankNum?.text)) {
            ToastUtils.showShort("bank num = null")
            return false
        }
        if (etCvv == null || TextUtils.isEmpty(etCvv?.text)) {
            ToastUtils.showShort("bank cvv = null")
            return false
        }
        if (TextUtils.isEmpty(expireDate)) {
            ToastUtils.showShort("unselect expire date = null")
            return false
        }

//        expiryMonth : Int, expiryYear : Int
        return true
    }

    fun getAccessCode(charge: Charge,  cardNum: String, cvc: String,
                      expiryMonth: Int, expiryYear: Int) {
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
                    val responseBean: AccessCodeResponseBean? =
                        checkResponseSuccess(response, AccessCodeResponseBean::class.java)
                    if (responseBean == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get access code ." + response.body())
                        }
                        return
                    }
                    charge.accessCode = responseBean.accessCode
                    chargeCard(charge, cardNum, cvc, expiryMonth, expiryYear)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get bank list = " + response.body())
                }
            })
    }

    private fun chargeCard(charge: Charge , cardNum: String, cvc: String,
                           expiryMonth: Int, expiryYear: Int) {
//        PaystackSdk.setPublicKey()
        PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {
            override fun onSuccess(transaction: Transaction) {
                Log.e(TAG, "onSuccess:    " + transaction.getReference())
//                verifyOnServer(transaction.getReference())
                uploadCard(transaction.getReference(), cardNum, cvc, expiryMonth, expiryYear)
            }

            //此函数只在请求OTP保存引用之前调用，如果需要，可以解除otp验证
            override fun beforeValidate(transaction: Transaction) {
                Log.e(TAG, "beforeValidate: " + transaction.getReference())
            }

            override fun onError(error: Throwable, transaction: Transaction?) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                Log.e(TAG, " = " + error.toString())
            }
        })
    }

    private fun uploadCard(reference : String, cardNum: String, cvc: String,
                           expiryMonth: Int, expiryYear: Int){
//                	String	Y
//                expireDate	String	Y	过期日	格式 MM/YY
//                	String	Y	CVV-卡片背面后三位数字
//        reference	String	Y	交易号
        var jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
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
                    val responseBean: UploadCardResponseBean? =
                        checkResponseSuccess(response, UploadCardResponseBean::class.java)
                    if (responseBean == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get access code ." + response.body())
                        }
                        return
                    }
                    if (responseBean.hasUpload != true){
                        ToastUtils.showShort("verify bank card failure")
                        return
                    }

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Log.e(TAG, "get bank list = " + response.body())
                }
            })
    }

    fun showTimePicker(listener: OnTimeSelectListener?) {
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        val type = booleanArrayOf(
            true, true, false, false, false, false
        )
        //时间选择器
        val pvTime = TimePickerBuilder(context, listener).setSubmitText("ok")
            .setCancelText("cancel")
            .setType(type).build()
        pvTime.show()
    }
}