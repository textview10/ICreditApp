package com.loan.icreditapp.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnKeyListener
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import com.loan.icreditapp.BuildConfig
import com.loan.icreditapp.R

class InputVerifyCodeView : LinearLayout {

    private var etNum1: AppCompatEditText? = null
    private var etNum2: AppCompatEditText? = null
    private var etNum3: AppCompatEditText? = null
    private var etNum4: AppCompatEditText? = null
    private var etNum5: AppCompatEditText? = null
    private var etNum6: AppCompatEditText? = null
    private var view1: View? = null
    private var view2: View? = null
    private var view3: View? = null
    private var view4: View? = null
    private var view5: View? = null
    private var view6: View? = null
    private var focus1 = false
    private var focus2: Boolean = false
    private var focus3: kotlin.Boolean = false
    private var focus4: kotlin.Boolean = false
    private var focus5: kotlin.Boolean = false
    private var focus6: kotlin.Boolean = false

    private var hasFocus = false
    private var selectedColor = 0
    private var unselectedColor: kotlin.Int = 0


    constructor(context: Context?) : super(context) {

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        etNum1 = findViewById(R.id.et_verify_num_1)
        etNum2 = findViewById<AppCompatEditText>(R.id.et_verify_num_2)
        etNum3 = findViewById<AppCompatEditText>(R.id.et_verify_num_3)
        etNum4 = findViewById<AppCompatEditText>(R.id.et_verify_num_4)
        etNum5 = findViewById<AppCompatEditText>(R.id.et_verify_num_5)
        etNum6 = findViewById<AppCompatEditText>(R.id.et_verify_num_6)
        view1 = findViewById(R.id.view_verify_num_1)
        view2 = findViewById<View>(R.id.view_verify_num_2)
        view3 = findViewById<View>(R.id.view_verify_num_3)
        view4 = findViewById<View>(R.id.view_verify_num_4)
        view5 = findViewById<View>(R.id.view_verify_num_5)
        view6 = findViewById<View>(R.id.view_verify_num_6)
        etNum1?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            focus1 = b
            updateViewIndicate(0)
        })
        etNum2?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            focus2 = b
            updateViewIndicate(1)
        })
        etNum3?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            focus3 = b
            updateViewIndicate(2)
        })
        etNum4?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            focus4 = b
            updateViewIndicate(3)
        })
        etNum5?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            focus5 = b
            updateViewIndicate(4)
        })
        etNum6?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            focus6 = b
            updateViewIndicate(5)
        })
        etNum1?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onTextChange(etNum1, etNum2, null)
            }
        })
        etNum2?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onTextChange(etNum2, etNum3, etNum1)
            }
        })
        etNum3?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }
            override fun afterTextChanged(editable: Editable) {
                onTextChange(etNum3, etNum4, etNum2)
            }
        })
        etNum4?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onTextChange(etNum4, etNum5, etNum3)
            }
        })
        etNum5?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onTextChange(etNum5, etNum6, etNum4)
            }
        })
        etNum6?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                onTextChange(etNum6, null, etNum5)
            }
        })
        selectedColor = resources.getColor(R.color.verify_sms_select)
        unselectedColor = resources.getColor(R.color.verify_sms_unselect)
        etNum1?.requestFocus()
