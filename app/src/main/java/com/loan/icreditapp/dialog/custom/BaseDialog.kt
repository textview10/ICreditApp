package com.loan.icreditapp.dialog.custom

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Bundle

open class BaseDialog :Dialog {

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, themeResId: Int) : super(context) {
        initialize()
    }

    private fun initialize() {}

    override fun dismiss() {
        super.dismiss()
    }

    private val callback: Application.ActivityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
//            Log.e("BaseDialog","on activity stopped ...");
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (context is Activity) {
                    val activity1 = context as Activity
                    if (activity1 === activity && isShowing) {
                        dismiss()
                    }
                }
            }
        }
}