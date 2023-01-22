package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.AppUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment

class AboutFragment : BaseFragment(){

    private var tvVersion : AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_about, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvVersion = view.findViewById(R.id.tv_abount_version)
        var versionName =  AppUtils.getAppVersionName()
        if (!TextUtils.isEmpty(versionName)){
            tvVersion?.text = versionName
        }
    }
}