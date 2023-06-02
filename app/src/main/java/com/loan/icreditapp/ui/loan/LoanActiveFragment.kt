package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.SPUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.event.RateUsEvent
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.util.FirebaseUtils
import org.greenrobot.eventbus.EventBus

class LoanActiveFragment : BaseLoanFragment() {

    private val TAG = "LoanActiveFragment"

    private var flCommit: FrameLayout? = null
    private var tvTotalAmount: AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_loan_active, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flCommit = view.findViewById(R.id.fl_loan_active_apply_now_commit)

        tvTotalAmount = view.findViewById(R.id.tv_loan_active_total_amount)
        tvTotalAmount?.text = mOrderInfo?.totalAmount.toString()

        if (checkNeedShowLog()){
            if (Constant.IS_FIRST_APPLY) {
                FirebaseUtils.logEvent( "fireb_activity")
            }
            FirebaseUtils.logEvent( "fireb_activity_all")
            EventBus.getDefault().post(RateUsEvent())
        }

        flCommit?.setOnClickListener ( OnClickListener{
            clickRepayLoad()
        } )
    }
}