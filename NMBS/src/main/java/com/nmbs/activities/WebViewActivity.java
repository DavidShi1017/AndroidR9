package com.nmbs.activities;

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

import com.nmbs.R;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.RefreshDossierAsyncTask;
import com.nmbs.async.UploadDossierAsyncTask;

import com.nmbs.log.LogUtils;

import com.nmbs.util.AppLanguageUtils;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.Utils;


public class WebViewActivity extends AppCompatActivity {

    private final static String Intent_Key_Url = "url";
    private final static String Intent_Key_Flow = "flow";
    private final static String Intent_Key_Dnr = "dnr";
    private final static String TAG = "WebViewActivity";
    private WebView webView;
    private String url, urlTemp, email, dnr;
    private ProgressBar progressbar;
    public final static int BOOKING_FLOW = 0;
    public final static int EXCHANGE_FLOW = 1;
    public final static int REFUND_FLOW = 2;
    public final static int NORMAL_FLOW = 3;
    public final static int OPTION_FLOW = 4;
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
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().supportMultipleWindows();
        progressbar = (ProgressBar) findViewById(R.id.myProgressBar);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d(TAG, "onPageStarted...." + url);

            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                LogUtils.e(TAG, "onReceivedSslError....error...." + error);
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }


            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                LogUtils.d(TAG, "shouldOverrideUrlLoading...." + url);
                urlTemp = url;
                String screen = Utils.getUrlValue(url, "screen");
                LogUtils.d(TAG, "screen...." + screen);

                if(screen != null){
                    view.stopLoading();
                    if (screen.equalsIgnoreCase("DossierList")) {
                        finish();
                        startActivity(MyTicketsActivity.createIntent(WebViewActivity.this));
                    } else if (screen.equalsIgnoreCase("UploadDNR") || screen.equalsIgnoreCase("UploadDossier")) {
                        String email = Utils.getUrlValue(url, "email");
                        LogUtils.e("Webview", "email------>" + email);
                        finish();
                        startActivity(UploadDossierActivity.createUploadDossierIntent(WebViewActivity.this,
                                NMBSApplication.PAGE_UploadTicket_DNR, null, dnr, email, responseErrorMessage));
                    } else if (screen.equalsIgnoreCase("home")) {
                        String logout = Utils.getUrlValue(url, "logout");
                        if(logout != null && Boolean.valueOf(logout)){
                            NMBSApplication.getInstance().getLoginService().logOut();
                        }
                        Intent myIntent = new Intent(WebViewActivity.this.getApplicationContext(), MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                        finish();
                    }else if (screen.equalsIgnoreCase("login")) {
                        try {
                            String email = Utils.getUrlValue(url, "email");
                            startActivity(LoginActivity.createIntent(WebViewActivity.this, email));
                            NMBSApplication.getInstance().getLoginService().setEmail(email);
                            finish();
                            return true;
                        } catch (Exception e) {
                            LogUtils.d(TAG, "Exception...." + url);
                        }
                        return true;
                    } else if (screen.equalsIgnoreCase("DossierDetail")) {
                        if (flow == EXCHANGE_FLOW) {
                            String refresh = Utils.getUrlValue(url, "Refresh");
                            if (refresh != null && refresh.equalsIgnoreCase("YES")) {
                                //refresh....
                                refreshDossier();
                                finish();
                                if (lastActivity != null) {
                                    lastActivity.finish();
                                }
                            } else {
                                finish();
                                if (lastActivity != null) {
                                    lastActivity.finish();
                                }
                            }
                        } else if (flow == REFUND_FLOW) {
                            if (isRefundSuccess) {
                                startActivity(MyTicketsActivity.createIntent(WebViewActivity.this));
                            } else {
                                finish();
                                if (lastActivity != null) {
                                    lastActivity.finish();
                                }
                            }
                        }
                    }
                    return true;
                }else {
                    String navigation = Utils.getUrlValue(url, "Navigation");
                    if(navigation != null && navigation.equalsIgnoreCase("InApp")){
                        view.stopLoading();
                        Intent myIntent = new Intent(WebViewActivity.this.getApplicationContext(), MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                        finish();
                        return true;
                    }else{
                        String scheme = Utils.getUrlScheme(url);
                        LogUtils.d(TAG, "scheme...." + scheme);
                        if (scheme.equalsIgnoreCase("bepgenapp")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        } else if (scheme.equalsIgnoreCase("tel")) {
                    /*String telNumber = url.substring(url.indexOf(":") + 1);
                    Log.d(TAG, "telNumber...." + telNumber);*/
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            if (ActivityCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    int res = checkSelfPermission(Manifest.permission.CALL_PHONE);
                                    if (res != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                                return true;
                            }
                            startActivity(intent);
                            return true;
                        }
                        if(url.contains("pdfservice") && url.contains("pdf=")){
                            //https://accept-shared.bene-system.com//hp/pdfservice?pdf=PSCJTXQ1536892386284LS1482018
                            //webView.loadUrl("https://docs.google.com/viewer?url=" + url);
                            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);*/
                            String pdfUrl = "http://xxx.pdf";
                            String data = "<iframe src='http://docs.google.com/gview?embedded=true&url="+ url +"'"+" width='100%' height='100%' style='border: none;'></iframe>";

                            webView.loadData(data, "text/html", "UTF-8");
                            return true;
                        }
                    }

                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.d(TAG, "onPageFinished...." + url);

                /*CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                Log.e("onPageFinished", "Cookies = " + CookieStr);*/

                if(flow == EXCHANGE_FLOW){
                    if(url.contains("ConfirmationConfirmed")){
                        refreshDossier();
                        //refresh....
                    }else if(url.contains("ConfirmationProvisional")){

                    }else if(url.contains("ConfirmationError")){
                        refreshDossier();
                        //refresh....
                    }
                }else if(flow == REFUND_FLOW){
                    if(url.contains("ConfirmationSuccess")){
                        isRefundSuccess = true;
                        refreshDossier();
                        //refresh....
                    }
                }else{
                    LogUtils.d(TAG, "onPageFinished....flow...." + flow);
                    String str = url.substring(url.indexOf("#") + 1);
                    if(str != null && str.length() == 28){
                        NMBSApplication.getInstance().getLoginService().saveCheckOptionViaUrl(url);
                    }
                    if(url.contains("ConfirmationConfirmed") && url.contains("email")){
                        email = Utils.getUrlValue(url, "email");
                        dnr = Utils.getUrlValue(url, "dnr");
                        LogUtils.d(TAG, "onPageFinished....email...." + email);
                        LogUtils.d(TAG, "onPageFinished....dnr...." + dnr);
                        // upload.....
                        if(!UploadDossierAsyncTask.isUploading){
                            UploadDossierAsyncTask asyncTask = new UploadDossierAsyncTask(uploadHandler, getApplicationContext(), dnr, email);
                            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }else if(url.contains("ConfirmationProvisional") && url.contains("email")){
                        email = Utils.getUrlValue(url, "email");
                        dnr = Utils.getUrlValue(url, "dnr");
                    }else if(url.contains("Booking-Confirmation") && url.contains("ConfirmationError")){

                    }
                }

            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(!isFinishing()){
                    showSimpleDialog();
                }
            }



        });

        webView.setWebChromeClient(new WebChromeClient() {


            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {
                LogUtils.d(TAG, "Open the Window..");
                if(NetworkUtils.isOnline(WebViewActivity.this)) {
                    WebView newWebView = new WebView(WebViewActivity.this);
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();

                    newWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                            handler.proceed();
                            super.onReceivedSslError(view, handler, error);
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            if(url.contains("pdfservice") && url.contains("pdf=")){
                                //https://accept-shared.bene-system.com//hp/pdfservice?pdf=PSCJTXQ1536892386284LS1482018
                               // webView.loadUrl("https://docs.google.com/viewer?url=" + url);
                            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);*/
                                startActivity(WebViewPDFActivity.createIntent(WebViewActivity.this, url));

                            }else{
                                startActivity(WebViewOverlayActivity.createIntent(WebViewActivity.this, url));
                            }

                            view.removeAllViews();
                            return true;
                        }
                    });
                }
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                } else {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                    progressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        //synCookies(getApplicationContext(), url);
        webView.loadUrl(url);
    }

