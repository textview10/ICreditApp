package com.loan.icreditapp.ui.profile.widget

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.loan.icreditapp.R
import com.loan.icreditapp.ui.widget.MyTextInputLayout

class EditTextContainer : FrameLayout {

    private var editText: TextInputEditText? = null
    private var ivClear: ImageView? = null
    private var hint: String? = null
    private var layout: MyTextInputLayout? = null

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
                hint = typedArray.getString(R.styleable.select_view_select_view_hint)
            }
        }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_edit_text, this, false)
        layout = view.findViewById(R.id.edit_view_input_layout)
        editText = view.findViewById(R.id.edit_view_edit_text)
        ivClear = view.findViewById(R.id.edit_view_clear)
        layout?.setHint(hint)
        layout?.saveText(hint)
        ivClear?.setOnClickListener(OnClickListener {
            if (editText != null) {
                editText!!.setText("")
            }
        })
        editText?.addTextChangedListener(mTextWatcher)
        addView(view)
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (isShowMode) {
                return
            }
            if (!TextUtils.isEmpty(s) && s.length > 0) {
                if (ivClear != null) {
                    ivClear!!.visibility = VISIBLE
                }
            } else {
                if (ivClear != null) {
                    ivClear!!.visibility = INVISIBLE
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
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

    fun getEditText() : TextInputEditText?{
        return editText
    }

    fun setEditTextAndSelection(editTextStr: String) {
        post {
            if (editText != null && !TextUtils.isEmpty(editTextStr)) {
                editText!!.setText(editTextStr)
                editText!!.setSelection(editText!!.text!!.length)
            }
        }
    }

    fun setSelectionLast() {
        if (editText != null) {
            val editTextStr = editText!!.text.toString()
            if (!TextUtils.isEmpty(editTextStr)) {
                editText!!.setText(editTextStr)
                editText!!.setSelection(editText!!.text!!.length)
            }
        }
    }

    fun setPassWordMode(isPasswordMode: Boolean) {
        if (editText != null) {
            editText!!.transformationMethod =
                if (isPasswordMode) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
        }
    }

    fun setInputNum() {
        if (editText != null) {
            editText!!.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (editText != null) {
            editText!!.addTextChangedListener(mTextWatcher)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        layout?.setHint("")
        if (editText != null) {
            editText!!.removeTextChangedListener(mTextWatcher)
        }
    }

    private var isShowMode = false
    fun setShowMode() {
        isShowMode = true
        if (editText != null) {
            editText!!.isFocusable = false
            editText!!.isEnabled = false
            editText!!.isClickable = false
        }
        if (ivClear != null) {
            ivClear!!.visibility = GONE
        }
    }
}