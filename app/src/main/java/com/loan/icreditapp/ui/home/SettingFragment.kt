package com.loan.icreditapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment

class SettingFragment : BaseFragment() {

    private var llMyloan:LinearLayout? = null
    private var llMyProfile:LinearLayout? = null
    private var llCard:LinearLayout? = null
    private var llBankAccount:LinearLayout? = null
    private var llMessage:LinearLayout? = null
    private var llHelp:LinearLayout? = null
    private var llAbout:LinearLayout? = null
    private var llLogout:LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llMyloan = view.findViewById(R.id.ll_setting_my_loan)
        llMyProfile = view.findViewById(R.id.ll_setting_my_profile)
        llCard = view.findViewById(R.id.ll_setting_card)
        llBankAccount = view.findViewById(R.id.ll_setting_bank_account)
        llMessage = view.findViewById(R.id.ll_setting_message)
        llHelp = view.findViewById(R.id.ll_setting_help)
        llAbout = view.findViewById(R.id.ll_setting_about)
        llLogout = view.findViewById(R.id.ll_setting_logout)

        llMyloan?.setOnClickListener(View.OnClickListener {

        })
        llMyProfile?.setOnClickListener(View.OnClickListener {

        })
        llCard?.setOnClickListener(View.OnClickListener {

        })
        llBankAccount?.setOnClickListener(View.OnClickListener {

        })

        llMessage?.setOnClickListener(View.OnClickListener {

        })
        llHelp?.setOnClickListener(View.OnClickListener {

        })
        llAbout?.setOnClickListener(View.OnClickListener {

        })
        llLogout?.setOnClickListener(View.OnClickListener {

        })
    }
}