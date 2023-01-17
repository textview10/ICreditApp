package com.loan.icreditapp.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.loan.icreditapp.R
import com.loan.icreditapp.global.AppManager
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.model.Response

 abstract class BaseActivity : AppCompatActivity() {

    fun toFragment(fragment: BaseFragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction() // 开启一个事务
            transaction.replace(getFragmentContainerRes(), fragment)
            transaction.commitAllowingStateLoss()
        }
    }

    @IdRes
    protected open fun getFragmentContainerRes(): Int {
//        return R.id.fl_sign_up_container
        return -1
    }

    protected fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>): T? {
        return CheckResponseUtils.checkResponseSuccess(response, clazz)
    }

    protected fun checkResponseSuccess(response: Response<String>): String? {
        return CheckResponseUtils.checkResponseSuccess(response)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppManager.sInstance.addActivity(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        AppManager.sInstance.finishActivity(this)
        super.onDestroy()
    }

    fun addFragment(fragment: BaseFragment?, tag: String?) {
        if (fragment != null) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(getFragmentContainerRes(), fragment, tag)
            beginTransaction.addToBackStack(tag)
            beginTransaction.commitAllowingStateLoss()
        }
    }


}