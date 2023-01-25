package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.loan.icreditapp.R

class LoanOverDueFragment : BaseLoanFragment() {

    private var flCommit: FrameLayout? = null
    private var tvTotalAmount: AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_loan_overdue, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flCommit = view.findViewById(R.id.fl_loan_overdue_commit)
        tvTotalAmount = view.findViewById(R.id.tv_loan_overdue_total_amount)
        tvTotalAmount?.text = mOrderInfo?.totalAmount.toString()

        flCommit?.setOnClickListener(View.OnClickListener {
            clickRepayLoad()
        })
    }
}