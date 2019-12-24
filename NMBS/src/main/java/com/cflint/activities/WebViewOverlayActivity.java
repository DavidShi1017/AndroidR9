package com.cflint.activities;


import android.content.Context;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.webkit.WebSettings;
import android.webkit.WebView;

import android.widget.ProgressBar;
import android.widget.TextView;


import com.cflint.R;
import com.cflint.activities.view.MyWebChromeClient;
import com.cflint.activities.view.MyWebViewClient;

import com.cflint.util.AppLanguageUtils;

import com.cflint.util.Utils;

public class WebViewOverlayActivity extends AppCompatActivity {

    private final static String Intent_Key_Url = "url";
    private final static String TAG = "WebViewOverlayActivity";
    private WebView webView;
    private ProgressBar progressbar;
    private TextView tvTitle;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        setContentView(R.layout.activity_web_view_overlay);
        url = getIntent().getStringExtra(Intent_Key_Url);
        webView = (WebView) findViewById(R.id.webview);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setTextZoom(100);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().supportMultipleWindows();
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        progressbar = (ProgressBar) findViewById(R.id.myProgressBar);
        webView.setWebViewClient(new MyWebViewClient(this, webView));

        webView.setWebChromeClient(new MyWebChromeClient(this, progressbar));

        webView.loadUrl(url);
    }



    public static Intent createIntent(Context context, String url){
        Log.d(TAG, "url...." + url);
        Intent intent = new Intent(context, WebViewOverlayActivity.class);
        intent.putExtra(Intent_Key_Url, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
    }
    public void close(View view){
        finish();
    }
}
