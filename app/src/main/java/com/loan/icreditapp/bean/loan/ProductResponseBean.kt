package com.loan.icreditapp.bean.loan

import android.text.TextUtils

class ProductResponseBean {

    var type : String? = null

    var products : List<Product>? = null

    class Product {
        var amount: String? = null
        var prodId: String? = null
        var prodName: String? = null
        var period: String? = null
        var stage: String? = null
    }

    fun isMarketing() : Boolean{
        return TextUtils.equals("marketing", type)
    }
}