package com.nmbs.activities.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.MyTicketsActivity;

import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierSummary;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.PushService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.TrackerConstant;

public class DialogSubNotification extends Dialog {


    private RelativeLayout btnOk;

    private boolean isInProgress;
    private TextView tvNotNow, tvEnable;
    private TextView createSubscriptionErrorView, tvSubscriptionDescribe;
    private ProgressBar progressBar;
    private Dossier dossier;
    private DossierSummary dossierSummary;
    private LinearLayout llNotNow;
    private ImageView ivClose;
    private Activity activity;
    private DossierDetailsService dossierDetailsService;
    //private Button btnEnable;
    private boolean isEnable, isUpload;
    private LinearLayout llError;
    private ISettingService settingService;
    private ServiceDeleteSubscriptionReceiver serviceDeleteSubscriptionReceiver;
    private final static String TAG = DialogSubNotification.class.getSimpleName();
    private ReloadCallback reloadCallback;
    private int pageFlag = -1;
    public DialogSubNotification(Activity activity, Dossier dossier, boolean isUpload, int pageFlag) {
        super(activity, R.style.Dialogheme);
        this.dossier = dossier;
        this.activity = activity;
        this.isUpload = isUpload;
        this.pageFlag = pageFlag;
    }

    /*public void setPageFlag(int pageFlag){
        this.pageFlag = pageFlag;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_sub_notification);
        dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
        settingService = NMBSApplication.getInstance().getSettingService();
        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        btnOk = (RelativeLayout) findViewById(R.id.ll_enable);
        /*btnCancel = (Button) findViewById(R.id.btn_cancel);
        edEmail = (EditText) findViewById(R.id.et_settings_email);
        tvEmailError = (TextView) findViewById(R.id.tv_email_error);*/
        tvNotNow = (TextView) findViewById(R.id.tv_notnow_title);
        tvNotNow.setText(activity.getResources().getString(R.string.general_cancel).toUpperCase());
        //tvNotNow.setText(activity.getApplicationContext().getResources().getString(R.string.general_notnow).toUpperCase());
        llNotNow = (LinearLayout) findViewById(R.id.ll_notnow);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        createSubscriptionErrorView = (TextView) findViewById(R.id.tv_create_subscription_error_describe);
        progressBar = (ProgressBar) findViewById(R.id.pgb_enable_bar);
        //btnEnable = (Button) findViewById(R.id.btn_enable);
        llError = (LinearLayout) findViewById(R.id.ll_error);
        tvEnable = (TextView) findViewById(R.id.tv_enable);
        tvSubscriptionDescribe = (TextView) findViewById(R.id.tv_create_subscription_describe);

    }

    private void executeSubscription(){
        //btnOk.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ivClose.setClickable(false);
        btnOk.setClickable(false);
        setCancelable(false);
    }
    private void finishedSubscription(){

        //btnOk.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ivClose.setClickable(true);
        btnOk.setClickable(true);
        setCancelable(true);
        if(serviceDeleteSubscriptionReceiver != null){
            activity.unregisterReceiver(serviceDeleteSubscriptionReceiver);
            serviceDeleteSubscriptionReceiver = null;
        }
    }

    private void deleteSubscriptionReceiver(){
        //Log.d(TAG, "Delete Subscription Receiver...");
        if (serviceDeleteSubscriptionReceiver == null) {
            serviceDeleteSubscriptionReceiver = new ServiceDeleteSubscriptionReceiver();
            activity.registerReceiver(serviceDeleteSubscriptionReceiver, new IntentFilter(ServiceConstant.PUSH_DELETE_SUBSCRIPTION_ACTION));
        }
    }

    private void TrackNotfication(String action){
        if(pageFlag == NMBSApplication.PAGE_UploadTicket){
            GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, action, "");
        }else if(pageFlag == NMBSApplication.PAGE_UploadTicket_DNR){
            GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY, action, "");
        }else if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
            GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.DOSSIER_CATEGORY, action, "");
        }
    }

    private void bindAllListeners(){
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llError.setVisibility(View.GONE);
                if (dossier != null) {
                    isInProgress = true;
                    executeSubscription();
                    LogUtils.d(TAG, "isEnable..." + isEnable);
                    if(isEnable){
                        if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                            GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_SELECT_DISABLE_NOTIFICATION, "");
                        }else {
                            TrackNotfication(TrackerConstant.UPLOAD_TICKET_SELECT_ENABLE_NOTIFICATION);
                        }

                        deleteSubscriptionReceiver();
                        dossierDetailsService.disableSubscription(dossier, dossierSummary, true);
                    }else{
                        if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                            GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_SELECT_NOTIFICATION, "");
                        }else {
                            TrackNotfication(TrackerConstant.UPLOAD_TICKET_SELECT_ENABLE_NOTIFICATION);
                        }

                        dossierDetailsService.enableSubscription(dossier, handler,settingService.getCurrentLanguagesKey());
                    }
                }
            }
        });
        llNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackNotfication(TrackerConstant.UPLOAD_TICKET_SELECT_NO_NOTIFICATION);
                finish();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void finish(){
        dismiss();
        if(isUpload){
            activity.startActivity(MyTicketsActivity.createIntent(activity));
            activity.finish();
        }

    }

    private void setViewStateBasedOnValue(){
        dossierSummary = dossierDetailsService.getDossier(dossier.getDossierId());
        if (dossierSummary != null && dossier != null){
            isEnable = dossierSummary.isDossierPushEnabled();
            LogUtils.d(TAG, "dossierSummary.isDossierPushEnabled().........." + dossierSummary.isDossierPushEnabled());

            if(dossierSummary.isDossierPushEnabled()){
                tvEnable.setText(activity.getResources().getString(R.string.general_disable_notifications));
                tvSubscriptionDescribe.setText(activity.getResources().getString(R.string.general_unsubscribe_intro));

            }else{
                tvEnable.setText(activity.getResources().getString(R.string.general_enable_notifications));
                tvSubscriptionDescribe.setText(activity.getResources().getString(R.string.general_subscribe_intro));
            }
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            //Log.e(TAG, "handleMessage...");
            //Log.e(TAG, "msg.what..." + msg.what);
            DossierSummary dossierSummary = dossierDetailsService.getDossier(dossier.getDossierId());
            isInProgress = false;
            finishedSubscription();

            //Log.e(TAG, "msg.what..." + msg.what);
            switch (msg.what) {
                case 0:
                    if(dossierSummary != null){
                        if(NMBSApplication.getInstance().getTestService().isCreateSubscription()){
                            dossierSummary.setDossierPushEnabled(true);
                            //Log.e(TAG, "Test mode...Not trigger create subscriptions call.." + msg.what);
                            //Log.e(TAG, "Test mode...Not trigger create subscriptions call..setDossierPushEnabled...true");
                        }
                        dossierSummary.setDossierPushEnabled(false);
                        dossierDetailsService.updateDossier(dossierSummary);
                        dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                    }
                    if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_NOTIFICATION, "");
                    }else {
                        TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
                    }

                    llError.setVisibility(View.VISIBLE);
                    createSubscriptionErrorView.setText(activity.getString(R.string.alert_subscription_create_failure));
                    //dismiss();
                    break;
                case 1:
                    if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_NOTIFICATION, "");
                    }else {
                        TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
                    }
                    if (dossierSummary != null){
                        dossierSummary.setDossierPushEnabled(true);
                        dossierDetailsService.updateDossier(dossierSummary);
                        dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                        //Log.d("Connection", "status..isPushEnabled..." + dossierDetailsService.isPushEnabled(dossier));
                    }
                    progressBar.setVisibility(View.GONE);
                    llError.setVisibility(View.VISIBLE);
                    createSubscriptionErrorView.setText(activity.getString(R.string.alert_subscription_not_all_created));
                    //finish();
                    break;
                case 2:
                    if(dossierSummary != null){
                        dossierSummary.setDossierPushEnabled(true);
                        dossierDetailsService.updateDossier(dossierSummary);
                        dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                        //Log.d("Connection", "status..isPushEnabled..." + dossierDetailsService.isPushEnabled(dossier));
                    }
                    progressBar.setVisibility(View.GONE);
                    //llError.setVisibility(View.VISIBLE);
                    //createSubscriptionErrorView.setText(activity.getString(R.string.alert_subscription_failed));
                    //finish();
                    //if(UploadDossierActivity.ContextMyTickets != pageFlag){
                    if(isUpload){
                        activity.startActivity(MyTicketsActivity.createIntent(activity));
                        activity.finish();

                    }
                    dismiss();
                    //}
                    break;
                case 3:
                    progressBar.setVisibility(View.GONE);
                    //createSubscriptionErrorView.setText(activity.getString(R.string.upload_tickets_subscribe_title));
                    finish();
                    break;
                case PushService.USER_ERROR:
                    progressBar.setVisibility(View.GONE);
                    //Log.e(TAG, "handler.PushService.USER_ERROR()...");
                    llError.setVisibility(View.VISIBLE);
                    createSubscriptionErrorView.setText(activity.getResources().getString(R.string.alert_subscription_missingID));
                    if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_NOTIFICATION, "");
                    }else {
                        TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
                    }
                    break;
            }
            if (reloadCallback != null) {
                reloadCallback.reloadData();
            }
        };
    };


    class ServiceDeleteSubscriptionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "DeleteSubscriptionReceiver...onReceive...");
            int successFlag = intent.getIntExtra(ActivityConstant.DELETE_SUBSCRIPTION_RESULT, 0);
            //Log.d(TAG, "DeleteSubscriptionReceiver...onReceive...successFlag..." + successFlag);
            if(successFlag == 1 || successFlag == 2){
                if(successFlag == 1){
                    createSubscriptionErrorView.setText(activity.getString(R.string.alert_subscription_not_all_deleted));
                    if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_DISABLE_NOTIFICATION, "");
                    }else {
                        TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
                    }
                }else {
                    if(isUpload){
                        activity.startActivity(MyTicketsActivity.createIntent(activity));
                        activity.finish();

                    }
                    dismiss();
                }
                if (dossierSummary != null){
                    dossierSummary.setDossierPushEnabled(false);
                    dossierDetailsService.updateDossier(dossierSummary);
                    dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                }
                //dismiss();
            }
            if(successFlag == 0){
                if(NMBSApplication.getInstance().getTestService().isDeleteSubscription()){
                    dossierSummary.setDossierPushEnabled(false);
                }
                llError.setVisibility(View.VISIBLE);
                if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
                    GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_DISABLE_NOTIFICATION, "");
                }else {
                    TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
                }

                createSubscriptionErrorView.setText(activity.getString(R.string.alert_subscription_delete_failure));
            }
            if(dossierDetailsService.isLinkedDnr(dossier)){
                if(isUpload){
                    if(pageFlag == 0){
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION, "");
                    }else{
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY, TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION, "");
                    }
                }else {
                    if(pageFlag == 0){
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY,TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION,"");
                    }else{
                        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY,TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION,"");
                    }
                }
                llError.setVisibility(View.VISIBLE);
                createSubscriptionErrorView.setText(activity.getString(R.string.upload_tickets_subscribe_title));
            }

            finishedSubscription();
            if (reloadCallback != null) {
                reloadCallback.reloadData();
            }
        }
    }
    public void setReloadCallback(ReloadCallback reloadCallback){

        this.reloadCallback = reloadCallback;
    }
    public interface ReloadCallback {
        public void reloadData();
    }
}



