package com.loan.icreditapp.api

class Api {

    companion object {
        private val HOST = "http://srv.chucard.com"

        //检测服务器是否存活
        val CHECK_SERVER_ALIVE: String = HOST + "/v1/start/live"
        // 检测更新
        val UPDATE_DETAIL: String = HOST + " /v1/start/detail"
        //    v1/station/list
        //验证手机号码是否注册
        val CHECK_MOBILE: String = HOST + "/api/v1/account/check_mobile"

        //发送短信验证码(获取验证码)
        val GET_SMS_CODE: String = HOST + "/api/v1/account/send_sms_otp"

        //验证验证码
        val CHECK_SMS_CODE: String = HOST + "/api/v1/account/check_otp_code"

        //注册账号
        val REGISTER: String = HOST + "/api/v1/account/register"

        //登录账号
        val LOGIN: String = HOST + "/api/v1/account/login"

        //登出账号
        val LOGOUT: String = HOST + "/api/v1/account/logout"

        //修改密码
        val MODIFY_PSD: String = HOST + "/api/v1/account/modify_password"

        //上传fcm token
        val UPLOAD_FCM_TOKEN: String = HOST + "/api/v1/account/upload_fcmtoken"

        //获取基本信息
        val GET_CONFIG: String = HOST + "/api/v1/account/base_profile_config"

        //获取客户信息
        val GET_PROFILE: String = HOST + "/api/v1/account/get_profile"

        //上传客户基本信息
        val UPLOAD_BASE: String = HOST + "/api/v1/account/upload_base"

        //上传联系人信息
        val UPLOAD_CONTACT: String = HOST + "/api/v1/account/upload_contact"

        //上传证件相关信息
        val UPLOAD_IDENTITY: String = HOST + "/api/v1/account/upload_identity"

        //获取银行列表
        val GET_BANK_LIST: String = HOST + "/api/v1/account/get_bank_list"

        //上传银行账号信息
        val UPLOAD_BANK: String = HOST + "/api/v1/account/upload_bank"

        val UPLOAD_WALLET: String = HOST + "/api/v1/account/upload_wallet"

        //上传客户端信息
        val UPLOAD_CLIENT_INFO: String =
            HOST + "/api/v1/account/upload_client_info"

        //产品试算
        val PRODUCT_TRIAL: String = HOST + "/api/v1/product/trial"

        //产品列表
        val GET_PRODUCT_LIST: String = HOST + "/api/v1/product/list"

        //获取订单详情
        val GET_ORDER_INFO: String = HOST + "/api/v1/order/info"

        //验证客户是否可以借贷
        val CHECK_CAN_ORDER: String = HOST + "/api/v1/order/check"

        //申请订单
        val ORDER_APPLY: String = HOST + "/api/v1/order/apply"

        val WEB_VIEW_POLICY: String = HOST + "/html/Privacy.html"

        val WEB_VIEW_TERM: String = HOST + "/html/terms.html"
    }

}