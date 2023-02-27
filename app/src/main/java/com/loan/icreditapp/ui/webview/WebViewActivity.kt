package com.loan.icreditapp.ui.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseActivity

class WebViewActivity : BaseActivity() {
    private var ivBack: ImageView? = null
    private var tvTitle: TextView? = null

    companion object {

        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TYPE = "extra_type"
        const val TYPE_PRIVACY = 111
        const val TYPE_TERMS = 112

        fun launchWebView(context: Context, url: String, type: Int) {
            var intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_TYPE, type)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_webview)

        ivBack = findViewById(R.id.iv_webview_back)
        tvTitle = findViewById(R.id.tv_webview_title)
        ivBack?.setOnClickListener(View.OnClickListener {
            finish()
        })
        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        var titleStr : String? = null
        if (type == TYPE_TERMS){
            titleStr = getString(R.string.about_terms)
        } else if (type == TYPE_PRIVACY){
            titleStr = getString(R.string.about_privacy)
        }
        if (!TextUtils.isEmpty(titleStr)){
            tvTitle?.text = titleStr
        }

        val url = intent.getStringExtra(EXTRA_URL)
        val webViewFragment = WebViewFragment()
        webViewFragment.setUrl(url)
        toFragment(webViewFragment)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_activity_webview_container
    }
}