package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.bank.BankDetailResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.profile.widget.SelectContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class BankAccountFragment : BaseFragment() {
    private val TAG = "BankAccountFragment"

    private var tvBankName: SelectContainer? = null
    private var tvBankAccountNum: SelectContainer? = null
    private var tvPhoneNum: SelectContainer? = null
    private var flLoading: FrameLayout? = null

    private var hasUpload: Boolean = false

    private var bankName: String? = null
    private var bankAccountNum: String? = null
    private var phoneNum: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_bank_acount, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvBankName =  view.findViewById(R.id.select_container_bank_name)
        tvBankAccountNum = view.findViewById(R.id.select_container_bank_account_number)
        tvPhoneNum = view.findViewById(R.id.select_container_phone_number)
        flLoading = view.findViewById(R.id.fl_myloan_loading)

        tvBankName?.setShowMode()
        tvBankAccountNum?.setShowMode()
        tvPhoneNum?.setShowMode()
        requestBankAccount()
    }

    private fun requestBankAccount(){
        flLoading?.visibility = View.VISIBLE
        if (hasUpload) {
            bindData()
            return
        }
        hasUpload = true
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("token", Constant.mToken)
            jsonObject.put("mobile", Constant.mMobile)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_BANK_DETAIL).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    flLoading?.visibility = View.GONE
                    val bankDetail: BankDetailResponseBean? =
                        checkResponseSuccess(response, BankDetailResponseBean::class.java)
                    if (bankDetail == null) {
                        return
                    }
                    updateData(bankDetail)
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
                    Log.e(TAG, "bank account error.")
                    ToastUtils.showShort("bank account error.")
                }
            })
    }

    private fun updateData(bankDetail: BankDetailResponseBean) {
        bankName = bankDetail.bankdetail?.bankName
        bankAccountNum = bankDetail.bankdetail?.bankAccountNumber
        phoneNum = Constant.mMobile
    }

    private fun bindData() {
        if (!TextUtils.isEmpty(bankName)){
            tvBankName?.setData(bankName)
        }
        if (!TextUtils.isEmpty(bankAccountNum)){
            tvBankAccountNum?.setData(bankAccountNum)
        }
        if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(bankName) && !TextUtils.isEmpty(bankAccountNum)){
            tvPhoneNum?.setData(phoneNum)
        }
    }
}