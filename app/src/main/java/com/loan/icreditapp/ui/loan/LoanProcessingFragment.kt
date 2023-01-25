package com.loan.icreditapp.ui.loan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.loan.icreditapp.R
import com.loan.icreditapp.dialog.order.OrderInfoBean

class LoanProcessingFragment : BaseLoanFragment() {

    private val TAG = "LoanProcessingFragment"

    private var tvTotalAmount : AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_loan_processing, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTotalAmount = view.findViewById(R.id.tv_loan_processing_total_amount)
        tvTotalAmount?.text = mOrderInfo?.totalAmount.toString()
    }


}