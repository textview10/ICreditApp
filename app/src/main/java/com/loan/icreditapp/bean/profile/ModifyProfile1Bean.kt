package com.loan.icreditapp.bean.profile

class ModifyProfile1Bean {

    //用户token
    var token: String? = null

    //是否完善个人信息
    var hasProfile: Boolean? = false

    //是否完善联系人信息
    var hasContact: Boolean? = false

    //是否完善个人其他信息
    var hasAddress: Boolean? = false

    //是否通过身份证验证
    var nationalChecked: Boolean? = false

    var bvnChecked:Boolean?= false

}