package com.nmbs.activities;

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

import com.nmbs.R;
import com.nmbs.activities.view.DialogLogout;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.async.UploadDossierAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.model.GeneralSetting;
import com.nmbs.util.AppLanguageUtils;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

public class WebViewCreateActivity extends AppCompatActivity {

    private final static String Intent_Key_Url = "url";
    public final static String TAG = "WebViewOverlayActivity";
    private WebView webView;
    private ProgressBar progressbar;
    private TextView tvTitle;
    private String url, urlTemp;
    private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ImageView ivLogin, ivLogout;
    private String title;
    private String email, dnr;
    private String responseErrorMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        setContentView(R.layout.activity_web_view_create);
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
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().supportMultipleWindows();
        webView.getSettings().setSupportMultipleWindows(true);
        progressbar = (ProgressBar) findViewById(R.id.myProgressBar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);

        ivLogin =  findViewById(R.id.iv_login);
        ivLogout = findViewById(R.id.iv_logout);
        showLogin();
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d(TAG, "onPageStarted...." + url);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
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
                        startActivity(MyTicketsActivity.createIntent(WebViewCreateActivity.this));
                    } else if (screen.equalsIgnoreCase("UploadDNR")) {
                        String email = Utils.getUrlValue(url, "email");
                        String dnr = Utils.getUrlValue(url, "dnr");
                        finish();
                        startActivity(UploadDossierActivity.createUploadDossierIntent(WebViewCreateActivity.this,
                                NMBSApplication.PAGE_UploadTicket_DNR, null, dnr, email, responseErrorMessage));
                    } else if (screen.equalsIgnoreCase("home")) {
                        String logout = Utils.getUrlValue(url, "logout");
                        if(logout != null && Boolean.valueOf(logout)){
                            NMBSApplication.getInstance().getLoginService().logOut();
                        }
                        Intent myIntent = new Intent(WebViewCreateActivity.this.getApplicationContext(), MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                        finish();
                    }else if (screen.equalsIgnoreCase("login")) {
                        try {
                            String email = Utils.getUrlValue(url, "email");
                            startActivity(LoginActivity.createIntent(WebViewCreateActivity.this, email));
                            NMBSApplication.getInstance().getLoginService().setEmail(email);
                            finish();
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
                        Intent myIntent = new Intent(WebViewCreateActivity.this.getApplicationContext(), MainActivity.class);
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
                            if (ActivityCompat.checkSelfPermission(WebViewCreateActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    int res = checkSelfPermission(Manifest.permission.CALL_PHONE);
                                    if (res != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
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
                LogUtils.d(TAG, "onPageFinished...." + url);

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
            public void onReceivedTitle(WebView view, String titleWebview) {
                super.onReceivedTitle(view, title);
                if(title == null || title.isEmpty()){
                    title = titleWebview;
                }
                tvTitle.setText(title);
            }
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {
                LogUtils.d(TAG, "Open the Window..");
                if(NetworkUtils.isOnline(WebViewCreateActivity.this)) {
                    WebView newWebView = new WebView(WebViewCreateActivity.this);
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
                            webView.loadUrl(url);
                            LogUtils.d(TAG, "Open the Window.." + url);
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
        LogUtils.d(TAG, "url...." + url);
        Intent intent = new Intent(context, WebViewCreateActivity.class);
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
                    Toast.makeText(WebViewCreateActivity.this, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
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
    private void showSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewCreateActivity.this);
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


    public void logout(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        DialogLogout dialogLogout = new DialogLogout(WebViewCreateActivity.this, TAG);
        dialogLogout.show();
        //finish();
    }

    public void login(View view){
        startActivity(LoginActivity.createIntent(WebViewCreateActivity.this, ""));
    }


    public void messages(View view) {

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(com.nmbs.activities.MessageActivity.createIntent(getApplicationContext(),
                                        NMBSApplication.getInstance().getMessageService().getMessageResponse()));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void test(View view){
        startActivity(TestActivity.createIntent(this));
        finish();
    }

    public void stationBoard(View view){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(com.nmbs.activities.StationBoardActivity.createIntent(getApplicationContext(), null));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void stations(View view){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(com.nmbs.activities.StationInfoActivity.createIntent(getApplicationContext()));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
        //startActivity(com.nmbs.activities.CallCenterActivity.createIntent(MainActivity.this,0,null,null));
    }

    public void realtimeAlerts(View view){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(com.nmbs.activities.AlertActivity.createIntent(getApplicationContext()));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void bookTicktes(View view){
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_BOOK_YOUR_TICKET,TrackerConstant.HOME_BOOK_YOUR_TICKET_LABEL);
        final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(generalSetting != null && generalSetting.getBookingUrl() != null && !generalSetting.getBookingUrl().isEmpty()){
                                    //Utils.openProwser(SettingsActivity.this, generalSetting.getBookingUrl(), clickToCallService);
                                    if(NetworkUtils.isOnline(WebViewCreateActivity.this)) {
                                        GoogleAnalyticsUtil.getInstance().sendScreen(WebViewCreateActivity.this, TrackerConstant.BOOKING);
                                        startActivity(WebViewActivity.createIntent(getApplicationContext(),
                                                Utils.getUrl(generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
                                    }
                                }
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void lowestFares(View view){
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_SELECT_LOWESTFARES,TrackerConstant.HOME_SELECT_LOWESTFARES_LABEL);
        final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(generalSetting != null && generalSetting.getLffUrl()!= null && !generalSetting.getLffUrl().isEmpty()){
                                    //Utils.openProwser(SettingsActivity.this, generalSetting.getLffUrl(), clickToCallService);
                                    if(NetworkUtils.isOnline(WebViewCreateActivity.this)) {
                                        GoogleAnalyticsUtil.getInstance().sendScreen(WebViewCreateActivity.this, TrackerConstant.LLF);
                                        startActivity(WebViewActivity.createIntent(WebViewCreateActivity.this,
                                                Utils.getUrl(generalSetting.getLffUrl()), WebViewActivity.BOOKING_FLOW, ""));
                                    }
                                }
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }
    public void trainschedules(View view){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(ScheduleSearchActivity.createIntent(getApplicationContext()));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }

    }
    private boolean isGoto;
    public void settings(View view){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(SettingsActivity.createIntent(getApplicationContext()));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void about(View view){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(WizardActivity.createIntent(getApplicationContext(), WizardActivity.Wizard_Home));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }
    public void myTickets(View view){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(MyTicketsActivity.createIntent(getApplicationContext()));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void uploadTickets(View view){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                startActivity(UploadDossierActivity.createUploadDossierIntent(getApplicationContext(), NMBSApplication.PAGE_UploadTicket, null, null, null, null));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }
    public void myOption(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(NMBSApplication.getInstance().getLoginService().isLogon()){
                                    if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
                                        startActivity(WebViewActivity.createIntent(getApplicationContext(),
                                                Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
                                        GoogleAnalyticsUtil.getInstance().sendScreen(WebViewCreateActivity.this, TrackerConstant.CommercialTTLListUrl);
                                    }
                                }else{
                                    DialogMyOptions dialogMyOptions = new DialogMyOptions(WebViewCreateActivity.this);
                                    dialogMyOptions.show();
                                }
                                isGoto = false;
                            }
                        }
                    });
        }else{
            if(NMBSApplication.getInstance().getLoginService().isLogon()){
                if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
                    startActivity(WebViewActivity.createIntent(getApplicationContext(),
                            Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
                    GoogleAnalyticsUtil.getInstance().sendScreen(WebViewCreateActivity.this, TrackerConstant.CommercialTTLListUrl);
                }
            }else{
                DialogMyOptions dialogMyOptions = new DialogMyOptions(this);
                dialogMyOptions.show();
            }
        }


    }

    public void loginOrManage(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(NMBSApplication.getInstance().getLoginService().isLogon()){
                                    ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), null);
                                    profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    startActivity(WebViewCreateActivity.createIntent(getApplicationContext(), Utils.getUrl(generalSetting.getProfileOverviewUrl())));
                                    GoogleAnalyticsUtil.getInstance().sendScreen(WebViewCreateActivity.this, TrackerConstant.ProfileOverviewUrl);
                                }else{
                                    startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
                                }
                                isGoto = false;
                            }
                        }
                    });
        }else{
            if(NMBSApplication.getInstance().getLoginService().isLogon()){
                ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), null);
                profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                startActivity(WebViewCreateActivity.createIntent(getApplicationContext(), Utils.getUrl(generalSetting.getProfileOverviewUrl())));
                GoogleAnalyticsUtil.getInstance().sendScreen(WebViewCreateActivity.this, TrackerConstant.ProfileOverviewUrl);
            }else{
                startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
            }
        }
    }
    private TextView tvMenuOptionCount;
    private RelativeLayout rlMenuMyOption;
    public void clickMenu(View view) {
        //startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
        tvMenuTicketCount = (TextView) findViewById(R.id.tv_menu_ticket_count);
        tvMenuRealtimeCount = (TextView) findViewById(R.id.tv_menu_realtime_count);
        tvMenuMessageCount = (TextView) findViewById(R.id.tv_menu_message_count);
        tvMenuTicketCount.setText("(" + MainActivity.ticketCount + ")");
        tvMenuRealtimeCount.setText("(" + MainActivity.realtimeCount + ")");
        tvMenuMessageCount.setText("(" + MainActivity.messateCount + ")");

        rlMenuMyOption = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_myoptions);
        tvMenuOptionCount = (TextView) findViewById(R.id.tv_menu_option_count);
        tvMenuOptionCount.setText("(" + MainActivity.optionCount + ")");
        TextView tvMenuLogon = (TextView) findViewById(R.id.tv_menu_logon);
        RelativeLayout rlMenuLogin = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_login);
        if(!NMBSApplication.getInstance().getLoginService().isLogon()){
            rlMenuMyOption.setAlpha(0.3f);
            tvMenuOptionCount.setText("");
            tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
            if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
                    && NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl() != null
                    && !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl().isEmpty()){
                rlMenuLogin.setVisibility(View.VISIBLE);
            }else {
                rlMenuLogin.setVisibility(View.GONE);
            }
        }else {
            tvMenuLogon.setText(getResources().getString(R.string.menu_content_manageprofile));
        }
        if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
                && NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl() != null
                && !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl().isEmpty()){
            rlMenuMyOption.setVisibility(View.VISIBLE);
        }else {
            rlMenuMyOption.setVisibility(View.GONE);
        }
        mDrawerLayout.openDrawer(GravityCompat.END);
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
