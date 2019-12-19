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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.async.UploadDossierAsyncTask;
import com.cflint.log.LogUtils;
import com.cflint.util.AppLanguageUtils;
import com.cflint.util.NetworkUtils;
import com.cflint.util.Utils;

public class WebViewOverlayActivity extends AppCompatActivity {

    private final static String Intent_Key_Url = "url";
    private final static String TAG = "WebViewOverlayActivity";
    private WebView webView;
    private ProgressBar progressbar;
    private TextView tvTitle;
    private String url, urltemp, email, dnr;
    private String responseErrorMessage;

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
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d(TAG, "onPageStarted...." + url);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewOverlayActivity.this);
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton(getString(R.string.alert_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton(getString(R.string.general_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                LogUtils.d(TAG, "shouldOverrideUrlLoading...." + url);
                urltemp = url;
                String screen = Utils.getUrlValue(url, "screen");
                LogUtils.d(TAG, "screen...." + screen);

                if(screen != null){
                    view.stopLoading();
                    if (screen.equalsIgnoreCase("DossierList")) {
                        finish();
                        startActivity(MyTicketsActivity.createIntent(WebViewOverlayActivity.this));
                    } else if (screen.equalsIgnoreCase("UploadDNR")) {
                        String email = Utils.getUrlValue(url, "email");
                        String dnr = Utils.getUrlValue(url, "dnr");
                        finish();
                        startActivity(UploadDossierActivity.createUploadDossierIntent(WebViewOverlayActivity.this,
                                NMBSApplication.PAGE_UploadTicket_DNR, null, dnr, email, responseErrorMessage));
                    } else if (screen.equalsIgnoreCase("home")) {
                        String logout = Utils.getUrlValue(url, "logout");
                        if(logout != null && Boolean.valueOf(logout)){
                            NMBSApplication.getInstance().getLoginService().logOut();
                        }
                        Intent myIntent = new Intent(WebViewOverlayActivity.this.getApplicationContext(), MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                        finish();
                    }else if (screen.equalsIgnoreCase("login")) {
                        try {
                            String email = Utils.getUrlValue(url, "email");
                            startActivity(LoginActivity.createIntent(WebViewOverlayActivity.this, email));
                            NMBSApplication.getInstance().getLoginService().setEmail(email);
                            finish();
                            return true;
                        } catch (Exception e) {
                            Log.d(TAG, "Exception...." + url);
                        }
                        return true;
                    }
                }else {
                    String navigation = Utils.getUrlValue(url, "Navigation");
                    if(navigation != null && navigation.equalsIgnoreCase("InApp")){
                        view.stopLoading();
                        Intent myIntent = new Intent(WebViewOverlayActivity.this.getApplicationContext(), MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                        finish();
                    }else {
                        String scheme = Utils.getUrlScheme(url);
                        LogUtils.d(TAG, "scheme...." + scheme);
                        if (scheme.equalsIgnoreCase("bepgenapp")) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }catch (Exception e){

                            }
                            return true;
                        } else if (scheme.equalsIgnoreCase("tel")) {
                    /*String telNumber = url.substring(url.indexOf(":") + 1);
                    Log.d(TAG, "telNumber...." + telNumber);*/
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            if (ActivityCompat.checkSelfPermission(WebViewOverlayActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    int res = checkSelfPermission(Manifest.permission.CALL_PHONE);
                                    if (res != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                                return false;
                            }
                            startActivity(intent);
                            return true;
                        }
                        if(url.contains("pdfservice") && url.contains("pdf=")){
                            //https://accept-shared.bene-system.com//hp/pdfservice?pdf=PSCJTXQ1536892386284LS1482018
                            webView.loadUrl("https://docs.google.com/viewer?url=" + url);
                        }
                    }
                    NMBSApplication.getInstance().getLoginService().saveProfileViaUrl(url);
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished...." + url);

                if(url.contains("ConfirmationConfirmed") && url.contains("email")){;
                    email = Utils.getUrlValue(url, "email");
                    dnr = Utils.getUrlValue(url, "dnr");
                    // upload.....
                    if(!UploadDossierAsyncTask.isUploading){
                        UploadDossierAsyncTask asyncTask = new UploadDossierAsyncTask(uploadHandler, getApplicationContext(), dnr, email);
                        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }else if(url.contains("ConfirmationProvisional") && url.contains("email")){
                    email = Utils.getUrlValue(url, "email");
                    dnr = Utils.getUrlValue(url, "dnr");
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
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("ANDROID_LAB", "TITLE=" + title);
                tvTitle.setText(title);
            }
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {
                Log.d(TAG, "Open the Window..");
                if(NetworkUtils.isOnline(WebViewOverlayActivity.this)) {
                    WebView newWebView = new WebView(WebViewOverlayActivity.this);
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();

                    newWebView.setWebViewClient(new WebViewClient() {

                        @Override
                        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewOverlayActivity.this);
                            builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                            builder.setPositiveButton(getString(R.string.alert_continue), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.proceed();
                                }
                            });
                            builder.setNegativeButton(getString(R.string.general_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.cancel();
                                }
                            });
                            final AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            webView.loadUrl(url);
                            Log.d(TAG, "Open the Window.." + url);
                            return true;
                        }
                    });
                }
                return true;
            }
        });

        webView.loadUrl(url);
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

    public static Intent createIntent(Context context, String url){
        Log.d(TAG, "url...." + url);
        Intent intent = new Intent(context, WebViewOverlayActivity.class);
        intent.putExtra(Intent_Key_Url, url);
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
                    Toast.makeText(WebViewOverlayActivity.this, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Intent intentTel = new Intent(Intent.ACTION_CALL, Uri.parse(urltemp));
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
    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewOverlayActivity.this);
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