//        if (true){
//            return
//        }
        etNum1?.setOnKeyListener(OnKeyListener { view, i, keyEvent ->
            onDeleteText(i, etNum1, null)
            false
        })
        etNum2?.setOnKeyListener(OnKeyListener { view, i, keyEvent ->
            onDeleteText(i, etNum2, etNum1)
            false
        })
        etNum3?.setOnKeyListener(OnKeyListener { view, i, keyEvent ->
            onDeleteText(i, etNum3, etNum2)
            false
        })
        etNum4?.setOnKeyListener(OnKeyListener { view, i, keyEvent ->
            onDeleteText(i, etNum4, etNum3)
            false
        })
        etNum5?.setOnKeyListener(OnKeyListener { view, i, keyEvent ->
            onDeleteText(i, etNum5, etNum4)
            false
        })
        etNum6?.setOnKeyListener(OnKeyListener { view, i, keyEvent ->
            onDeleteText(i, etNum6, etNum5)
            false
        })
    }

    private fun onDeleteText(keyCode : Int, cur: AppCompatEditText?, pre: AppCompatEditText?){
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            val text = cur!!.text.toString()
            if (TextUtils.isEmpty(text)){
                if (pre != null){
                    pre.requestFocus()
                }
            }
        }
    }

    private fun onTextChange(
        cur: AppCompatEditText?,
        next: AppCompatEditText?,
        pre: AppCompatEditText?
    ) {
        val text = cur!!.text.toString()
        if (!TextUtils.isEmpty(text)) {
            val length = text.length
            if (length > 0) {
                if (length > 1) {
                    cur.setText(text.substring(0, 1))
                    if (length == 2) {
                        next?.setText(text.substring(1, 2))
                        next?.setSelection(1)
                    }
                } else {
//                    cur?.setSelection()
                }
                try {
                    cur.setSelection(1)
                    cur.requestFocus()
                } catch (e : Exception) {

                }
                if (next != null) {
                    next?.requestFocus()
                }
            }
        } else {
            //当删除时
            if (pre != null) {
                val textLength = pre.text!!.length
                if (textLength > 0) {
                    pre.setSelection(textLength)
                }
                pre.requestFocus()
            }
        }
        checkInputComplete()
    }

    fun getVerifyCode(): String? {
        val s1 = getVerifyItemCode(etNum1)
        val s2 = getVerifyItemCode(etNum2)
        val s3 = getVerifyItemCode(etNum3)
        val s4 = getVerifyItemCode(etNum4)
        val s5 = getVerifyItemCode(etNum5)
        val s6 = getVerifyItemCode(etNum6)
        return if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)
            || TextUtils.isEmpty(s3) || TextUtils.isEmpty(s4)
            || TextUtils.isEmpty(s5) || TextUtils.isEmpty(s6)
        ) {
            ""
        } else s1 + s2 + s3 + s4 + s5 + s6
    }

    private fun getVerifyItemCode(et: AppCompatEditText?): String? {
        return if (et != null && et.text != null) {
            if (et.text.toString().length == 0) {
                null
            } else if (et.text.toString().length == 1) {
                et.text.toString()
            } else {
                et.text.toString().substring(0, 1)
            }
        } else null
    }

    private fun checkInputComplete(): Boolean {
        if (checkInputItemComplete(etNum1) && checkInputItemComplete(etNum2)
            && checkInputItemComplete(etNum3) && checkInputItemComplete(etNum4)
            && checkInputItemComplete(etNum5) && checkInputItemComplete(etNum6)
        ) {
            if (mObserver != null) {
                mObserver!!.onEnd()
            }
            return true
        }
        return false
    }

    private fun checkInputItemComplete(et: AppCompatEditText?): Boolean {
        if (et == null || et.text == null) {
            return false
        }
        if (TextUtils.isEmpty(et.text.toString())) {
            return false
        }
        return if (et.text.toString().length != 1) {
            false
        } else true
    }

    private fun updateViewIndicate(index: Int) {
        view1?.setBackgroundColor(unselectedColor)
        view2?.setBackgroundColor(unselectedColor)
        view3?.setBackgroundColor(unselectedColor)
        view4?.setBackgroundColor(unselectedColor)
        view5?.setBackgroundColor(unselectedColor)
        view6?.setBackgroundColor(unselectedColor)
        when (index) {
            0 -> view1?.setBackgroundColor(selectedColor)
            1 -> view2?.setBackgroundColor(selectedColor)
            2 -> view3?.setBackgroundColor(selectedColor)
            3 -> view4?.setBackgroundColor(selectedColor)
            4 -> view5?.setBackgroundColor(selectedColor)
            5 -> view6?.setBackgroundColor(selectedColor)
        }
        checkHasFocus()
    }

    private fun checkHasFocus() {
        var finalFocus = false
        if (focus1 || focus2 || focus3 || focus4 || focus5 || focus6) {
            finalFocus = true
        }
        if (hasFocus != finalFocus) {
            hasFocus = finalFocus
            if (mObserver != null) {
                mObserver!!.onFocusChange(hasFocus)
            }
        }
    }

    fun setVerifyCode(authCode : String) {
        try {
            val num1 = authCode[0].toString()
            val num2 = authCode[1].toString()
            val num3 = authCode[2].toString()
            val num4 = authCode[3].toString()
            val num5 = authCode[4].toString()
            val num6 = authCode[5].toString()
            etNum1?.setText(num1)
            etNum2?.setText(num2)
            etNum3?.setText(num3)
            etNum4?.setText(num4)
            etNum5?.setText(num5)
            etNum6?.setText(num6)
        } catch (e : java.lang.Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
    }

    fun clearAll() {
        etNum1?.setText("")
        etNum2?.setText("")
        etNum3?.setText("")
        etNum4?.setText("")
        etNum5?.setText("")
        etNum6?.setText("")
        etNum1?.requestFocus()
    }

    private var mObserver: Observer? = null

    fun getEditText() :AppCompatEditText?{
       return etNum1
    }

    fun setObserver(observer: Observer?) {
        mObserver = observer
    }

    interface Observer {
        fun onEnd()
        fun onFocusChange(hasFocus: Boolean)
    }
}