package com.loan.icreditapp.base

import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.util.CheckResponseUtils
import com.lzy.okgo.model.Response

open class BaseFragment : Fragment() {

    fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>?): T? {
        return CheckResponseUtils.checkResponseSuccess(response, clazz)
    }

    fun checkResponseSuccess(response: Response<String>): String? {
        return CheckResponseUtils.checkResponseSuccess(response)
    }

    private var lastClickMillions: Long = 0

    protected fun checkClickFast(): Boolean {
        val delay = System.currentTimeMillis() - lastClickMillions
        if (delay < 3000) {
            ToastUtils.showShort("click too fast. please wait a monment")
            return true
        }
        lastClickMillions = System.currentTimeMillis()
        return false
    }

    protected fun checkShortClickFast(): Boolean {
        val delay = System.currentTimeMillis() - lastClickMillions
        if (delay < 500) {
            ToastUtils.showShort("click too fast. please wait a monment")
            return true
        }
        lastClickMillions = System.currentTimeMillis()
        return false
    }
}