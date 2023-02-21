package com.loan.icreditapp.bean

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy

class BaseResponseBean {
    var body: Any? = null
    var head: Head? = null

    class Head {
        var code: String? = null
        var msg: String? = null
    }

    fun isRequestSuccess(): Boolean {
        return head != null && head?.code == "200"
    }

    fun isLogout() : Boolean {
        if (head != null){
           if (head!!.code == "401" || head!!.code == "405"){
               return true
           }
        }
        return false;
    }

    fun getMessage(): String? {
        var msg = head?.msg
        return msg
    }

    fun getData(): Any? {
        return body
    }

    fun getBodyStr(): String? {
        val gson = GsonBuilder() //             # 将DEFAULT改为STRING
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls().create()
        return gson.toJson(body)
    }
}