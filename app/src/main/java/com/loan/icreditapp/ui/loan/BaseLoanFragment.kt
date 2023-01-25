package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.bean.loan.RepayLoanResponseBean
import com.loan.icreditapp.dialog.order.OrderInfoBean
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.time.temporal.TemporalAmount

abstract class BaseLoanFragment : BaseFragment() {

    private val TAG = "base_loan_fragment"

    var mOrderInfo : OrderInfoBean? = null

    private var tvDisburseDate: AppCompatTextView? = null
    private var tvDueDate: AppCompatTextView? = null
    private var tvLoanAMount: AppCompatTextView? = null
    private var tvOriginFee: AppCompatTextView? = null
    private var tvRolloverFee: AppCompatTextView? = null
    private var tvTotalAmountDue: AppCompatTextView? = null

    open fun setOrderInfo(orderInfoBean: OrderInfoBean) {
        mOrderInfo = orderInfoBean
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDisburseDate = view.findViewById(R.id.tv_loan_process_disburse_date)
        tvDueDate = view.findViewById(R.id.tv_loan_process_due_date)
        tvLoanAMount = view.findViewById(R.id.tv_loan_process_loan_amount)
        tvOriginFee = view.findViewById(R.id.tv_loan_process_loan_origin_fee)
        tvRolloverFee = view.findViewById(R.id.tv_loan_process_loan_fee)
        tvTotalAmountDue = view.findViewById(R.id.tv_loan_process_loan_total_amount_due)
        bindDataInternal()
    }

    private fun bindDataInternal() {
        if (mOrderInfo == null) {
            return
        }
        if (mOrderInfo?.stageList != null) {
            var stage: OrderInfoBean.Stage? = mOrderInfo?.stageList!![0]
            if (stage != null){
                tvDisburseDate?.text = stage.disburseTime.toString()
                tvDueDate?.text =  stage.repayDate.toString()

                tvLoanAMount?.text = "₦ " + stage.amount.toString()
                tvOriginFee?.text = "₦ " + stage.fee.toString()
                tvRolloverFee?.text = "₦ " + stage.interest.toString()
            }
        }
        tvTotalAmountDue?.text = "₦ " + mOrderInfo?.totalAmount.toString()
    }

    //点击还款
    fun clickRepayLoad(){
        if (checkClickFast()){
            return
        }
        if (mOrderInfo == null){
            ToastUtils.showShort("repay load failure .")
            return
        }
        if (TextUtils.isEmpty(mOrderInfo?.orderId)){
            ToastUtils.showShort("repay load orderId == null .")
            return
        }
        if (mOrderInfo?.totalAmount == null){
            ToastUtils.showShort("repay load total amount == null .")
            return
        }
        uploadReplayLoad(mOrderInfo?.orderId!!, mOrderInfo?.totalAmount!!)
    }

    //订单ID ,申请金额
    private fun uploadReplayLoad(orderId : String, amount: Double){
        val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
        try {
            jsonObject.put("accountId", Constant.mAccountId)
            jsonObject.put("orderId", orderId)
            jsonObject.put("amount", amount.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        Log.e(TAG, "111 id = " + Constant.mAccountId);
        OkGo.post<String>(Api.LOAN_REPAY).tag(TAG)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    val loanResponse: RepayLoanResponseBean? =
                        checkResponseSuccess(response, RepayLoanResponseBean::class.java)
                    if (loanResponse == null) {
                        Log.e(TAG, " repay loan failure ." + response.body())
                        return
                    }
                   if (!TextUtils.equals(loanResponse.status, "1")){
                       ToastUtils.showShort("repay loan failure 1.")
                       Log.e(TAG, " repay loan failure 1 ." + response.body())
                       return
                   }
                    ToastUtils.showShort("repay loan success.")
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " repay loan failure = " + response.body())
                    }
                    ToastUtils.showShort(" repay loan failure")
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}