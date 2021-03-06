package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogError;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.MyTicketsDnrReferenceAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AutoRetrievalDossiersTask;
import com.nmbs.async.MigrateDossierAsyncTask;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.async.RealTimeInfoAsyncTask;
import com.nmbs.async.RefreshDossierAsyncTask;
import com.nmbs.async.UploadDossierAsyncTask;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.listeners.DeleteDossierListener;
import com.nmbs.log.LogUtils;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierDetailParameter;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Order;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.impl.AsyncDossierAfterSaleResponse;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;

import com.nmbs.util.FunctionConfig;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.MenuUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 2/25/16.
 */
public class MyTicketsActivity extends BaseActivity implements MyTicketsDnrReferenceAdapter.RefreshCallback {
    private LinearLayout dnrList, llTicketRefresh, llTicketMigrating, llAotoRetrieval;
    private static final String Intent_Key_dosssier = "Dossier";
    private MyTicketsDnrReferenceAdapter myTicketsDnrReferenceAdapter;
    private List<DossierSummary> dossiersActive;
    private List<DossierSummary> dossiersInActive;
    private List<DossierSummary> dossiers;
    private final static String TAG = UploadDossierActivity.class.getSimpleName();
    private DossierDetailsService dossierDetailsService;
    private boolean isActive = true;
    private TextView tvActive, tvInActive, tvNoDnr;
    private SettingService settingService;
    private List<Order> listOrders;
    private MyState myState;
    private ProgressDialog progressDialog;
    private Dossier dossier;
    private DossierSummary dossierSummary;
    private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
    private MigrateDossierReceiver migrateDossierReceiver;
    private UploadDossierReceiver uploadDossierReceiver;
    private IMasterService masterService;
    private IClickToCallService clickToCallService;
    private IMessageService messageService;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
    private RefreshDossierReceiver refreshDossierReceiver;
    private RefreshMultipleDossierReceiver refreshMultipleDossierReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        messageService = ((NMBSApplication) getApplication()).getMessageService();
        masterService = ((NMBSApplication) getApplication()).getMasterService();
        clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
        setContentView(R.layout.activity_my_tickets_view);

        dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
        realTimeInfoRequestParameter = dossierDetailsService.getRealTimeInfoRequestParameter();

        getIntentValues();
        bindAllViewElements();

