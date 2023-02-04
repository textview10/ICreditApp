package com.loan.icreditapp.bean

class UpdateResponseBean {

    //0 不需要 1 可选择 2 强制升级
    var updateType : String? = null
    //更新路径
    var updateUrl : String? = null
    //更新头部内容
    var updateTitle : String? = null
    //更新主体内容
    var updateContent : String? = null
    //当前配置最新版本
    var configVersion : String? = null
    //0 关闭 1开启
    var comingSoonSwitch : String? = null
    //展示给客户的URL
    var comingSoonUrl : String? = null
}