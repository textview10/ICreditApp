package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment

class LoanPaidFragment : BaseLoanFragment(){

    private val TAG = "LoanPaidFragment"

    private var flCommit : FrameLayout? = null
    private var tvTotalAmount : AppCompatTextView? = null

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
        tvTotalAmount =  view.findViewById(R.id.tv_loan_paid_total_amount)
        flCommit =  view.findViewById(R.id.fl_loan_paid_commit)

        flCommit?.setOnClickListener { OnClickListener{

        } }
    }
}