        bindAllListeners();
        //initData();
/*        DecryptUtils.getInstance(this).cleanRoomStart();
        String firstResult = DecryptUtils.getInstance(this).retrieveData("unguessable");
        Log.e("encryptedData", "firstResult------>"+ firstResult);
        //Log.e("firstResult", "firstResult------>" + firstResult);*/
        GoogleAnalyticsUtil.getInstance().sendScreen(MyTicketsActivity.this, TrackerConstant.DOSSIER_LIST);
    }

    private void getDossiers(){
        dossiersActive = dossierDetailsService.getDossiers(true);
        dossiersInActive = dossierDetailsService.getDossiers(false);

    }

    private void getIntentValues(){

    }

    private void initView(){
        this.dnrList = (LinearLayout)findViewById(R.id.ll_my_tickets_dnr_list);
        tvActive = (TextView) findViewById(R.id.tv_active);
        tvInActive = (TextView) findViewById(R.id.tv_inactive);
        tvNoDnr = (TextView) findViewById(R.id.tv_no_dnr);
        llTicketRefresh = (LinearLayout) findViewById(R.id.ll_my_tickets_refresh_and_view_layout);
        llTicketMigrating = (LinearLayout) findViewById(R.id.ll_mytickey_migrating);
        llAotoRetrieval = (LinearLayout) findViewById(R.id.ll_mytickey_auto_retrieval);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
    }

    private void bindAllViewElements(){
        initView();
        if (AutoRetrievalDossiersTask.isWorking){
            llAotoRetrieval.setVisibility(View.VISIBLE);
        }else{
            llAotoRetrieval.setVisibility(View.GONE);
        }

    }

    private void bindAllListeners(){

    }

    public void upLoad(View view){
        startActivity(UploadDossierActivity.createUploadDossierIntent(this, NMBSApplication.PAGE_UploadTicket_DNR, null, null, null, null));
    }

    public void viewOldDnr(View view){
        startActivity(com.nmbs.activity.MyTicketsActivity.createIntent(this));
    }

    public void initData(int i){
        //MigrateDossier();
        getDossiers();
        LogUtils.e(TAG, "initData...isRefreshing....." + i);

        this.dnrList.removeAllViews();
        if(isActive){
            dossiers = dossiersActive;
            tvNoDnr.setText(getResources().getString(R.string.my_tickets_no_active_ticket));
        }else {
            dossiers = dossiersInActive;
            tvNoDnr.setText(getResources().getString(R.string.my_tickets_no_inactive_ticket));
            /*Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.DESC);
            Collections.sort(dossiers, comp);*/
        }
        if(dossiers != null && dossiers.size() > 0){
            tvNoDnr.setVisibility(View.GONE);
            myTicketsDnrReferenceAdapter = new MyTicketsDnrReferenceAdapter(this, new DeleteDossierListener() {
                @Override
                public void deleted() {
                    initData(2);
                }
            });
            for(DossierSummary dossierSummary: dossiers){
                //Log.d("Dossiers", "Dossier id is:::" + dossierSummary.getDossierId() + "::: Dossier EarliestTravel is:::" + dossierSummary.getEarliestTravel());
                if(dossierSummary != null){
                    DossierDetailsResponse dossierResponse = dossierDetailsService.getDossierDetail(dossierSummary);
                    if(dossierResponse != null){
                        myTicketsDnrReferenceAdapter.setRefreshCallback(this);
                        //dossierResponse.getDossier().setDossierState(DossierDetailsService.DossierStateError);
                        myTicketsDnrReferenceAdapter.getMyTicketsDnrReferenceView(dossierResponse.getDossier(), this.dnrList, dossierSummary, isActive);
                    }
                }
            }
        }else {
            tvNoDnr.setVisibility(View.VISIBLE);
        }
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MyTicketsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public void refresh(View view){
        MigrateDossierAsyncTask asyncTask = new MigrateDossierAsyncTask(migrateHandler, settingService.getCurrentLanguagesKey(), getApplicationContext(), listOrders);
        asyncTask.execute((Void) null);
        llTicketMigrating.setVisibility(View.VISIBLE);
        llTicketRefresh.setVisibility(View.GONE);
    }

    private Handler migrateHandler = new Handler() {
        public void handleMessage(Message msg) {
            llTicketMigrating.setVisibility(View.GONE);
            llTicketRefresh.setVisibility(View.VISIBLE);
            switch (msg.what) {
                case MigrateDossierAsyncTask.MigrateSuccessful:
                    //Log.d(TAG, "MigrateSuccessful...");
                    break;
                case MigrateDossierAsyncTask.MigrateUnSuccessful:
                    //Log.d(TAG, "MigrateUnSuccessful...");
                    break;
            }
        }
    };

    public void active(View view){
        isActive = true;
        initData(0);
        setActiveView();
    }

    public void inActive(View view){
        isActive = false;
        initData(1);
        setInActiveView();
    }

    public void clickHelp(View view){
        startActivity(WizardActivity.createIntent(this, WizardActivity.Wizard_MyTickets));
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.DOSSIER_CATEGORY,TrackerConstant.DOSSIER_SELECT_WIZARD,"");
    }

    private void setActiveView(){
        tvActive.setBackgroundColor(getResources().getColor(R.color.background_group_title));
        tvInActive.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
    }

    private void setInActiveView(){
        tvActive.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
        tvInActive.setBackgroundColor(getResources().getColor(R.color.background_group_title));
    }


