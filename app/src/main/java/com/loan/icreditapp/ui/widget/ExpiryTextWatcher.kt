package com.loan.icreditapp.ui.widget

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText

class ExpiryTextWatcher : TextWatcher {
    private val TAG = "ExpiryTextWatcher"

    private var mEditText : AppCompatEditText? = null

    constructor(editText: AppCompatEditText?) {
        mEditText = editText
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChangeInternal(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    private fun onTextChangeInternal(s: CharSequence?, start: Int, before: Int, count: Int){
        if (s == null){
            return
        }

        if (before > 0 && count == 0){
            if (s.endsWith("/") && s.length > 1){
                var text = s.toString().replace("/", "")
                mEditText?.setText(text)
                mEditText?.setSelection(text.length)
            }
        } else {
            if (s.endsWith("/")){
                return
            }
        }
//        Log.e(TAG, " start = " + start + " before = " + before + " count = "+ count)
        if (before == 0 && count > 0) {
            var sLength = s.length
            if (sLength >= 2 && !s.contains("/")) {
                val charIrr = s.toString().toCharArray()
                val sb = StringBuffer()
                for (i in 0 until charIrr.size) {
                    sb.append(charIrr[i])
                    if (i == 1){
                        sb.append("/")
                    }
                }
                mEditText?.setText(sb.toString())
                mEditText?.setSelection(sb.toString().length)
            }
        }
    }
}