package com.loan.icreditapp.ui.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import com.loan.icreditapp.R

class GenderCheckBox : LinearLayout {

    constructor(context: Context?) : super(context) {

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

    }

    private var ivMale: ImageView? = null
    private var ivFamily: ImageView? = null
    private var llMale: LinearLayout? = null
    private var llFamily: LinearLayout? = null

    private var mCurPos = 1 //1男2女

    override fun onFinishInflate() {
        super.onFinishInflate()
        ivMale = findViewById(R.id.iv_person_profile_check_male)
        ivFamily = findViewById<ImageView>(R.id.iv_person_profile_check_family)
        llMale = findViewById(R.id.ll_person_profile_male_container)
        llFamily = findViewById<LinearLayout>(R.id.ll_person_profile_family_container)
        llMale?.setOnClickListener(OnClickListener {
            mCurPos = 1
            updateState()
        })
        llFamily?.setOnClickListener(OnClickListener {
            mCurPos = 2
            updateState()
        })
    }

    private fun updateState() {
        if (mCurPos == 1) {
            ivMale?.setImageResource(R.drawable.ic_checkbox_check)
            ivFamily?.setImageResource(R.drawable.ic_checkbox_uncheck)
        } else if (mCurPos == 2) {
            ivMale?.setImageResource(R.drawable.ic_checkbox_uncheck)
            ivFamily?.setImageResource(R.drawable.ic_checkbox_check)
        }
    }

    fun getCurPos(): Int {
        return mCurPos
    }

    fun setPos(pos: Int) {
        if (pos == 1 || pos == 2) {
            mCurPos = pos
            updateState()
        }
    }

    fun setShowMode() {
        isFocusable = false
        isEnabled = false
        isClickable = false
        llMale?.isClickable = false
        llFamily?.isClickable = false
    }
}