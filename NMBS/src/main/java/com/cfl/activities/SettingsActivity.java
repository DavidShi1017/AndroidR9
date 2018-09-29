package com.cfl.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cfl.R;
import com.cfl.activities.view.DialogChangeLanguageSuccessful;
import com.cfl.activities.view.DialogCheckUpdateNotification;
import com.cfl.activities.view.DialogMyOptions;
import com.cfl.activities.view.DialogSettingsNotifi;
import com.cfl.activities.view.DialogSettingsPassword;
import com.cfl.activities.view.ProgressDialogCustom;
import com.cfl.activity.BaseActivity;
import com.cfl.adapter.CollectionItemAdapter;
import com.cfl.application.NMBSApplication;
import com.cfl.async.CheckUpdateAsyncTask;
import com.cfl.async.ProfileInfoAsyncTask;
import com.cfl.exceptions.NetworkError;
import com.cfl.exceptions.RequestFail;
import com.cfl.listeners.ActivityPostExecuteListener;
import com.cfl.listeners.SettingsListener;
import com.cfl.log.LogUtils;
import com.cfl.model.CheckAppUpdate;
import com.cfl.model.CollectionItem;
import com.cfl.model.GeneralSetting;
import com.cfl.model.HafasUser;
import com.cfl.model.LogonInfo;
import com.cfl.preferences.SettingsPref;
import com.cfl.services.ICheckUpdateService;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.IPushService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;
import com.cfl.util.AppLanguageUtils;
import com.cfl.util.FunctionConfig;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.NetworkUtils;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

import java.util.List;

public class SettingsActivity extends BaseActivity implements SettingsListener, ActivityPostExecuteListener{

    private TextView tvEmailValue, tvSettingsStartnotifications,
            tvSettingsMinimumdelay, tvSettingsGeneralsettingsLanguage, tvSettingsGeneralsettingsVersion,
            tvPersonaldetailDescription, tvPhoneValue, tvSettingsTravelremindersDescriptio;//tvPasswordValue,
    private SettingService settingService = null;
    private ICheckUpdateService checkUpdateService;
    private ProgressDialog progressDialog;
    private String pwd;
    private String selectedStartTime, selectedDelayTime;
    private SwitchCompat swAutoUpdate, swTR, swFT, sw3rd, swDnr, swOptions;
    private String selectedLanguageKey, currentLanguageKey, currentLanguage;
    private IPushService pushService;
    private List<CollectionItem> languages;
    private boolean isSucceededCallService;
    private DialogCheckUpdateNotification dialogCheckUpdateNotification;
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private LinearLayout llEmail, llPhone, llSettingsBottom;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
    private IMasterService masterService;
    private IClickToCallService clickToCallService;
    private IMessageService messageService;
    private Button btnDeletePersonal;

    private Handler checkUpdateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceConstant.MESSAGE_WHAT_OK:
                    hideWaitDialog();
                    try {
                        showCheckAppResponse();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ServiceConstant.MESSAGE_WHAT_ERROR:
                    hideWaitDialog();
                    Bundle bundle = msg.getData();
                    NetworkError error = (NetworkError) bundle
                            .getSerializable(ServiceConstant.PARAM_OUT_ERROR);

                    switch (error) {
                        case CONNECTION:
                            hideWaitDialog();
                            //showCheckAppResponse();
                            Toast.makeText(SettingsActivity.this,getString(R.string.setting_error_service_not_available), Toast.LENGTH_LONG).show();
                            break;
                        case TIMEOUT:
                            hideWaitDialog();
                            break;
                    }

                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        messageService = ((NMBSApplication) getApplication()).getMessageService();
        pushService = ((NMBSApplication) getApplication()).getPushService();
        checkUpdateService = ((NMBSApplication) getApplication()).getCheckUpdateService();
        //settingService.initLanguageSettings();

        masterService = ((NMBSApplication) getApplication()).getMasterService();
        clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
        setContentView(R.layout.activity_settings);


        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
        GoogleAnalyticsUtil.getInstance().sendScreen(SettingsActivity.this, TrackerConstant.SETTINGS);
    }

