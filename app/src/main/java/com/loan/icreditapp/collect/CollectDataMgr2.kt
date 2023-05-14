package com.loan.icreditapp.collect

import com.loan.icreditapp.api.Api

class CollectDataMgr2 : BaseCollectDataMgr(){

    companion object {
        private const val TAG = "CollectDataMgr2"
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectDataMgr2()
        }
    }

    override fun getTag(): String {
        return TAG
    }

    override fun getLogTag(): String {
        return "old "
    }

    override fun getApi(): String {
        return Api.UPLOAD_AUTH2
    }

}