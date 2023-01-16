package com.loan.icreditapp.global

import android.app.Activity
import java.util.*

class AppManager {
    private var activityStack: Stack<Activity?>? = null


    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            AppManager()
        }
    }
    /**
     * 单一实例
     */
    fun getAppManager(): AppManager? {
        return sInstance
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity?) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack!!.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = activityStack!!.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
//		//应用即将全部关闭，清理缓存
//		if(activityStack.size()==1){
//			((AppContext)activity.getApplication()).clearWebViewCache();
//
//		}
        var activity = activity
        if (activity != null) {
            activityStack!!.remove(activity)
            //            activity.finish();
            activity = null
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack!!) {
            if (activity!!.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    //获取指定类名的Activity
    fun getActivity(cls: Class<*>): Activity? {
        for (activity in activityStack!!) {
            if (activity!!.javaClass == cls) {
                return activity
            }
        }
        return null
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack!!.size
        while (i < size) {
            if (null != activityStack!![i]) {
                activityStack!![i]!!.finish()
            }
            i++
        }
        activityStack!!.clear()
    }
}