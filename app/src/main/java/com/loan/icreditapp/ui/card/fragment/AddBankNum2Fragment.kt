package com.loan.icreditapp.ui.card.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment

class AddBankNum2Fragment : BaseFragment()  {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_banknum, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}