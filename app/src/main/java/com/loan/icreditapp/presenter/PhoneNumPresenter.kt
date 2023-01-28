package com.loan.icreditapp.presenter

import android.content.Context
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.blankj.utilcode.util.ToastUtils
import com.loan.icreditapp.R

class PhoneNumPresenter constructor(context: Context?) {
    private var mContext: Context?

    private val mPhoneNumMap = HashMap<String, Int>()

    private val mItems = arrayOf(" 234 ")

    init {
        mContext = context
        initPhoneNumMap()
    }

    //    手机号的格式，开头必须是7、8、9、07、08、09、2347、2348、2349、23407、23408、23409:
    //    当开头是7、8、9时，限制用户输入位数10位；
    //    当开头是07、08、09时，限制用户输入位数11位；
    //    当开头是2347、2348、2349时，限制用户输入位数13位；
    //    当开头是23407、23408、23409时，限制用户输入位数14位。
    private fun initPhoneNumMap() {
        mPhoneNumMap.clear()
        mPhoneNumMap["0"] = 10
        mPhoneNumMap["7"] = 10
        mPhoneNumMap["8"] = 10
        mPhoneNumMap["9"] = 10
        mPhoneNumMap["07"] = 11
        mPhoneNumMap["08"] = 11
        mPhoneNumMap["09"] = 11
        mPhoneNumMap["2347"] = 13
        mPhoneNumMap["2348"] = 13
        mPhoneNumMap["2349"] = 13
        mPhoneNumMap["23407"] = 14
        mPhoneNumMap["23408"] = 14
        mPhoneNumMap["23409"] = 14
    }

    fun initSpinner(spinner: Spinner) {
        val adapter = ArrayAdapter(mContext!!, R.layout.item_login_dropdown, mItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }


    fun checkIsCorrectPhoneNum(phoneNum: String): Boolean {
        if (TextUtils.equals(phoneNum, "254888888888")) {
            return true
        }
        val iterator: Iterator<Map.Entry<String, Int>> = mPhoneNumMap.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            if (phoneNum.startsWith(key)) {
                val isCorrect = phoneNum.length == value
                if (!isCorrect) {
                    ToastUtils.showShort(" phone num length must be $value")
                }
                return isCorrect
            }
        }
        return false
    }

    fun getSelectString(selectedItemPosition: Int): String? {
        return if (selectedItemPosition >= mItems.size) {
            ""
        } else mItems[selectedItemPosition].trim()
    }
}