/*    @Override
    protected void onStop() {
        unregisterReceiver(migrateDossierReceiver);
        super.onStop();
    }*/

    @Override
    public void refresh(Dossier dossier, DossierSummary dossierSummary) {
        //Log.d(TAG, "refresh...Dossier");
        this.dossier = dossier;
        reEnableState();
    }

    private class MyState {

        public AsyncDossierAfterSaleResponse asyncDossierAfterSaleResponse;
        public DossierDetailsResponse dossier;
        private boolean isRefreshed;
        public void unRegisterHandler(){
            if (asyncDossierAfterSaleResponse != null){
                asyncDossierAfterSaleResponse.unregisterHandler();
            }
        }

        public void registerHandler(Handler handler){
            if (asyncDossierAfterSaleResponse != null){
                asyncDossierAfterSaleResponse.registerHandler(handler);
            }
        }
    }

    private void reEnableState(){
        if (myState == null){
            //Initial call
            //Log.i(TAG, "myState is null");
            myState = new MyState();
            //getDossierData("MXMYQWZ"); Testing

        }
        getDossierData();
        //Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);

    }
    // refresh Data
    private void getDossierData(){
        if(this.dossier != null){
            DossierDetailParameter dossier = new DossierDetailParameter(this.dossier.getDossierId(), null);
            List<DossierDetailParameter> dossiers = new ArrayList<DossierDetailParameter>();
            dossiers.add(dossier);
            myState.asyncDossierAfterSaleResponse = NMBSApplication.getInstance().getAssistantService().refrshDossierDetail(dossiers, settingService, false, true);
            myState.asyncDossierAfterSaleResponse.registerHandler(mHandler);
            myState.registerHandler(mHandler);
            showWaitDialog();
        }
    }

    private void refreshRealTime(){
        RealTimeInfoAsyncTask asyncTask = new RealTimeInfoAsyncTask(getApplicationContext(), settingService, dossierDetailsService,
                realTimeHandler, realTimeInfoRequestParameter);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private Handler realTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RealTimeInfoAsyncTask.REALTIMEMESSAGE:
                    //Log.d(TAG, "realTimeHandler...Finished RealTime..");
                    break;
                case ServiceConstant.MESSAGE_WHAT_ERROR:
                    break;
            }
        }
    };

    private void responseReceived(DossierDetailsResponse dossierResponse) {
        //Log.e(TAG, "responseReceived....");
        hideWaitDialog();
        refreshRealTime();
        if(dossierResponse != null){
            if(dossierSummary != null){
                if(dossierSummary.isDossierPushEnabled()){
                    dossierDetailsService.autoEnableSubscription(dossierSummary, dossier, null, settingService.getCurrentLanguagesKey());
                    dossierDetailsService.deleteSubscription(dossier);
                }else {
                    dossierDetailsService.disableSubscription(dossier, dossierSummary, false);
                }
            }
        }else{
            dossierDetailsService.deleteDossierSubscription(dossierSummary, dossier);
        }
        myState.dossier = dossierResponse;
        myState.unRegisterHandler();
        myState.isRefreshed = true;
        initData(4);
        //Log.d(TAG, "show response");
    }

    //Handler which is called when the App is refreshed.
    // The what parameter of the message decides if there was an error or not.
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceConstant.MESSAGE_WHAT_OK:
                    responseReceived(myState.asyncDossierAfterSaleResponse.getDossierAftersalesResponse());
                    // showView();

                    break;
                case ServiceConstant.MESSAGE_WHAT_ERROR:
                    hideWaitDialog();
                    Bundle bundle = msg.getData();
                    NetworkError error = (NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
                    String responseErrorMessage = bundle.getString(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
                    switch (error) {
                        case TIMEOUT:
                            responseErrorMessage = getResources().getString(R.string.general_server_unavailable);

                            // finish();
                            break;
                        case wrongCombination:
                            responseErrorMessage = getResources().getString(R.string.upload_tickets_failure_parameter_mismatch);
                            // finish();
                            break;
                        case donotContainTicke:
                            responseErrorMessage = getResources().getString(R.string.upload_tickets_failure_notravelsegment);
                            // finish();
                            break;
                        case journeyPast:
                            responseErrorMessage = getResources().getString(R.string.alert_add_existing_ticket_journey_past_description);

                            // finish();
                            break;
                        case CustomError:
                            hideWaitDialog();
                            if (responseErrorMessage == null) {
                                responseErrorMessage = getString(R.string.general_server_unavailable);
                            }
                            break;
                        default:
                            break;
                    }
                    if(!isFinishing()){
                        DialogError dialogError = new DialogError(MyTicketsActivity.this,
                                getResources().getString(R.string.upload_tickets_failure_title), responseErrorMessage);

                        dialogError.show();
                    }

                    //Log.d(TAG, "Upload dossier failure, the error msg is.........." + responseErrorMessage);
                    break;
            }
        }
    };

    // show progressDialog.
    private void showWaitDialog() {
        if(isFinishing()){
            return;
        }
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this,
                    getString(R.string.alert_loading),
                    getString(R.string.alert_waiting), true);
        }
    }

    // hide progressDialog
    private void hideWaitDialog() {

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        initView();
        if(UploadDossierAsyncTask.isUploading){
            showWaitDialog();
        }

        if(RefreshDossierAsyncTask.isRefreshing){
            showWaitDialog();
        }
        if (migrateDossierReceiver == null) {
            migrateDossierReceiver = new MigrateDossierReceiver();
            registerReceiver(migrateDossierReceiver, new IntentFilter(MigrateDossierAsyncTask.MigrateDossier_Broadcast));
        }

        if (uploadDossierReceiver == null) {
            uploadDossierReceiver = new UploadDossierReceiver();
            registerReceiver(uploadDossierReceiver, new IntentFilter(UploadDossierAsyncTask.UploadDossier_Broadcast));
        }
        if (refreshDossierReceiver == null) {
            refreshDossierReceiver = new RefreshDossierReceiver();
            registerReceiver(refreshDossierReceiver, new IntentFilter(RefreshDossierAsyncTask.RefreshDossier_Broadcast));
        }

        if (refreshMultipleDossierReceiver == null) {
            refreshMultipleDossierReceiver = new RefreshMultipleDossierReceiver();
            registerReceiver(refreshMultipleDossierReceiver, new IntentFilter(AutoRetrievalDossiersTask.RefreshDossier_Broadcast));
        }

        initData(3);
        super.onResume();
    }

    class RefreshDossierReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("RefreshDossierReceiver", "RefreshDossierReceiver...onReceive...");
            //realTimeFinished();
            //llTicketMigrating.setVisibility(View.GONE);
            //llTicketRefresh.setVisibility(View.GONE);
