package com.loan.icreditapp.ui.profile.widget

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.loan.icreditapp.R

class SelectContainer : FrameLayout {

    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var ivIcon: ImageView? = null
    private var hint: String? = null
    private var desc: String? = null
    private var textColor : Int? = null

    @DrawableRes
    private var drawableRes = 0

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
                desc = typedArray.getString(R.styleable.select_view_select_view_desc)
                drawableRes = typedArray.getResourceId(
                    R.styleable.select_view_select_view_icon,
                    R.drawable.ic_profile_bottom_arrow
                )
            }
        }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_select, this, false)
        tvTitle = view.findViewById(R.id.view_select_tv_title)
        tvDesc = view.findViewById<TextView>(R.id.view_select_tv_desc)
        ivIcon = view.findViewById(R.id.view_select_iv_icon)
        tvTitle?.setText(hint)
        if (!TextUtils.isEmpty(desc)) {
            tvDesc?.setText(desc)
        }
        if (drawableRes != 0) {
            ivIcon?.setImageResource(drawableRes)
        }
        addView(view)

        textColor = Color.parseColor("#333333")

    }

    fun setData(data: String?) {
        tvDesc?.text = data
        if (textColor != null){
            tvDesc?.setTextColor(textColor!!)
        }
    }

    fun getData(): String {
        return if (tvDesc == null) "" else tvDesc?.getText().toString()
    }

    fun isEmptyText(): Boolean {
        val data = getData()
        return TextUtils.isEmpty(data)
    }

    fun setShowMode() {
        isFocusable = false
        isEnabled = false
        isClickable = false
        if (ivIcon != null) {
            ivIcon!!.visibility = GONE
        }
    }
}