package com.hit.wi.t9.settings;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.hit.wi.t9.R;
import com.umeng.analytics.MobclickAgent;


/**
 * 用户手册
 */
public final class HandBookActivity extends Activity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        final WebView webView = ((WebView) findViewById(R.id.webview));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/html/manual.html");
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
