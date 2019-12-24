package com.cflint.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Window;

import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cflint.R;

import com.cflint.application.NMBSApplication;
import com.cflint.async.RefreshDossierAsyncTask;
import com.cflint.async.UploadDossierAsyncTask;

import com.cflint.log.LogUtils;

import com.cflint.util.AppLanguageUtils;
import com.cflint.util.NetworkUtils;
import com.cflint.util.Utils;
import com.cflint.activities.view.MyWebChromeClient;
import com.cflint.activities.view.MyWebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private final static String Intent_Key_Url = "url";
    private final static String Intent_Key_Flow = "flow";
    private final static String Intent_Key_Dnr = "dnr";
    private final static String TAG = "WebViewActivity";
    private WebView webView;
    private String url, urltemp, email, dnr;
    private ProgressBar progressbar;
    public final static int BOOKING_FLOW = 0;
    public final static int EXCHANGE_FLOW = 1;
    public final static int REFUND_FLOW = 2;
    public final static int NORMAL_FLOW = 3;
    private int flow;
    private static Activity lastActivity;
    private String responseErrorMessage;
    private boolean isRefundSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        setContentView(R.layout.activity_web_view);
        //LogUtils.w("WebViewActivity", "WebViewActivity--------->");
        url = getIntent().getStringExtra(Intent_Key_Url);
        flow = getIntent().getIntExtra(Intent_Key_Flow, 3);
        dnr = getIntent().getStringExtra(Intent_Key_Dnr);
        webView = (WebView) findViewById(R.id.webview);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setTextZoom(100);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT );
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().supportMultipleWindows();
        progressbar = (ProgressBar) findViewById(R.id.myProgressBar);
        webView.setWebViewClient(new MyWebViewClient(this, webView));

        webView.setWebChromeClient(new MyWebChromeClient(this, progressbar));
        //synCookies(getApplicationContext(), url);
        webView.loadUrl(url);
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
    }


    public static Intent createIntent(Activity activity, String url, int flow, String dnr) {
        LogUtils.d(TAG, "url...." + url);
        lastActivity = activity;
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Intent_Key_Url, url);
        intent.putExtra(Intent_Key_Flow, flow);
        intent.putExtra(Intent_Key_Dnr, dnr);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    public static Intent createIntent(Context context, String url, int flow, String dnr) {
        LogUtils.d(TAG, "url...." + url);
        //lastActivity = activity;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Intent_Key_Url, url);
        intent.putExtra(Intent_Key_Flow, flow);
        intent.putExtra(Intent_Key_Dnr, dnr);
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
}
