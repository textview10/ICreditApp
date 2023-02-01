package com.loan.icreditapp.ui.widget

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textfield.TextInputLayout

class MyTextInputLayout : TextInputLayout {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {



    }

    private var hintStr : String? = null

    fun saveText(hint: String?) {
        hintStr = hint.toString()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (!TextUtils.isEmpty(hintStr)){
            setHint(hintStr)
            helperText = hintStr
        }
    }
}