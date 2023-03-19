package com.loan.icreditapp.dialog

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.loan.icreditapp.R
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.GooglePlayUtils

class RateUsDialog constructor(context: Context): Dialog(context) {

    init {
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度
            lp.height = WindowManager.LayoutParams.MATCH_PARENT //设置高度
            lp.horizontalMargin = 0f
            lp.verticalMargin = 0f
            window?.attributes = lp
        }
        setContentView(R.layout.dialog_rate_us)
        val tvRateUs: AppCompatTextView = findViewById(R.id.tv_dialog_rate_us)
        val tvDesc: AppCompatTextView = findViewById(R.id.tv_dialog_rate_us_desc)
        if (Constant.textInfoResponse != null && !TextUtils.isEmpty(Constant.textInfoResponse!!.fiveStarContent)){
            tvDesc?.text = Constant.textInfoResponse!!.fiveStarContent
        }
        tvRateUs.setOnClickListener {
            if (GooglePlayUtils.getInstance().hasGooglePlay(getContext())){
                GooglePlayUtils.getInstance()
                    .goToGooglePlay(getContext(), GooglePlayUtils.MY_APP_MARKTE_URL)
            } else {
                GooglePlayUtils.goRateToExplore(getContext(), GooglePlayUtils.MY_APP_MARKTE_URL)
            }

            dismiss()
        }
    }
}