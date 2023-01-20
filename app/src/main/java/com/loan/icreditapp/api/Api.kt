package com.loan.icreditapp.api

class Api {

    companion object {
        private val HOST = "http://srv.chucard.com"

        //检测服务器是否存活
        val CHECK_SERVER_ALIVE: String = HOST + "/v1/start/live"
        // 检测更新
        val UPDATE_DETAIL: String = HOST + "/v1/start/detail"
        //    v1/station/list
        //验证手机号码是否注册
        val CHECK_MOBILE: String = HOST + "/v1/account/mobile/check"

        //发送短信验证码(获取验证码)
        val GET_SMS_CODE: String = HOST + "/v1/account/captcha"

        //验证验证码
        val CHECK_SMS_CODE: String = HOST + "/v1/account/captcha/check"

        //注册账号
        val REGISTER: String = HOST + "/v1/account/register"

        //登录账号
        val LOGIN: String = HOST + "/v1/account/login"

        //登出账号
        val LOGOUT: String = HOST + "/v1/account/logout"

        //修改密码
        val MODIFY_PSD: String = HOST + "/api/v1/account/modify_password"

        //上传fcm token
        val UPLOAD_FCM_TOKEN: String = HOST + "/v1/account/fcmtoken"

        //获取基本信息
        val GET_CONFIG: String = HOST + "/v1/dict/detail"

        //查询个人资料 profile1
        val GET_PROFILE_1: String = HOST + "/v1/account/profile/detail"
        //上传个人资料 profile1
        val UPLOAD_PROFILE_1: String = HOST + "/v1/account/profile"

        //查询紧急通讯录 profile2
        val GET_CONTACT_2: String = HOST + "/v1/account/contact/detail"
        //上传紧急通讯录 profile2
        val UPLOAD_CONTACT_2: String = HOST + "/v1/account/contact"

        //查询其他信息 profile3
        val GET_OTHER_3: String = HOST + "/v1/account/other/detail"
        //填写联系人资料(包括修改) profile3
        val UPLOAD_OTHER_3: String = HOST + "/v1/account/other"

        val GET_PRODUCTS: String = HOST + "/v1/order/products"

        //上传联系人信息
        val UPLOAD_CONTACT: String = HOST + "/api/v1/account/upload_contact"

        //上传证件相关信息
        val UPLOAD_IDENTITY: String = HOST + "/api/v1/account/upload_identity"

        //获取银行列表
        val GET_BANK_LIST: String = HOST + "/v1/account/card/list"

        //上传银行账号信息
        val UPLOAD_BANK: String = HOST + "/api/v1/account/upload_bank"

        val UPLOAD_WALLET: String = HOST + "/api/v1/account/upload_wallet"

        //上传客户端信息
        val UPLOAD_CLIENT_INFO: String = HOST + "/api/v1/account/upload_client_info"

        //产品试算
        val PRODUCT_TRIAL: String = HOST + "/api/v1/product/trial"

        //产品列表
        val GET_PRODUCT_LIST: String = HOST + "/api/v1/product/list"

        //获取订单详情
        val GET_ORDER_INFO: String = HOST + "/v1/loan/detail"

        //验证客户是否可以借贷
        val CHECK_CAN_ORDER: String = HOST + "/api/v1/order/check"

        //申请订单
        val ORDER_APPLY: String = HOST + "/api/v1/order/apply"

        val WEB_VIEW_POLICY: String = HOST + "/html/Privacy.html"

        val WEB_VIEW_TERM: String = HOST + "/html/terms.html"

        val REQUEST_MESSAGE_LIST: String = HOST + "/v1/station/list"
    }

}