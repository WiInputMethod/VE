package com.hit.wi.t9.settings;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hit.wi.t9.R;
import com.umeng.analytics.MobclickAgent;


public final class DonateWiActivity extends Activity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        final WebView webView = ((WebView) findViewById(R.id.webview));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://54.69.160.111:8000/dynamic/donate/alipay");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.loadUrl("file:///android_asset/html/donate.html");

            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

}
