package com.loan.icreditapp.api

import com.loan.icreditapp.BuildConfig

class Api {

    companion object {
        private val USE_TEST_HOST_FLAG : Boolean = true

//        private val HOST = if (BuildConfig.DEBUG || USE_TEST_HOST_FLAG) "http://srv.chucard.com" else  "https://srv.creditng.com"
        private val HOST =  "https://srv.creditng.com"

        //检测服务器是否存活
        val CHECK_SERVER_ALIVE: String = HOST + "/v1/start/live"
        // 检测更新
        val UPDATE_DETAIL: String = HOST + "/v1/start/detail"
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
        val MODIFY_PSD: String = HOST + "/v1/account/modify/password"

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
        //产品试算
        val PRODUCT_TRIAL: String = HOST + "/v1/loan/trial"

        val UPLOAD_BANK_ACCOUNT: String = HOST + "/v1/account/bankaccount/check"
        //获取银行列表
        val GET_BANK_LIST: String = HOST + "/v1/account/bank/list"
        //获取自己银行卡列表
        val GET_CARD_LIST: String = HOST + "/v1/account/card/list"
        //获取Access Code
        val GET_ACCESS_CODE: String = HOST + "/v1/account/access/code"
        //上传银行卡信息
        val UPLOAD_CARD: String = HOST + "/v1/account/card/upload"
        //授权信息上报
        val UPLOAD_AUTH: String = HOST + "/v1/account/auth/upload"
        //硬件信息上报
        val UPLOAD_HARD_WARE : String = HOST + "/v1/account/hardware"
        //申请贷款
        val LOAD_APPLY: String = HOST + "/v1/loan/apply"
        //获取订单号
        val GET_ORDER_ID: String = HOST + "/v1/loan/check"
        //还款
        val LOAN_REPAY: String = HOST + "/v1/loan/repay"
        //获取银行账号详情
        val GET_BANK_DETAIL: String = HOST + "/v1/account/bank/detail"

        //获取订单详情
        val GET_ORDER_INFO: String = HOST + "/v1/loan/detail"

        val GET_POLICY: String = "https://www.creditng.com/privacy.html"

        val GET_TERMS: String = "https://www.creditng.com/terms.html"

        val GET_ALL: String = "https://www.creditng.com/all.html"

        val REQUEST_MESSAGE_LIST: String = HOST + "/v1/station/list"

        val CHECK_UPDATE: String = HOST + "/v1/start/detail"

//        @POST("/v1/loan/uploadJson")
//        fun uploadJsonon(@Body params: flutterWaveParams?): Observable<BaseRep<verifyFlutterBean?>?>?
        //* 上传后端校验Flutterwave
        val UPLOAD_JSON : String = HOST + "/v1/loan/uploadJson"

        //* 获取Flutterwave参数
        // * chargeType // 1 绑卡 2 主动还款
        val GET_TEXT_REF : String = HOST + "/v1/loan/getTxRef"

//        /**
//         * 查询flutter订单状态
//         * @param accountId
//         * @param orderId
//         * @param txRef
//         * @return
//         */
//        @POST("/v1/loan/getFlutterwaveResult")
//        fun getFlutterStatus(
//            @Query("accountId") accountId: String?,
//            @Query("orderId") orderId: String?,
//            @Query("txRef") txRef: String?
//        ): Observable<BaseRep<OrderStatus?>?>?
        val GET_FLUTTER_WAVE_RESULT = HOST + "/v1/loan/getFlutterwaveResult"

        //* 获取 url
        val PAY_STACK = HOST + "/v1/loan/repay/paystack"

//         * 查询paystck还款结果
        val PAY_STACK_RESULT = HOST + "/v1/loan/repay/paystack/result"

//         * Monify虚拟账号
        val GET_RESERVED_ACCOUNT = HOST + "/v1/loan/get/reserved/account"

        // * redocly
        val REDOCLY_REPAY_PAGE = HOST + "/v1/loan/get/redocly/repay/page"
    }

}