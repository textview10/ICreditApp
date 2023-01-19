package com.loan.icreditapp.dialog.custom

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.blankj.utilcode.util.ScreenUtils
import com.loan.icreditapp.R

class CustomDialog : BaseDialog {
    private var mContext: Context? = null
    private var tvComfirm: TextView? = null
    private var tvTitle: TextView? = null
    private var tvCancel: TextView? = null
    private var flContent: FrameLayout? = null
    private var positiveListener: View.OnClickListener? = null
    private var negativeListener: View.OnClickListener? = null
    private var llContainer: LinearLayout? = null
    private var ivIcon: ImageView? = null
    private var ivClose: ImageView? = null

    constructor(context: Context) : super(context, R.style.DialogTheme) {
        mContext = context
        initializeView()
    }

    constructor(context: Context, themeResId: Int) :super(context, themeResId) {
        mContext = context
        initializeView()
    }

    private fun initializeView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度
            lp.height = (ScreenUtils.getScreenHeight() * 3f /4).toInt() //设置高度
            window?.attributes = lp
        }
        setContentView(R.layout.dialog_base)
        llContainer = findViewById(R.id.ll_custom_dialog_container)
        ivIcon = findViewById(R.id.iv_dialog_icon)
        tvTitle = findViewById<TextView>(R.id.tv_dialog_title)
        tvCancel = findViewById<TextView>(R.id.tv_dialog_cancel)
        tvComfirm = findViewById(R.id.tv_dialog_comfirm)
        flContent = findViewById(R.id.fl_dialog_content)
        ivClose = findViewById(R.id.iv_dialog_close)
        ivClose?.setOnClickListener(View.OnClickListener { v: View? -> dismiss() })
        tvComfirm?.setOnClickListener(View.OnClickListener { v: View? ->
            if (positiveListener != null) {
                positiveListener!!.onClick(v)
            }
            dismiss()
        })
        tvCancel?.setOnClickListener(View.OnClickListener { v: View? ->
            if (negativeListener != null) {
                negativeListener?.onClick(v)
            }
            dismiss()
        })
        llContainer?.setBackgroundResource(R.drawable.shape_corner_light_custom_dialog_bg)
        tvTitle?.setTextColor(context.resources.getColor(R.color.custom_dialog_title_text_color_light))
        tvCancel?.setTextColor(context.resources.getColor(R.color.custom_dialog_cancel_text_color_light))
        tvComfirm?.setTextColor(context.resources.getColor(R.color.custom_dialog_confirm_text_color_light))
    }

    fun setView(@LayoutRes res: Int): CustomDialog? {
        return setView(LayoutInflater.from(context).inflate(res, flContent, false))
    }

    fun setMessage(msg: String?): CustomDialog? {
        if (flContent!!.childCount != 0) {
            flContent!!.removeAllViews()
        }
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_base_text, flContent, false)
        val tvDesc = view.findViewById<TextView>(R.id.tv_base_desc)
        tvDesc.text = msg
        tvDesc.setTextColor(context.resources.getColor(R.color.custom_dialog_text_color_light))
        flContent!!.addView(view)
        return this
    }


    fun setView(view: View?): CustomDialog? {
        if (flContent!!.childCount != 0) {
            flContent!!.removeAllViews()
        }
        flContent!!.addView(view)
        return this
    }

    fun setTitle(title: String?): CustomDialog? {
        if (tvTitle?.getVisibility() != View.VISIBLE) {
            tvTitle?.setVisibility(View.VISIBLE)
        }
        tvTitle?.setText(title)
        return this
    }

    fun setIcon(@DrawableRes res: Int): CustomDialog? {
        if (ivIcon!!.visibility != View.VISIBLE) {
            ivIcon!!.visibility = View.VISIBLE
        }
        ivIcon!!.setImageResource(res)
        return this
    }

    fun setPositiveButton(text: String?, listener: View.OnClickListener?): CustomDialog? {
        tvComfirm!!.visibility = View.VISIBLE
        tvComfirm!!.text = text
        positiveListener = listener
        return this
    }

    fun setNegativeButton(text: String?, listener: View.OnClickListener): CustomDialog? {
        tvCancel?.setVisibility(View.VISIBLE)
        tvCancel?.setText(text)
        this.negativeListener = listener
        return this
    }

    override fun show() {
        if (mContext != null && mContext is Activity) {
            val activity = mContext as Activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isFinishing || activity.isDestroyed) {
                    return
                }
            }
        }
        super.show()
    }

}