package com.loan.icreditapp.dialog.producttrial

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.bean.loan.TrialResponseBean
import com.loan.icreditapp.global.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class ProductTrialDialog : Dialog {

    private val TAG = "ProductTrialDialog"

    var tvRepayDate: AppCompatTextView? = null
    var tvAmount: AppCompatTextView? = null
    var flCancel: FrameLayout? = null
    var flSubmit: FrameLayout? = null

    constructor(context: Context, response : TrialResponseBean) : super(context){
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = window?.attributes
        lp?.width = (ScreenUtils.getAppScreenWidth() * 3 / 4) //设置宽度
        window?.attributes = lp

        setCancelable(false)
        setContentView(R.layout.dialog_product_trial)

        tvRepayDate = findViewById(R.id.tv_product_trial_repay_date)
        tvAmount = findViewById(R.id.tv_product_trial_amount)
        flCancel = findViewById(R.id.fl_product_trial_cancel)
        flSubmit = findViewById(R.id.fl_product_trial_submit)

        flSubmit?.setOnClickListener {
            mListener?.onClickAgree()
            dismiss()
        }
        flCancel?.setOnClickListener { dismiss() }

        if (!TextUtils.isEmpty(response.repayDate)) {
            tvRepayDate?.text = response.repayDate
        }
        if (!TextUtils.isEmpty(response.totalRepaymentAmount)) {
            tvRepayDate?.text = response.repayDate
        }
    }


    private var mListener: OnDialogClickListener? = null

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        mListener = listener
    }

    abstract class OnDialogClickListener {
        abstract fun onClickAgree()
        fun onClickCancel() {}
    }
}