package com.loan.icreditapp.ui.login2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loan.icreditapp.base.BaseFragment

class BaseOtpFragment : BaseFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}