/*            if(dossierDetailsService.getCurrentDossier() != null){
                dossier = dossierDetailsService.getCurrentDossier();
            }*/
            hideWaitDialog();
            initData(6);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(migrateDossierReceiver != null){
            unregisterReceiver(migrateDossierReceiver);
            dossierDetailsService.cleanRealtimeInfoRequest();
        }
        if(uploadDossierReceiver != null){
            unregisterReceiver(uploadDossierReceiver);
        }

        if(refreshDossierReceiver != null){
            unregisterReceiver(refreshDossierReceiver);
        }
        if(refreshMultipleDossierReceiver != null){
            unregisterReceiver(refreshMultipleDossierReceiver);
        }
        Intent broadcastIntent = new Intent(AutoRetrievalDossiersTask.RefreshDossier_Broadcast);
        getApplication().sendBroadcast(broadcastIntent);
    }

    class RefreshMultipleDossierReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("RefreshossierReceiver", "RefreshMultipleDossierReceiver...onReceive...");
            //Log.d("MigrateDossier", "MigrateDossierReceiver...onReceive...");
            //realTimeFinished();
            //llTicketMigrating.setVisibility(View.GONE);
            //llTicketRefresh.setVisibility(View.GONE);
            if (AutoRetrievalDossiersTask.isWorking){
                llAotoRetrieval.setVisibility(View.VISIBLE);
            }else{
                llAotoRetrieval.setVisibility(View.GONE);
            }
            initData(7);
        }
    }

    class MigrateDossierReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("MigrateDossier", "MigrateDossierReceiver...onReceive...");
            //realTimeFinished();
            llTicketMigrating.setVisibility(View.GONE);
            llTicketRefresh.setVisibility(View.GONE);
            initData(5);
        }
    }
    class UploadDossierReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("UploadDossierReceiver", "UploadDossierReceiver...onReceive...");
            //realTimeFinished();
            //llTicketMigrating.setVisibility(View.GONE);
            //llTicketRefresh.setVisibility(View.GONE);
            hideWaitDialog();
            String errorMessage = intent.getStringExtra(UploadDossierAsyncTask.Intent_Key_ErrorMessage);
            String dnr = intent.getStringExtra(UploadDossierAsyncTask.Intent_Key_Dnr);
            String email = intent.getStringExtra(UploadDossierAsyncTask.Intent_Key_Email);
            if(errorMessage != null){
                startActivity(UploadDossierActivity.createUploadDossierIntent(MyTicketsActivity.this, NMBSApplication.PAGE_UploadTicket_DNR, null, dnr, email, errorMessage));

            }
            initData(8);
        }
    }

    public void test(View view){
        startActivity(TestActivity.createIntent(this));
        finish();
    }

    public void stationBoard(View view){

        MenuUtil.stationBoard(this, mDrawerLayout, mDrawerList);
    }

    public void stations(View view){
        MenuUtil.stations(this, mDrawerLayout, mDrawerList);
    }

    public void realtimeAlerts(View view){
        MenuUtil.realtimeAlerts(this, mDrawerLayout, mDrawerList);
    }

    public void bookTicktes(View view){
        MenuUtil.bookTicktes(this, mDrawerLayout, mDrawerList, masterService);
    }

    public void lowestFares(View view){
        MenuUtil.lowestFares(this, mDrawerLayout, mDrawerList, masterService);
    }
    public void trainschedules(View view){
        MenuUtil.trainschedules(this, mDrawerLayout, mDrawerList);
    }

    public void settings(View view){
        MenuUtil.settings(this, mDrawerLayout, mDrawerList);
    }

    public void about(View view){
        MenuUtil.about(this, mDrawerLayout, mDrawerList);
    }
    public void myTickets(View view){
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }

    public void uploadTickets(View view){
        MenuUtil.uploadTickets(this, mDrawerLayout, mDrawerList);
    }
    public void myOption(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        MenuUtil.myOption(this, mDrawerLayout, mDrawerList, masterService);
    }

    public void loginOrManage(View view){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        MenuUtil.loginOrManage(this, mDrawerLayout, mDrawerList, masterService);
    }

    public void clickMenu(View view) {
        //startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
        MenuUtil.clickMenu(this);
        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    public void close(View view){
        finish();
    }
}
