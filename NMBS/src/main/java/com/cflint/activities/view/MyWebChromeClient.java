package com.cflint.activities.view;


import android.app.Activity;
import android.content.DialogInterface;

import android.net.http.SslError;

import android.os.Message;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cflint.activities.WebViewOverlayActivity;
import com.cflint.activities.WebViewPDFActivity;
import com.cflint.log.LogUtils;
import com.cflint.util.NetworkUtils;


public class MyWebChromeClient extends WebChromeClient {

    private Activity activity;
    public final static String TAG = "MyWebChromeClient";

    private ProgressBar progressbar;

    public MyWebChromeClient(Activity activity, ProgressBar progressbar){
        this.activity = activity;
        this.progressbar = progressbar;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg) {
        LogUtils.d(TAG, "Open the Window..");
        if(NetworkUtils.isOnline(activity)) {
            WebView newWebView = new WebView(activity);
            view.addView(newWebView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(com.cflint.R.string.notification_error_ssl_cert_invalid);
                    builder.setPositiveButton(activity.getString(com.cflint.R.string.alert_continue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton(activity.getString(com.cflint.R.string.general_cancel), new DialogInterface.OnClickListener() {
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
                    if(url.contains("pdfservice") && url.contains("pdf=")){
                        //https://accept-shared.bene-system.com//hp/pdfservice?pdf=PSCJTXQ1536892386284LS1482018
                        // webView.loadUrl("https://docs.google.com/viewer?url=" + url);
                            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);*/
                        activity.startActivity(WebViewPDFActivity.createIntent(activity, url));

                    }else{
                        activity.startActivity(WebViewOverlayActivity.createIntent(activity, url));
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

}