    private void bindAllViewsElement() {
        llEmail = (LinearLayout) findViewById(R.id.ll_email);
        llPhone = (LinearLayout) findViewById(R.id.ll_phone);

        tvEmailValue = (TextView) findViewById(R.id.tv_email_value);
        tvPhoneValue = (TextView) findViewById(R.id.tv_phone_value);
        swAutoUpdate = (SwitchCompat) findViewById(R.id.sw_auto_update);
        swTR = (SwitchCompat) findViewById(R.id.sw_tr);
        swDnr = (SwitchCompat) findViewById(R.id.sw_dnr);
        swOptions = (SwitchCompat) findViewById(R.id.sw_options);
        swFT = (SwitchCompat) findViewById(R.id.sw_ft);
        sw3rd = (SwitchCompat) findViewById(R.id.sw_3rd);
        tvSettingsStartnotifications = (TextView) findViewById(R.id.tv_settings_startnotifications);
        tvSettingsMinimumdelay = (TextView) findViewById(R.id.tv_settings_minimumdelay);
        tvSettingsGeneralsettingsLanguage = (TextView) findViewById(R.id.tv_settings_generalsettings_language);
        tvSettingsGeneralsettingsVersion = (TextView) findViewById(R.id.tv_settings_generalsettings_version);
        tvSettingsGeneralsettingsVersion.setText(SettingsPref.getSettingsVersion(getApplicationContext()));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
        tvPersonaldetailDescription = (TextView) findViewById(R.id.tv_personaldetail_description);
        btnDeletePersonal = (Button) findViewById(R.id.btn_delete_personal);
        tvSettingsTravelremindersDescriptio = (TextView) findViewById(R.id.tv_settings_travelreminders_descriptio);
        llSettingsBottom = (LinearLayout) findViewById(R.id.ll_settings_bottom);

        if(NMBSApplication.getInstance().getLoginService().isLogon()){
            tvSettingsTravelremindersDescriptio.setText(getResources().getString(R.string.settings_travelreminders_description));
        }else {
            tvSettingsTravelremindersDescriptio.setText(getResources().getString(R.string.settings_travelreminders_description_forlogout));
        }

        llSettingsUpdates = (LinearLayout) findViewById(R.id.ll_settings_updates);
        if(FunctionConfig.kFunCheckForUpdate){
            llSettingsUpdates.setVisibility(View.VISIBLE);
        }else {
            llSettingsUpdates.setVisibility(View.GONE);
        }

        llSettingsFacebook = (LinearLayout) findViewById(R.id.ll_settings_facebook);
        if(FunctionConfig.kFunFBIntegration){
            llSettingsFacebook.setVisibility(View.VISIBLE);
        }else {
            llSettingsFacebook.setVisibility(View.GONE);
        }

        llSyncBookings = (LinearLayout) findViewById(R.id.ll_sync_bookings);
        if(FunctionConfig.kFunMyProfile){
            llSyncBookings.setVisibility(View.VISIBLE);
        }else {
            llSyncBookings.setVisibility(View.GONE);
        }

        llSettingsOptions = (LinearLayout) findViewById(R.id.ll_settings_options);
        if(FunctionConfig.kFunMyProfile){
            llSettingsOptions.setVisibility(View.VISIBLE);
        }else {
            llSettingsOptions.setVisibility(View.GONE);
        }
        if(FunctionConfig.kFunAppRating){
            llSettingsBottom.setVisibility(View.VISIBLE);
        }else {
            llSettingsBottom.setVisibility(View.GONE);
        }


    }
    private LinearLayout llSettingsUpdates, llSettingsFacebook, llSyncBookings, llSettingsOptions;
    private void bindAllListeners() {
        swAutoUpdate.setOnCheckedChangeListener(autoUpdateCheckedChangeListener);
        swTR.setOnCheckedChangeListener(trCheckedChangeListener);
        swDnr.setOnCheckedChangeListener(dnrCheckedChangeListener);
        swOptions.setOnCheckedChangeListener(optionsCheckedChangeListener);

        swFT.setOnCheckedChangeListener(ftCheckedChangeListener);
        sw3rd.setOnCheckedChangeListener(sw3rdCheckedChangeListener);
    }
    private SwitchCompat.OnCheckedChangeListener autoUpdateCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            settingService.saveAutoUpdate(isChecked);
        }
    };

    private SwitchCompat.OnCheckedChangeListener trCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            /*IPushService pushService = NMBSApplication.getInstance().getPushService();
            if(isChecked){
                pushService.addAllLocalNotificationFromDatabase();
            }else{
                pushService.deleteAllLocalNotification();
            }*/
            settingService.saveTravelReminders(isChecked);
        }
    };

    private SwitchCompat.OnCheckedChangeListener dnrCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            /*IPushService pushService = NMBSApplication.getInstance().getPushService();
            if(isChecked){
                pushService.addAllLocalNotificationFromDatabase();
            }else{
                pushService.deleteAllLocalNotification();
            }*/
            LogUtils.e("dnr sw", "onCheckedChanged---->" + isChecked);
            settingService.saveDnr(isChecked);
            boolean is = settingService.isDnr();
            LogUtils.e("dnr sw", "onCheckedChanged----saved--->" + is);
        }
    };
    private SwitchCompat.OnCheckedChangeListener optionsCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            /*IPushService pushService = NMBSApplication.getInstance().getPushService();
            if(isChecked){
                pushService.addAllLocalNotificationFromDatabase();
            }else{
                pushService.deleteAllLocalNotification();
            }*/
            settingService.saveOptions(isChecked);
        }
    };
    private SwitchCompat.OnCheckedChangeListener ftCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            settingService.saveFacebookTrack(isChecked);
        }
    };
    private SwitchCompat.OnCheckedChangeListener sw3rdCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            settingService.save3rdTrack(isChecked);
        }
    };

    public void changePassword(View view) {
        DialogSettingsPassword dialog = new DialogSettingsPassword(this, this, pwd);
        dialog.show();
    }

    public void changeEmail(View view) {
        //DialogSettingsEmail dialog = new DialogSettingsEmail(this, this, tvEmailValue.getText().toString());
        //dialog.show();
    }

    public void showNotifi(View view) {
        DialogSettingsNotifi dialog = new DialogSettingsNotifi(this, this, selectedStartTime, selectedDelayTime);
        dialog.show();
    }

    public void feedback(View view){
        sendEmail();
    }

    public void rate(View view){
        openAppStore();
    }

    public void openAppStore() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
        startActivity(intent);	}

    public void sendEmail() {
        Utils.sendEmail(getApplicationContext());
    }

    public void changeLanguage(View view){
        showDialog(1);
    }

    public  void deletePersonalData(View view){

        tvEmailValue.setText("");
        tvPhoneValue.setText("");
        //tvPasswordValue.setText("");
        settingService.deletePersonalData();
        llEmail.setVisibility(View.GONE);
        NMBSApplication.getInstance().getClickToCallService().deletePhoneNumber();
        llPhone.setVisibility(View.GONE);
        NMBSApplication.getInstance().getLoginService().logOutFully();
        tvPersonaldetailDescription.setText(getResources().getString(R.string.settings_personaldetail_removed));
        btnDeletePersonal.setVisibility(View.GONE);

    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    public void setPassword(String password) {
        pwd = password;
        //setTvPassword(password);
    }

    @Override
    public void setEmail(String email) {
        setTvEmail(email);
    }

    @Override
    public void setStartNotifi(String time) {
        selectedStartTime = time;
        tvSettingsStartnotifications.setText(selectedStartTime);
    }

    @Override
    public void setDelayNotifi(String time) {
        selectedDelayTime = time;
        tvSettingsMinimumdelay.setText(selectedDelayTime);
    }


    private void setTvEmail(String email){
        tvEmailValue.setText(email);
    }

    private void setViewStateBasedOnValue(){
        String email = "";
        String phone = "";
        LogonInfo loginInfo = NMBSApplication.getInstance().getLoginService().getLogonInfo();
        if(loginInfo != null){
            email = loginInfo.getEmail();
        }
        setTvEmail(email);

        if(loginInfo != null){
            phone = loginInfo.getPhoneNumber();
        }
        //phone = NMBSApplication.getInstance().getClickToCallService().getPhoneNumber();
        tvPhoneValue.setText(phone);
        if(email == null || email.isEmpty()){
            llEmail.setVisibility(View.GONE);
        }else{
            llEmail.setVisibility(View.VISIBLE);
        }
        if(phone == null || phone.isEmpty()){
            llPhone.setVisibility(View.GONE);
        }else{
            llPhone.setVisibility(View.VISIBLE);
        }
        if(loginInfo == null && email.isEmpty() && phone.isEmpty()){
            tvPersonaldetailDescription.setText(getResources().getString(R.string.settings_personaldetail_removed));
            btnDeletePersonal.setVisibility(View.GONE);
        }else {
            tvPersonaldetailDescription.setText(getResources().getString(R.string.settings_personaldetail_description));
            btnDeletePersonal.setVisibility(View.VISIBLE);
        }
        boolean isChecked = settingService.isAutoUpdate();

        if(FunctionConfig.kFunCheckForUpdate){
            swAutoUpdate.setChecked(isChecked);
        }else {
            swAutoUpdate.setChecked(false);
        }

        isChecked = settingService.isDnr();
        swDnr.setChecked(isChecked);
        isChecked = settingService.isOptions();
        swOptions.setChecked(isChecked);
        isChecked = settingService.isTravelReminders();
        swTR.setChecked(isChecked);

        isChecked = settingService.isFacebookTrack();

        if(FunctionConfig.kFunFBIntegration){
            swFT.setChecked(isChecked);
        }else {
            swFT.setChecked(false);
        }

        //setTvPassword(pwd);
        isChecked = settingService.is3rdTrack();
        sw3rd.setChecked(isChecked);
        selectedStartTime = settingService.getStartNotifiTime();
        selectedDelayTime = settingService.getDelayNotifiTime();
        if (selectedStartTime.isEmpty()){
            String[] time = getResources().getStringArray(R.array.settings_time);
            tvSettingsStartnotifications.setText(time[0]);
        }else{
            tvSettingsStartnotifications.setText(selectedStartTime);
        }
        if (selectedDelayTime.isEmpty()){
            String[] time = getResources().getStringArray(R.array.settings_time);
            tvSettingsMinimumdelay.setText(time[0]);
        }else{
            tvSettingsMinimumdelay.setText(selectedDelayTime);
        }
        languages = settingService.readLanguages();
        currentLanguageKey = settingService.getCurrentLanguagesKey();
        currentLanguage = settingService.getCurrentLanguage(currentLanguageKey, languages);
        tvSettingsGeneralsettingsLanguage.setText(currentLanguage);
    }

    public void checkUpdateVersion(View view){
        showWaitDialog();
        checkUpdateService.setCheckAppManually(true);
        CheckUpdateAsyncTask checkUpdateAsyncTask = new CheckUpdateAsyncTask(checkUpdateHandler,checkUpdateService);
        checkUpdateAsyncTask.execute(settingService);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

        builder.setTitle(getString(R.string.settings_generalsettings_language));
        switch (id) {
            case 1:
                CollectionItemAdapter collectionItemAdapter = new CollectionItemAdapter(
                        getApplicationContext(),
                        R.layout.alert_dialog_data_adapter, languages);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, final int which) {
                        selectedLanguageKey = languages.get(which).getKey();
                        if (!selectedLanguageKey.equalsIgnoreCase(currentLanguageKey)){
                            //Log.d(TAG, "The language has been changed.");
                            if(NMBSApplication.getInstance().getDossierDetailsService().getDossiers() != null
                                    && NMBSApplication.getInstance().getDossierDetailsService().getDossiers().size() > 0){
                                //Log.d(TAG, "Show DialogChangeLanguageSuccessful:::");

                                DialogChangeLanguageSuccessful dialog = new DialogChangeLanguageSuccessful(SettingsActivity.this);
                                dialog.show();
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                        String beforeLanguage = selectedLanguageKey;
                                        ProgressDialogCustom.getInstance().showWaitDialog(SettingsActivity.this);
                                        try{
                                            final HafasUser hafasUser = pushService.getUser();
                                            final int delayTime = settingService.getDelayNotifiTimeIntger();
                                            final int startTime = settingService.getStartNotifiTimeIntger();
                                            if(hafasUser!=null){
                                                new Thread(){
                                                    public void run() {
                                                        try {
                                                            pushService.updateAccount(new HafasUser(hafasUser.getUserId(),pushService.getRegistrationId(),selectedLanguageKey,
                                                                    delayTime, delayTime, startTime));
                                                        } catch (RequestFail requestFail) {
                                                            requestFail.printStackTrace();
                                                        }
                                                    }
                                                }.start();

                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        settingService.cleanLastModifiedTime();
                                        settingService.setCurrentLanguagesKey(selectedLanguageKey);
                                        settingService.changeLanguageData(SettingsActivity.this, messageService, beforeLanguage);
                                        // The language has been changed.
                                        currentLanguage = languages.get(which).getLable();
                                    }
                                });
                            }else{
                                changeLanguage(which);
                            }
                        }
                    }
                };
                builder.setAdapter(collectionItemAdapter, listener);
                dialog = builder.create();
                break;
        }

        return dialog;
    }

    private void changeLanguage(int which){
        ProgressDialogCustom.getInstance().showWaitDialog(SettingsActivity.this);
        settingService.cleanLastModifiedTime();
        String beforeLanguage = selectedLanguageKey;
        settingService.setCurrentLanguagesKey(selectedLanguageKey);
        settingService.changeLanguageData(SettingsActivity.this, messageService, beforeLanguage);
        // The language has been changed.
        currentLanguage = languages.get(which).getLable();
    }

    private void changeLanguageSucceeded(){
        ProgressDialogCustom.getInstance().hideWaitDialog(this);
        finish();
        settingService.setCurrentLanguagesKey(selectedLanguageKey);
        AppLanguageUtils.changeAppLanguage(getApplicationContext(), selectedLanguageKey);
        // Log.d(TAG, "The currentLanguage:::" + currentLanguage);
        //Log.d(TAG, "The selectedLanguageKey:::" + selectedLanguageKey);
        //Log.d(TAG, "The Dossiers size:::" + NMBSApplication.getInstance().getDossierDetailsService().getDossiers().size());

        relaunchActivity();
        //tvSettingsGeneralsettingsLanguage.setText("The currentLanguage:::");

    }

    private void relaunchActivity(){
        //Log.e("restartActivity", "restartActivity......");
        // after save the select language , should start activity again, (otherwise tab-host language will not be changed)

        Intent myIntent = new Intent(SettingsActivity.this.getApplicationContext(), MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);
        Intent intent = new Intent(SettingsActivity.this.getApplicationContext(), SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void readPolicy(View view){
        String currentLanguage = settingService.getCurrentLanguagesKey();
        String actionUrl = "";
        GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
        if(generalSetting != null && generalSetting.getPrivacyPolicyUrl() != null && !generalSetting.getPrivacyPolicyUrl().isEmpty()){
            actionUrl = Utils.getUrl(generalSetting.getPrivacyPolicyUrl());

        }else {
            if("NL_BE".equals(currentLanguage.toUpperCase())){
                actionUrl = getString(R.string.url_privacy_policy_nl);
            }else if("DE_BE".equals(currentLanguage.toUpperCase())){
                actionUrl = getString(R.string.url_privacy_policy_de);
            }else if("FR_BE".equals(currentLanguage.toUpperCase())){
                actionUrl = getString(R.string.url_privacy_policy_fr);
            }else{
                actionUrl = getString(R.string.url_privacy_policy_en);
            }
            if(actionUrl.contains("?")){
                actionUrl += "&blockSmartAppBanner=true" + "&app=Android";
            }else{
                actionUrl += "?blockSmartAppBanner=true" + "&app=Android";
            }
        }
        if(NetworkUtils.isOnline(SettingsActivity.this)) {
            startActivity(WebViewOverlayActivity.createIntent(getApplicationContext(), actionUrl));
        }
        /*if("NL_BE".equals(currentLanguage.toUpperCase())){
            actionUrl = getString(R.string.url_privacy_policy_nl);
        }else if("DE_BE".equals(currentLanguage.toUpperCase())){
            actionUrl = getString(R.string.url_privacy_policy_de);
        }else if("FR_BE".equals(currentLanguage.toUpperCase())){
            actionUrl = getString(R.string.url_privacy_policy_fr);
        }else{
            actionUrl = getString(R.string.url_privacy_policy_en);
        }
        if(actionUrl.contains("?")){
            actionUrl += "&blockSmartAppBanner=true" + "&app=Android";
        }else{
            actionUrl += "?blockSmartAppBanner=true" + "&app=Android";
        }*/


        /*Uri uri = Uri.parse(actionUrl);
        Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);*/
    }

    @Override
    public void onPostExecute(boolean isSucceeded, String message) {
        isSucceededCallService = isSucceeded;
        //Log.e(TAG, "The language fiished!!!The status is succeeded??:::" + isSucceeded);
        changeLanguageSucceeded();
       /* if (isSucceededCallService){
            changeLanguageSucceeded();
        }else{
            ProgressDialogCustom.getInstance().hideWaitDialog(this);
        }*/
    }

    private void showWaitDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (progressDialog == null) {
                    //Log.e(TAG, "Show Wait Dialog....");
                    progressDialog = ProgressDialog.show(SettingsActivity.this, null,
                            getString(R.string.message_view_loading), true);
                    progressDialog.setCanceledOnTouchOutside(false);

                }
            }
        });
    }

    // hide progressDialog
    private void hideWaitDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                //Log.e(TAG, "Hide Wait Dialog....");
                if (progressDialog != null) {
                    //Log.e(TAG, "progressDialog is.... " + progressDialog);
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });

    }


    public void showCheckAppResponse() {
        final CheckAppUpdate checkAppUpdate = checkUpdateService
                .getCheckAppUpdate();
        if(checkAppUpdate.isUpToDate()){
            Toast.makeText(SettingsActivity.this, getString(R.string.checkappversion_alert_no_update), Toast.LENGTH_LONG).show();
        }else{
            dialogCheckUpdateNotification = new DialogCheckUpdateNotification(SettingsActivity.this,SettingsActivity.this,checkUpdateService,checkAppUpdate);
            dialogCheckUpdateNotification.setCanceledOnTouchOutside(false);
            dialogCheckUpdateNotification.show();
        }
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
                                startActivity(com.cfl.activities.MessageActivity.createIntent(SettingsActivity.this, messageService.getMessageResponse()));
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
                                startActivity(com.cfl.activities.StationBoardActivity.createIntent(SettingsActivity.this, null));
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
                                startActivity(com.cfl.activities.StationInfoActivity.createIntent(SettingsActivity.this));
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
                                startActivity(com.cfl.activities.AlertActivity.createIntent(SettingsActivity.this));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }

    public void bookTicktes(View view){
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_BOOK_YOUR_TICKET,TrackerConstant.HOME_BOOK_YOUR_TICKET_LABEL);
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();

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
                                    if(NetworkUtils.isOnline(SettingsActivity.this)) {
                                        GoogleAnalyticsUtil.getInstance().sendScreen(SettingsActivity.this, TrackerConstant.BOOKING);
                                        startActivity(WebViewActivity.createIntent(SettingsActivity.this,
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
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();

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
                                    if(NetworkUtils.isOnline(SettingsActivity.this)) {
                                        GoogleAnalyticsUtil.getInstance().sendScreen(SettingsActivity.this, TrackerConstant.LLF);
                                        startActivity(WebViewActivity.createIntent(SettingsActivity.this,
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
                                startActivity(ScheduleSearchActivity.createIntent(SettingsActivity.this));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }

    }
    private boolean isGoto;
    public void settings(View view){
        mDrawerLayout.closeDrawer(GravityCompat.END);
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
                                startActivity(WizardActivity.createIntent(SettingsActivity.this, WizardActivity.Wizard_Home));
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
                                startActivity(MyTicketsActivity.createIntent(SettingsActivity.this));
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
                                startActivity(UploadDossierActivity.createUploadDossierIntent(SettingsActivity.this, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
                                isGoto = false;
                                finish();
                            }
                        }
                    });
        }
    }
    public void myOption(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();
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
                                    startActivity(WebViewActivity.createIntent(getApplicationContext(),
                                            Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
                                }else{
                                    DialogMyOptions dialogMyOptions = new DialogMyOptions(SettingsActivity.this);
                                    dialogMyOptions.show();
                                }
                                isGoto = false;
                            }
                        }
                    });
        }else{
            if(NMBSApplication.getInstance().getLoginService().isLogon()){
                startActivity(WebViewActivity.createIntent(getApplicationContext(),
                        Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
            }else{
                DialogMyOptions dialogMyOptions = new DialogMyOptions(this);
                dialogMyOptions.show();
            }
        }


    }

    public void loginOrManage(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();
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
        if(!NMBSApplication.getInstance().getLoginService().isLogon()){
            rlMenuMyOption.setAlpha(0.3f);
            tvMenuOptionCount.setText("");
            tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
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
        RelativeLayout rlMenuBook = findViewById(R.id.rl_menu_traintickets_content_bookticktes);

        if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
                && NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getBookingUrl() != null
                && !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getBookingUrl().isEmpty()){
            rlMenuBook.setVisibility(View.VISIBLE);
        }else {
            rlMenuBook.setVisibility(View.GONE);
        }
        RelativeLayout rlMenuLowestfares = findViewById(R.id.rl_menu_traintickets_content_lowestfares);
        RelativeLayout rlMenuLogin = findViewById(R.id.rl_menu_traintickets_content_login);

        if(FunctionConfig.kFunLFFEntry){
            rlMenuLowestfares.setVisibility(View.VISIBLE);
        }else {
            rlMenuLowestfares.setVisibility(View.GONE);
        }

        if(FunctionConfig.kFunMyProfile){
            rlMenuMyOption.setVisibility(View.VISIBLE);
            rlMenuLogin.setVisibility(View.VISIBLE);
        }else {
            rlMenuMyOption.setVisibility(View.GONE);
            rlMenuLogin.setVisibility(View.GONE);
        }
        mDrawerLayout.openDrawer(GravityCompat.END);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            if(drawerOpen){
                mDrawerLayout.closeDrawer(GravityCompat.END);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
    }
    public void close(View view){
        finish();
    }
}
