package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.pay.MonifyResponseBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.pay.presenter.MonifyPresenter
import com.loan.icreditapp.ui.profile.widget.EditTextContainer
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class OfflineRepayFragment : BaseFragment() {

    private val TAG = "OfflineRepayFragment"

    private var llMonifyResult: LinearLayout? = null
    private var selectBankName: EditTextContainer? = null
    private var selectBankCode: EditTextContainer? = null
    private var selectAccountName: EditTextContainer? = null
    private var selectAccountNumber: EditTextContainer? = null
    private var tvCopy: AppCompatTextView? = null
    private var flLoading: FrameLayout? = null

    private var mMonifyBean : MonifyResponseBean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_offline_repay, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCopy = view.findViewById(R.id.tv_pay_copy_account_num)

        llMonifyResult = view.findViewById(R.id.ll_play_monify_result)
        selectBankName = view.findViewById(R.id.select_container_pay_bank_name)
        selectBankCode = view.findViewById(R.id.select_container_pay_bank_code)
        selectAccountName = view.findViewById(R.id.select_container_pay_account_name)
        selectAccountNumber = view.findViewById(R.id.select_container_pay_account_number)
        flLoading = view.findViewById(R.id.fl_offline_repay_loading)

        selectBankName?.setShowMode()
        selectBankCode?.setShowMode()
        selectAccountName?.setShowMode()
        selectAccountNumber?.setShowMode()

        tvCopy?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()) {
                return@OnClickListener
            }
            val text = getCLipBoardText()
            if (!TextUtils.isEmpty(text)) {
                ClipboardUtils.copyText(text)
                ToastUtils.showShort("Copy " + text + " to clipboard success")
            } else {
                ToastUtils.showShort("get reserved account failure")
            }
        })
        getReservedAccount()
    }

   private fun getCLipBoardText() : String?{
        if (mMonifyBean == null){
            return null
        }
        var sb = StringBuffer()
        if (!TextUtils.isEmpty(mMonifyBean!!.accountNumber)){
            sb.append(mMonifyBean!!.accountNumber)
        }
        return sb.toString()
    }

    private fun getReservedAccount() {
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.GET_RESERVED_ACCOUNT).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDetached || isRemoving) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    var monifyBean = CheckResponseUtils.checkResponseSuccess(
                        response,
                        MonifyResponseBean::class.java
                    )
                    if (monifyBean == null) {
                        return
                    }
                    mMonifyBean = monifyBean
                    showMonifyPage(monifyBean)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDetached || isRemoving) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                }
            })
    }

    private fun showMonifyPage(bean: MonifyResponseBean) {
        flLoading?.visibility = View.GONE
        llMonifyResult?.visibility = View.VISIBLE
        if (!TextUtils.isEmpty(bean.bankName)) {
            selectBankName?.setEditTextAndSelection(bean.bankName!!)
        }
        if (!TextUtils.isEmpty(bean.bankCode)) {
            selectBankCode?.setEditTextAndSelection(bean.bankCode!!)
        }
        if (!TextUtils.isEmpty(bean.accountName)) {
            selectAccountName?.setEditTextAndSelection(bean.accountName!!)
        }
        if (!TextUtils.isEmpty(bean.accountNumber)) {
            selectAccountNumber?.setEditTextAndSelection(bean.accountNumber!!)
        }
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}