package com.loan.icreditapp.bean

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy

class BaseResponseBean {
    private var code = 0
    private var message: String? = null
    private var data: Any? = null

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int) {
        this.code = code
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): Any? {
        return data
    }

    fun setData(data: Any?) {
        this.data = data
    }

    fun isRequestSuccess(): Boolean {
        return code == 0 && data != null
    }

    fun getBodyStr(): String? {
        val gson = GsonBuilder() //             # 将DEFAULT改为STRING
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls().create()
        return gson.toJson(data)
    }
}