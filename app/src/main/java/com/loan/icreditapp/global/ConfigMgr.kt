package com.loan.icreditapp.global

import android.text.TextUtils
import android.util.Log
import android.util.Pair
import com.blankj.utilcode.util.GsonUtils
import com.loan.icreditapp.api.Api
import com.loan.icreditapp.bean.BaseResponseBean
import com.loan.icreditapp.bean.bank.BankResponseBean
import com.loan.icreditapp.util.BuildRequestJsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ConfigMgr {

    companion object {
        private val TAG = "ConfigMgr"

        val mDebtList = ArrayList<Pair<String, String>>()
        val mEducationList = ArrayList<Pair<String, String>>()
        val mSalaryList = ArrayList<Pair<String, String>>()
        val mMaritalList = ArrayList<Pair<String, String>>()
        val mRelationShipList = ArrayList<Pair<String, String>>()
        val mWorkList = ArrayList<Pair<String, String>>()
        val mAreaMap = HashMap<Pair<String, String>, ArrayList<Pair<String, String>>>()

        val mBankList = ArrayList<BankResponseBean.Bank>()

        fun getAllConfig() {
            mDebtList.clear()
            mDebtList.add(Pair("yes", "0"))
            mDebtList.add(Pair("no", "1"))

            if (mEducationList.size > 0) {
                Log.i(TAG, " has request education")
            } else {
                //学历等级，
                getItemConfig("education", object : CallBack {
                    override fun onGetData(list: ArrayList<Pair<String, String>>) {
                        mEducationList.clear()
                        mEducationList.addAll(list)
                    }
                })
            }

            if (mSalaryList.size > 0) {
                Log.i(TAG, " has request salary")
            } else {
                //工资区间,
                getItemConfig("salary", object : CallBack {
                    override fun onGetData(list: ArrayList<Pair<String, String>>) {
                        mSalaryList.clear()
                        mSalaryList.addAll(list)
                    }
                })
            }
            if (mMaritalList.size > 0) {
                Log.i(TAG, " has request marital")
            } else {
            //婚姻状况，
                getItemConfig("marital", object : CallBack {
                    override fun onGetData(list: ArrayList<Pair<String, String>>) {
                        mMaritalList.clear()
                        mMaritalList.addAll(list)
                    }
                })
            }
            if (mRelationShipList.size > 0) {
                Log.i(TAG, " has request relationship")
            } else {
                //关系，，
                getItemConfig("relationship", object : CallBack {
                    override fun onGetData(list: ArrayList<Pair<String, String>>) {
                        mRelationShipList.clear()
                        mRelationShipList.addAll(list)
                    }
                })
            }
            if (mWorkList.size > 0) {
                Log.i(TAG, " has request work")
            } else {
                getItemConfig("work", object : CallBack{
                    override fun onGetData(list : ArrayList<Pair<String, String>>) {
                        mWorkList.clear()
                        mWorkList.addAll(list)
                    }
                })
            }
            if (!mAreaMap.isEmpty()){
                Log.i(TAG, " has request area .")
            } else {
                getCityData()
            }

        }

        fun getCityData(){
            //            stateArea:州地区地址联动,    state:州， area:地区，
            getCityConfig("stateArea", object : CallBack2{
                override fun onGetData(map: HashMap<Pair<String, String>, ArrayList<Pair<String, String>>>) {
                    mAreaMap.clear()
                    mAreaMap.putAll(map)
                }
            })
        }

        private fun getItemConfig(key : String, callBack : CallBack){
            val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
            try {
                jsonObject.put("dictType", key)
                jsonObject.put("parentId", "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            OkGo.post<String>(Api.GET_CONFIG).tag(TAG)
                .upJson(jsonObject.toString())
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        var responseBean : BaseResponseBean? = com.alibaba.fastjson.JSONObject.parseObject(response.body().toString(),
                            BaseResponseBean::class.java
                        )
                        if (responseBean == null || responseBean.body == null){
                            return
                        }
                        var list = parseItem(key, responseBean.body!!)
                        if (list.size > 0) {
                            callBack.onGetData(list)
                        }
                    }

                    override fun onError(response: Response<String>) {
                        super.onError(response)
                        Log.e(TAG, "get config failure = " + response.body())
                    }
                })
        }

        private fun getCityConfig(key : String, callBack : CallBack2){
            val jsonObject: JSONObject = BuildRequestJsonUtils.buildRequestJson()
            try {
                jsonObject.put("dictType", key)
                jsonObject.put("parentId", "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            OkGo.post<String>(Api.GET_CONFIG).tag(TAG)
                .upJson(jsonObject.toString())
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        var responseBean : BaseResponseBean? = com.alibaba.fastjson.JSONObject.parseObject(response.body().toString(),
                            BaseResponseBean::class.java
                        )
                        if (responseBean == null || responseBean.body == null){
                            return
                        }
                        var map : HashMap<Pair<String, String>, ArrayList<Pair<String, String>>> = parseCityItem(responseBean.body!!)
                        if (map.size > 0) {
                            callBack.onGetData(map)
                        }
                    }

                    override fun onError(response: Response<String>) {
                        super.onError(response)
                        Log.e(TAG, "get config failure = " + response.body())
                    }
                })
        }

        private fun parseCityItem(obj: Any): HashMap<Pair<String, String>, ArrayList<Pair<String, String>>> {
            val map: HashMap<Pair<String, String>, ArrayList<Pair<String, String>>> = HashMap<Pair<String, String>, ArrayList<Pair<String, String>>>()
            try {
                val tempObj = JSONObject(GsonUtils.toJson(obj))
                val dictMap =  tempObj.optJSONObject("dictMap")
                //州
                val stateArray = dictMap.optJSONArray("state")
                val areaObject = dictMap.optJSONObject("area")

                for (i in 0 until stateArray.length()) {
                    val jsonObject: JSONObject = stateArray.optJSONObject(i)
                    val key = jsonObject.optString("key")
                    val value = jsonObject.optString("val")
                    var pair = Pair(value, key)
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                        var tempList : ArrayList<Pair<String, String>> = ArrayList<Pair<String, String>>()

                        var tempArray:JSONArray =  areaObject.optJSONArray(key)
                        for (i in 0 until tempArray.length()) {
                            val itemJsonObject: JSONObject = tempArray.optJSONObject(i)
                            val itemKey = itemJsonObject.optString("key")
                            val itemValue = itemJsonObject.optString("val")
                            tempList.add(Pair(itemValue, itemKey))
                        }
                        map.put(pair, tempList)
//                        list.add(Pair(value, key))
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return map
        }

        private fun parseItem(key : String, obj: Any): ArrayList<Pair<String, String>> {
            val list: ArrayList<Pair<String, String>> = ArrayList<Pair<String, String>>()
            try {
                val tempObj = JSONObject(GsonUtils.toJson(obj))
                val dictMap =  tempObj.optJSONObject("dictMap")
                val jsonArray = dictMap.optJSONArray(key)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.optJSONObject(i)
                    val key = jsonObject.optString("key")
                    val value = jsonObject.optString("val")
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                        list.add(Pair(value, key))
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Collections.sort(list, object : Comparator<Pair<String, String>> {
                override fun compare(t1: Pair<String, String>, t2: Pair<String, String>): Int {
                    val first = t1.second
                    val second = t2.second
                    var firstInt = 0
                    var secondInt = 0
                    try {
                        if (!TextUtils.isEmpty(first)) {
                            firstInt = first.toInt()
                        }
                        if (!TextUtils.isEmpty(second)) {
                            secondInt = second.toInt()
                        }
                    } catch (e: Exception) {
                    }
                    return firstInt - secondInt
                }
            })
            return list
        }
    }

     private interface CallBack {
        fun onGetData(list : ArrayList<Pair<String, String>>)
    }

    private interface CallBack2 {
        fun onGetData(map : HashMap<Pair<String, String>, ArrayList<Pair<String, String>>>)
    }
}