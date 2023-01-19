package com.loan.icreditapp.bean.profile

class Profile1Bean {
    //电话号码
    var mobile: String? = null

    //身份证号
    var bvn: String? = null

    //身份证识别是否通过 0未通过 1通过
    var bvnChecked: String? = null

    //生日：1990-01-24
    var birthday: String? = null

    //性别代码 0未知 1男 2女
    var gender: String? = null

    //性别描述 Male  or Female
    var genderLabel: String? = null

    //用户姓名
    var fullName: String? = null

    //名字
    var firstName: String? = null

    //中间名
    var middleName: String? = null

    //姓
    var lastName: String? = null

    //邮箱地址
    var email: String? = null

    //住址所在郡县代码
    var homeArea: String? = null

    //住址所在郡县
    var homeAreaLabel: String? = null

    //住址所在州代码
    var homeState: String? = null

    //住址所在州
    var homeStateLabel: String? = null

    //住址详情
    var homeAddress: String? = null

    //资料填写结果	可忽略
    var result: String? = null

    //是否可以修改
    var edit: Boolean? = null

    //更新时间
    var utime: String? = null
}