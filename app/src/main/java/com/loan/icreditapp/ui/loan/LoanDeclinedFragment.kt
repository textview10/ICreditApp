package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.loan.icreditapp.R
import com.loan.icreditapp.event.ToApplyLoanEvent
import org.greenrobot.eventbus.EventBus

class LoanDeclinedFragment : BaseLoanFragment() {

    private var flCommit : FrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_loan_declined_2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flCommit =  view.findViewById(R.id.fl_loan_declined_commit)
        flCommit?.setOnClickListener(View.OnClickListener {
            if (checkClickFast()){
                return@OnClickListener
            }
            EventBus.getDefault().post(ToApplyLoanEvent())
        })
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}