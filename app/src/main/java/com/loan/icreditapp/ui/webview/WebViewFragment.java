package com.loan.icreditapp.ui.webview;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loan.icreditapp.R;
import com.loan.icreditapp.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class WebViewFragment extends BaseFragment {

    private WebView webView;

    private String mUrl;
    private ProgressBar pbLoading;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        initializeView(view);
        initializeData();
        return view;
    }

    private void initializeView(View view) {
        pbLoading = view.findViewById(R.id.pb_webview_loading);
        webView = view.findViewById(R.id.web_view);
//        startLoading();
    }

    private void initializeData() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 100 && pbLoading == null) {
                   return;
                }
                if (newProgress == 100){
                    if (pbLoading.getVisibility() == View.VISIBLE){
                        pbLoading.setVisibility(View.GONE);
                    }
                } else {
                    if (pbLoading.getVisibility() != View.VISIBLE){
                        pbLoading.setVisibility(View.VISIBLE);
                    }
                    pbLoading.setProgress(newProgress);
                }
//                Log.e("Test", " on progress changed = " + newProgress);
            }
        });
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);

        if (!TextUtils.isEmpty(mUrl)) {
            webView.loadUrl(mUrl);
//            Log.e("Test", " url 1 = " + mUrl);
        }
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    };

    public void setUrl(String url) {
        mUrl = url;
        if (webView != null) {
            webView.loadUrl(mUrl);
            Log.e("Test", " url = " + mUrl);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUrl = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    private void startLoading() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 100; i++) {
                    SystemClock.sleep(500);
                    final int index = i;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pbLoading != null) {
                                pbLoading.setProgress(index);
                            }
                        }
                    });
                }

            }
        }.start();
    }
}
