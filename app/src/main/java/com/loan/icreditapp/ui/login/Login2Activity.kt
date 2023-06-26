package com.loan.icreditapp.ui.login

import android.os.Bundle
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.ui.login2.Login2Fragment

class Login2Activity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        val login2Fragment = Login2Fragment()
        replaceFragment(login2Fragment)
    }



    private fun replaceFragment(fragment: BaseFragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(R.id.fl_login2_container, fragment)
        transaction.commitAllowingStateLoss()
    }
}