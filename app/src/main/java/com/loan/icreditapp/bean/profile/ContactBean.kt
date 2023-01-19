package com.loan.icreditapp.bean.profile

class ContactBean {

    var id: String? = null

    var number: String? = null

    var contactName: String? = null

    var photoUri: String? = null

    var ringTonePath: String? = null

    constructor(id: String?, number: String?, contactName: String?,  photoUri: String? , ringTonePath: String?){
        this.id = id
        this.number = number
        this.contactName = contactName
        this.photoUri = photoUri
        this.ringTonePath = ringTonePath
    }
}