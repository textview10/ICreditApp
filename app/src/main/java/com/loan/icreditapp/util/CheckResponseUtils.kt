package com.loan.icreditapp.util

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.bean.BaseResponseBean
import com.lzy.okgo.model.Response

class CheckResponseUtils {

//    public static final Gson gson = new GsonBuilder()
//////             # 将DEFAULT改为STRING
//            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
//            .create();

    //    public static final Gson gson = new GsonBuilder()
    //////             # 将DEFAULT改为STRING
    //            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
    //            .create();

    companion object {
        fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>?): T? {
            val body = checkResponseSuccess(response)
            return if (TextUtils.isEmpty(body)) {
                null
            } else JSONObject.parseObject(body, clazz)
        }

        fun checkResponseSuccess(response: Response<String>): String? {
            var responseBean: BaseResponseBean? = null
            try {
                responseBean = JSONObject.parseObject(
                    response.body().toString(),
                    BaseResponseBean::class.java
                )
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    throw e
                }
            }
            //        BaseResponseBean responseBean = gson.fromJson(response.body().toString(), BaseResponseBean.class);
            if (responseBean == null) {
                ToastUtils.showShort("request failure.")
                return null
            }
            if (!responseBean.isRequestSuccess()) {
                ToastUtils.showShort(responseBean.getMessage())
                return null
            }
            if (responseBean.getData() == null) {
                ToastUtils.showShort("request failure 2.")
                return null
            }
            return JSONObject.toJSONString(responseBean.getData())
        }
    }

}