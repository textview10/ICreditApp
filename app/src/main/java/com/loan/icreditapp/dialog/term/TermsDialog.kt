package com.loan.icreditapp.dialog.term

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ScreenUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.api.Api

class TermsDialog : Dialog {

    private var webView : WebView? = null
    private var pbLoading : ProgressBar? = null
    private var tvAgree : AppCompatTextView? = null
    private var tvCancel : AppCompatTextView? = null

    private var URL : String = Api.GET_ALL
    private var mListener: OnClickAgreeListener? = null

    constructor(context: Context) : super(context, R.style.TermDialogTheme) {
        val lp = window!!.attributes
        lp.width = LayoutParams.MATCH_PARENT //设置宽度
        lp.height = LayoutParams.MATCH_PARENT //设置宽度
        window!!.attributes = lp
        setContentView(R.layout.dialog_terms)
        webView = findViewById(R.id.webview_dialog_term)
        pbLoading = findViewById(R.id.pb_dialog_term_loading)
        tvAgree = findViewById(R.id.tv_dialog_term_comfirm)
        tvCancel = findViewById(R.id.tv_dialog_term_cancel)
        initializeData()
    }

    private fun initializeData() {
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress > 100 && pbLoading == null) {
                    return
                }
                if (newProgress == 100) {
                    if (pbLoading?.getVisibility() == View.VISIBLE) {
                        pbLoading?.setVisibility(View.GONE)
                    }
                } else {
                    if (pbLoading?.getVisibility() != View.VISIBLE) {
                        pbLoading?.setVisibility(View.VISIBLE)
                    }
                    pbLoading?.setProgress(newProgress)
                }
                //                Log.e("Test", " on progress changed = " + newProgress);
            }
        }
        webView!!.webViewClient = webViewClient
        webView!!.settings.javaScriptEnabled = true
        if (!TextUtils.isEmpty(URL)) {
            webView!!.loadUrl(URL)
            //            Log.e("Test", " url 1 = " + mUrl);
        }
        tvAgree?.setOnClickListener(View.OnClickListener {
            if (mListener != null){
                mListener?.onClickAgree()
            }
        })
        tvCancel?.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    fun setUrl(url: String) {
        URL = url
        if (webView != null) {
            webView!!.loadUrl(URL)
            Log.e("Test", " url = $URL")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun onDestroyDialog(){
        webView?.destroy()
    }

    fun setOnClickListener(listener: OnClickAgreeListener){
        mListener = listener
    }

    interface OnClickAgreeListener {
        fun onClickAgree()
    }
}