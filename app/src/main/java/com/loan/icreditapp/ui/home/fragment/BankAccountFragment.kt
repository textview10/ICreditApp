package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment

class BankAccountFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.fragment_bank_acount, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}