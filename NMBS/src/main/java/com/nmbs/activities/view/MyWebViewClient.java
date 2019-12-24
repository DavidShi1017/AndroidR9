package com.nmbs.activities.view;

import android.Manifest;
import android.app.Activity;
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
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.nmbs.activities.LoginActivity;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.activities.UploadDossierActivity;

import com.nmbs.activities.WebViewActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.UploadDossierAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.util.Utils;

public class MyWebViewClient extends WebViewClient implements  ActivityCompat.OnRequestPermissionsResultCallback{

    private Activity activity;
    public final static String TAG = "MyWebViewClient";
    private String url, urlTemp;
    private String responseErrorMessage;
    private String email, dnr;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;
    private WebView webView;
    public MyWebViewClient(Activity activity, WebView webView){
        this.activity = activity;
        this.webView = webView;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtils.d(TAG, "onPageStarted...." + url);
    }
    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(com.nmbs.R.string.notification_error_ssl_cert_invalid);
        builder.setPositiveButton(activity.getString(com.nmbs.R.string.alert_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton(activity.getString(com.nmbs.R.string.general_cancel), new DialogInterface.OnClickListener() {
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
        urlTemp = url;
        String screen = Utils.getUrlValue(url, "screen");
        LogUtils.d(TAG, "screen...." + screen);

        if(screen != null){
            view.stopLoading();
            if (screen.equalsIgnoreCase("DossierList")) {
                activity.finish();
                activity.startActivity(MyTicketsActivity.createIntent(activity));
            } else if (screen.equalsIgnoreCase("UploadDNR")) {
                String email = Utils.getUrlValue(url, "email");
                String dnr = Utils.getUrlValue(url, "dnr");
                activity.finish();
                activity.startActivity(UploadDossierActivity.createUploadDossierIntent(activity,
                        NMBSApplication.PAGE_UploadTicket_DNR, null, dnr, email, responseErrorMessage));
            } else if (screen.equalsIgnoreCase("home")) {
                String logout = Utils.getUrlValue(url, "logout");
                if(logout != null && Boolean.valueOf(logout)){
                    NMBSApplication.getInstance().getLoginService().logOut();
                }
                Intent myIntent = new Intent(activity.getApplicationContext(), MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(myIntent);
                activity.finish();
            }else if (screen.equalsIgnoreCase("login")) {
                try {
                    String email = Utils.getUrlValue(url, "email");
                    activity.startActivity(LoginActivity.createIntent(activity, email));
                    NMBSApplication.getInstance().getLoginService().setEmail(email);
                    activity.finish();
                    return true;
                } catch (Exception e) {
                    LogUtils.d(TAG, "Exception...." + url);
                }
                return true;
            }
        }else {
            String navigation = Utils.getUrlValue(url, "Navigation");
            if(navigation != null && navigation.equalsIgnoreCase("InApp")){
                view.stopLoading();
                Intent myIntent = new Intent(activity.getApplicationContext(), MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(myIntent);
                activity.finish();
                return true;
            }else{
                String scheme = Utils.getUrlScheme(url);
                LogUtils.d(TAG, "scheme...." + scheme);
                if (scheme.equalsIgnoreCase("bepgenapp")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        activity.startActivity(intent);
                    }catch (Exception e){

                    }
                    return true;
                } else if (scheme.equalsIgnoreCase("tel")) {
                    /*String telNumber = url.substring(url.indexOf(":") + 1);
                    Log.d(TAG, "telNumber...." + telNumber);*/
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            int res = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
                            if (res != PackageManager.PERMISSION_GRANTED) {
                                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        }
                        return false;
                    }
                    activity.startActivity(intent);
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
        LogUtils.d(TAG, "onPageFinished...." + url);

        if(url.contains("ConfirmationConfirmed") && url.contains("email")){;
            email = Utils.getUrlValue(url, "email");
            dnr = Utils.getUrlValue(url, "dnr");
            // upload.....
            if(!UploadDossierAsyncTask.isUploading){
                UploadDossierAsyncTask asyncTask = new UploadDossierAsyncTask(uploadHandler, activity.getApplicationContext(), dnr, email);
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
        if(!activity.isFinishing()){
            showSimpleDialog();
        }
    }
    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(com.nmbs.R.string.general_information);
        builder.setMessage(com.nmbs.R.string.general_server_unavailable);

        //监听下方button点击事件
        builder.setPositiveButton(com.nmbs.R.string.general_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(com.nmbs.R.string.general_refresh, new DialogInterface.OnClickListener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Intent intentTel = new Intent(Intent.ACTION_CALL, Uri.parse(urlTemp));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        activity.startActivity(intentTel);
                    }
                    break;
            /*default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);*/
                }
        }
    }
}
