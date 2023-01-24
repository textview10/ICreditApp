package com.loan.icreditapp.ui.profile.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.loan.icreditapp.R

class EditSelectContainer : FrameLayout {

    private var hintStr: String? = null

    @DrawableRes
    private var drawableRes = 0

    private var inputLayout: TextInputLayout? = null
    private var editText: TextInputEditText? = null
    private var ivClear: ImageView? = null

    constructor(context: Context) : super(context) {
        initializeView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initializeView(context, attrs)
    }

    private fun initializeView(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.select_view)
            if (typedArray != null) {
                hintStr = typedArray.getString(R.styleable.select_view_select_view_hint)
                drawableRes = typedArray.getResourceId(
                    R.styleable.select_view_select_view_icon,
                    R.drawable.ic_edit_text_clear
                )
            }
        }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_edit_select_text, this, false)
        inputLayout = view.findViewById(R.id.edit_select_view_input_layout)
        editText = view.findViewById(R.id.edit_select_view_edit_text)
        ivClear = view.findViewById(R.id.edit_select_view_clear)
        inputLayout?.setHint(hintStr)
        if (drawableRes != 0) {
            ivClear?.setImageResource(drawableRes)
        }
        view.setOnClickListener { v: View? ->
            if (mListener != null) {
                mListener!!.onClick()
            }
        }
        addView(view)
    }

    fun isEmptyText(): Boolean {
        return if (editText == null) {
            true
        } else TextUtils.isEmpty(getText())
    }

    fun getText(): String? {
        return if (editText == null) {
            ""
        } else editText!!.text.toString()
    }

    fun setEditTextAndSelection(editTextStr: String) {
        if (editText != null && !TextUtils.isEmpty(editTextStr)) {
            editText!!.setText(editTextStr)
            editText!!.setSelection(editTextStr.length)
        }
    }

    private var mListener: OnBgClickListener? = null

    fun setOnBgClickListener(listener: OnBgClickListener?) {
        mListener = listener
    }

    interface OnBgClickListener {
        fun onClick()
    }
}