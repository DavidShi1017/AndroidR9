package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activity.BaseActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.ScheduleRefreshAsyncTask;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.ScheduleDetailRefreshModel;
import com.nmbs.model.Subscription;
import com.nmbs.services.IPushService;
import com.nmbs.services.IScheduleService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.DateUtils;
import com.nmbs.util.Utils;

import java.util.Date;

/**
 * Created by Richard on 5/16/16.
 */
public class PushNotificationErrorActivity extends BaseActivity{
    private SettingService settingService;
    private IScheduleService scheduleService;
    private IPushService pushService;
    private TextView stationView, tvTitle;
    private TextView errorInfo;
    private Button retryBtn;
    private ProgressDialog progressDialog;
    private String subscriptionId;
    private Subscription subscription;
    private boolean isDnrRelated = false;
    private ScheduleDetailServiceStateReceiver scheduleDetailServiceStateReceiver;
    private final static String Intent_Key_Page = "Page";
    private int page;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        scheduleService = ((NMBSApplication) getApplication()).getScheduleService();
        pushService = ((NMBSApplication) getApplication()).getPushService();
        setContentView(R.layout.activity_push_notification_error);
        bindAllViewElement();
        registerReceiver();
        getIntentValue(getIntent());
        if (page == NMBSApplication.PAGE_PUSH){
            tvTitle.setText(getResources().getString(R.string.push_title));

        }else if(page == NMBSApplication.PAGE_ALERT){
            tvTitle.setText(getResources().getString(R.string.general_realtime_alerts));

        }else{
            tvTitle.setText(getResources().getString(R.string.schedule_title));

        }
    }

    public void back(View view){
        finish();
    }

    private void initData(RealTimeConnection realTimeConnection){
        startActivity(ScheduleResultDetailActivity.createPushIntent(this, subscriptionId, realTimeConnection, isDnrRelated, page));
        finish();
    }

    private void displayError(){
        this.stationView.setVisibility(View.VISIBLE);
        this.stationView.setText(subscription.getOriginStationName()+" - "+subscription.getDestinationStationName());
        this.errorInfo.setText(getString(R.string.push_fails));
        this.retryBtn.setVisibility(View.VISIBLE);
    }

    public void retry(View view){
        if(!subscription.getReconCtx().isEmpty()&&!subscription.getDeparture().isEmpty()){
            Date tempDate = DateUtils.stringToDateTime(subscription.getDeparture());
            refreshDetailData(subscription.getReconCtx(),tempDate);
        }
    }

    private void refreshDetailData(String connectionId,Date departure){
        ScheduleDetailRefreshModel scheduleDetailRefreshModel = new ScheduleDetailRefreshModel(connectionId,departure);
        ScheduleRefreshAsyncTask scheduleRefreshAsyncTask = new ScheduleRefreshAsyncTask(scheduleService,settingService,this);
        scheduleRefreshAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, scheduleDetailRefreshModel);
        showWaitDialog();
    }

    private void bindAllViewElement(){
        this.stationView = (TextView) findViewById(R.id.tv_push_notification_error_station_name);
        this.errorInfo = (TextView) findViewById(R.id.tv_push_notification_error_info);
        this.tvTitle = (TextView) findViewById(R.id.tv_title);
        this.retryBtn = (Button) findViewById(R.id.btn_push_notification_retry);
    }

    public void getIntentValue(Intent intent){
        subscriptionId = intent.getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID);
        page = intent.getIntExtra(Intent_Key_Page, 0);
        //Log.e("Push", "PushNotificationErrorActivity subscriptionId is..." + subscriptionId);
        System.out.println("!!!!!!!!!!!!"+subscriptionId);
        if(subscriptionId != null&&!"".equals(subscriptionId)){
            subscription = pushService.getSubscriptionById(subscriptionId);
            if(subscription != null){
                if(!"".equals(subscription.getDnr())){
                    isDnrRelated = true;
                }
                this.retryBtn.setVisibility(View.GONE);
                this.errorInfo.setVisibility(View.GONE);
                this.stationView.setVisibility(View.GONE);
                if(!subscription.getReconCtx().isEmpty()&&!subscription.getDeparture().isEmpty()){
                    Date tempDate = DateUtils.stringToDateTime(subscription.getDeparture());
                    refreshDetailData(subscription.getReconCtx(),tempDate);
                }
            }else{
                this.retryBtn.setVisibility(View.GONE);
                this.errorInfo.setText(getString(R.string.push_header));
                this.stationView.setVisibility(View.GONE);
            }
        }else{
            this.stationView.setVisibility(View.GONE);
            this.retryBtn.setVisibility(View.GONE);
            this.errorInfo.setText(getString(R.string.push_header));
            this.stationView.setVisibility(View.GONE);
        }
    }

    public void clickMenu(View view){
        startActivity(SettingsActivity.createIntent(this));
    }

    private void showWaitDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (progressDialog == null) {
                    //Log.e(TAG, "Show Wait Dialog....");
                    progressDialog = ProgressDialog.show(PushNotificationErrorActivity.this, null,
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

    private void registerReceiver(){
        if (scheduleDetailServiceStateReceiver == null) {
            scheduleDetailServiceStateReceiver = new ScheduleDetailServiceStateReceiver();
            registerReceiver(scheduleDetailServiceStateReceiver, new IntentFilter(ServiceConstant.SCHEDULE_DETAIL_SERVICE_ACTION));
        }
    }

    public static Intent createIntent(Context context, String subscriptionId, int page){
        Intent intent = new Intent(context, PushNotificationErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID, subscriptionId);
        intent.putExtra(Intent_Key_Page, page);
        return intent;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class ScheduleDetailServiceStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ServiceConstant.SCHEDULE_DETAIL_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
                hideWaitDialog();
                RealTimeConnection realTimeConnection = (RealTimeConnection)intent.getSerializableExtra(ActivityConstant.SCHEDULE_QUERY_REFRESH);
                if(realTimeConnection != null){
                    initData(realTimeConnection);
                }else{
                    displayError();
                }
            }
        }
    }
}
