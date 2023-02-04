package com.loan.icreditapp.collect

class UpdateMgr {

    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectDataMgr()
        }
    }

    fun checkUpdate(){

    }
}