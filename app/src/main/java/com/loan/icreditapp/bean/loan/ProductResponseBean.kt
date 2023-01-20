package com.loan.icreditapp.bean.loan

class ProductResponseBean {

    var products : List<Product>? = null

    class Product {
        var amount: String? = null
        var prodId: String? = null
        var prodName: String? = null
        var period: String? = null
        var stage: String? = null
    }
}