package com.loan.icreditapp.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.event.LogTimeOut
import com.loan.icreditapp.global.AppManager
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.login.SignInActivity
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        if (useLogout()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        }
    }

    override fun onDestroy() {
        AppManager.sInstance.finishActivity(this)
        super.onDestroy()
        if (useLogout()) {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEvent(event: LogTimeOut) {
        ToastUtils.showShort("need login.")
        Constant.mToken = null
        Constant.mAccountId = null
        Constant.mLaunchOrderInfo = null
        val header = BuildRequestJsonUtils.clearHeaderToken()
        OkGo.getInstance().addCommonHeaders(header)

        AppManager.sInstance.finishAllActivity()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun addFragment(fragment: BaseFragment?, tag: String?) {
        if (fragment != null) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(getFragmentContainerRes(), fragment, tag)
            beginTransaction.addToBackStack(tag)
            beginTransaction.commitAllowingStateLoss()
        }
    }

    open fun useLogout(): Boolean {
        return false
    }
}