package com.cflint.activities;

import android.Manifest;
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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cflint.R;
import com.cflint.activities.view.DialogLogout;
import com.cflint.activities.view.DialogMyOptions;
import com.cflint.activities.view.MyWebChromeClient;
import com.cflint.activities.view.MyWebViewClient;
import com.cflint.application.NMBSApplication;
import com.cflint.async.ProfileInfoAsyncTask;
import com.cflint.async.UploadDossierAsyncTask;
import com.cflint.log.LogUtils;
import com.cflint.model.GeneralSetting;
import com.cflint.util.AppLanguageUtils;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.NetworkUtils;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

public class WebViewCreateActivity extends AppCompatActivity {

    private final static String Intent_Key_Url = "url";
    public final static String TAG = "WebViewOverlayActivity";
    private WebView webView;
    private ProgressBar progressbar;

    private String url;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ImageView ivLogin, ivLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        setContentView(R.layout.activity_web_view_create);
        url = getIntent().getStringExtra(Intent_Key_Url);
        webView = (WebView) findViewById(R.id.webview);

        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().supportMultipleWindows();
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setTextZoom(100);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setSupportMultipleWindows(true);
        progressbar = (ProgressBar) findViewById(R.id.myProgressBar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);

        ivLogin =  findViewById(R.id.iv_login);
        ivLogout = findViewById(R.id.iv_logout);
        showLogin();
        webView.setWebViewClient(new MyWebViewClient(this, webView));

        webView.setWebChromeClient(new MyWebChromeClient(this, progressbar));

        webView.loadUrl(url);
    }



    public static Intent createIntent(Context context, String url){
        Log.d(TAG, "url...." + url);
        Intent intent = new Intent(context, WebViewCreateActivity.class);
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


    public void logout(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        if(!isFinishing()){
            DialogLogout dialogLogout = new DialogLogout(WebViewCreateActivity.this, TAG);
            dialogLogout.show();
        }

        //finish();
    }

    public void login(View view){
        startActivity(LoginActivity.createIntent(WebViewCreateActivity.this, ""));
    }




    private void showLogin(){
        if(NMBSApplication.getInstance().getLoginService().isLogon()){
            LogUtils.e("showLogin", "showLogin------->" + NMBSApplication.getInstance().getLoginService().getLogonInfo());
            ivLogin.setVisibility(View.GONE);
            ivLogout.setVisibility(View.VISIBLE);
        }else {

            ivLogin.setVisibility(View.VISIBLE);
            ivLogout.setVisibility(View.GONE);


            //rlMyOption.setBackgroundColor(getColor(R.color.dot_active));
        }
    }
}
