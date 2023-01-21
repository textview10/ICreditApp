package com.loan.icreditapp.api

class Api {

    companion object {
        private val HOST = "http://srv.chucard.com"
//        private val HOST = " https://api.hipkloan.com"

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
        //获取产品列表（APP使用）
        val GET_PRODUCTS: String = HOST + "/v1/loan/products"

        val UPLOAD_BANK_ACCOUNT: String = HOST + "/v1/account/bankaccount/check"

        //获取银行列表
        val GET_BANK_LIST: String = HOST + "/v1/account/bank/list"
        //获取自己银行卡列表
        val GET_CARD_LIST: String = HOST + "/v1/account/card/list"

        //授权信息上报
        val UPLOAD_AUTH: String = HOST + "/v1/account/auth/upload"

        //产品试算
        val PRODUCT_TRIAL: String = HOST + "/v1/loan/trial"
        //获取订单号
        val GET_ORDER_ID: String = HOST + "v1/loan/check"

        //获取订单详情
        val GET_ORDER_INFO: String = HOST + "/v1/loan/detail"

        val WEB_VIEW_POLICY: String = HOST + "/html/Privacy.html"

        val WEB_VIEW_TERM: String = HOST + "/html/terms.html"

        val REQUEST_MESSAGE_LIST: String = HOST + "/v1/station/list"
    }

}