/*    public static void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, "SitePreferences_www.accept.b-europe.com={\"c\":\"BE\",\"l\":\"nl\",\"r\":\"EUR\",\"u\":false,\"h\":\"99425697-0ef8-4bbd-bffd-f658c7115007\"}");//指定要修改的cookies
        CookieSyncManager.getInstance().sync();
    }*/

    private void refreshDossier(){
        if(!RefreshDossierAsyncTask.isRefreshing){
            RefreshDossierAsyncTask asyncTask = new RefreshDossierAsyncTask(null, getApplicationContext(), dnr);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private Handler uploadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                case 0:
                    Bundle bundle = msg.getData();
                    responseErrorMessage = bundle.getString(UploadDossierAsyncTask.Intent_Key_ErrorMessage);
                    //Log.d(TAG, "Upload dossier failure, the error msg is.........." + responseErrorMessage);
                    break;
            }
        }
    };
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
    }
    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
        builder.setTitle(R.string.general_information);
        builder.setMessage(R.string.general_server_unavailable);

        //监听下方button点击事件
        builder.setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.general_refresh, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                webView.reload();
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static Intent createIntent(Activity activity, String url, int flow, String dnr){
        LogUtils.d(TAG, "url...." + url);
        lastActivity = activity;
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Intent_Key_Url, url);
        intent.putExtra(Intent_Key_Flow, flow);
        intent.putExtra(Intent_Key_Dnr, dnr);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }
    public static Intent createIntent(Context context, String url, int flow, String dnr){
        LogUtils.d(TAG, "url...." + url);
        //lastActivity = activity;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Intent_Key_Url, url);
        intent.putExtra(Intent_Key_Flow, flow);
        intent.putExtra(Intent_Key_Dnr, dnr);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(WebViewActivity.this, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Intent intentTel = new Intent(Intent.ACTION_CALL, Uri.parse(urlTemp));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intentTel);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
