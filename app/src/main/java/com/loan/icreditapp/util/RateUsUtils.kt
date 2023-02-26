package com.loan.icreditapp.util

//import android.app.Activity
//import android.content.Context
//import dev.drojian.rate.RateManager
//import dev.drojian.rate.listeners.RateListener

class RateUsUtils {
//
//    fun showRate(context: Activity) {
//        val rateManager = RateManager(context, false, false)
//        rateManager.setCanceledOnTouchOutside(true)
//        rateManager.showRate(context, RateAdapter(context)
//        )
//    }
//
//    private fun updateRateData(context: Context, stars: Int) {
//        //如果评分小于等于以前的评分，就不再做处理
//
//    }
//
//    inner class RateAdapter(private val activity: Activity?) : RateListener {
//        override fun rateUs(stars: Int) {
//            if (activity == null) return
//            GooglePlayUtils.getInstance()
//                .goToGooglePlay(activity, GooglePlayUtils.MY_APP_MARKTE_URL)
//            //点击了"在GooglePlay上评星"，表示评分已经操作过
//            updateRateData(activity, stars)
//        }
//
//        override fun feedback(stars: Int) {
//            if (activity == null) return
//            if (stars > 4) {
//
//            } else {
//
//            }
//            //跳转googlePlay评分
//            GooglePlayUtils.getInstance()
//                .goToGooglePlay(activity, GooglePlayUtils.MY_APP_MARKTE_URL)
//            //点击了"评星"，表示评分已经操作过
//         updateRateData(activity, stars)
//        }
//
//        override fun cancelDialog() {}
//        override fun dismissDialog() {}
//        override fun sendEvent(category: String, action: String, label: String) {}
//        override fun sendException(e: Throwable) {}
//    }
}