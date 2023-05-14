package com.loan.icreditapp.collect

import com.loan.icreditapp.api.Api

class CollectDataMgr : BaseCollectDataMgr(){


    companion object {
        private const val TAG = "CollectDataMgr"

        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectDataMgr()
        }
    }

    override fun getTag(): String {
        return TAG
    }

    override fun getLogTag(): String {
        return "new "
    }

    override fun getApi(): String {
        return Api.UPLOAD_AUTH
    }
}