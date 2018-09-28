package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;


import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogError;
import com.nmbs.activities.view.DialogSubNotification;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.DossierDetailConnectionAdapter;
import com.nmbs.adapter.DossierDetailStationInfoNameAdapter;
import com.nmbs.adapter.DossierDetailStationInfoTextAdapter;
import com.nmbs.adapter.DossierDetailTicketAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.RealTimeInfoAsyncTask;
import com.nmbs.async.RefreshDossierAsyncTask;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.model.Connection;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierDetailParameter;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossierTravelSegment;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.PDF;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.model.RealTimeInfoResponse;
import com.nmbs.model.StationInfo;
import com.nmbs.model.StationInfoResponse;
import com.nmbs.model.StationIsVirtualText;
import com.nmbs.services.IStationInfoService;
import com.nmbs.services.impl.AsyncDossierAfterSaleResponse;
import com.nmbs.services.impl.ClickToCallService;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.FileManager;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.RatingUtil;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shig on 2016/4/16.
 */
public class DossierDetailActivity extends BaseActivity {
    private SettingService settingService;
    private static final String Intent_Key_Dossier = "Dossier";
    private static final String Intent_Key_DossierSummary = "DossierSummary";
    private final static String TAG = UploadDossierActivity.class.getSimpleName();
    private RelativeLayout rlTotalDossier, rlReservation, rlPickupStation, rlInsurance, rlStationifo, rlTickets, rlConnections;
    private Dossier dossier;
    private DossierSummary dossierSummary;
    private TextView tvTotal, tvReservation, tvPickupStation, tvAttention, tvDossierId, tvDossierError, tvTicketsIntroduction,
    tvDossierFullfilmentFailed, tvDossierInprogres, tvDossierMissingPDF, tvDossierMissingPDFOrBarcode;
    private boolean isExpandBookingInfo, isExpandTicket = true, isExpandConnection, isExpandStationInfo;
    private ImageView ivBookingInfoExpand, ivTicketsExpand, ivConnectionExpand, ivStationInfoExpand;
    private LinearLayout llDossierDetailBookingInfo, llDossierDetailTickets, llDossierTravelSegments, llDossierConnections,
            llDossierConnectionsInfo, llDossierStationInfo, llDossierStationInfoTexts, llDossierStationInfoNames, llDossierFooter, llRoot;
    private DossierDetailTicketAdapter dossierDetailTicketAdapter;
    private DossierDetailConnectionAdapter dossierDetailConnectionAdapter;
    private DossierDetailStationInfoNameAdapter dossierDetailStationInfoAdapter;
    private Button btnPdf, btnSetAlert, btnRealTime;
    private DossierDetailsService dossierDetailsService;
    private IStationInfoService stationInfoService;
    private DossierDetailStationInfoTextAdapter dossierDetailStationInfoTextAdapter;
    private MyState myState;
    private ProgressDialog progressDialog;
    private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
    private GeneralSetting generalSetting;
    public static final String REFRESH_VIEW_ACTION = "com.nmbs.intent.action.refresh.view.service";
    private String pdfId;
    private int clickToCallScenarioId;
    private ServiceRealTimeReceiver serviceRealTimeReceiver;
    private ScrollView slRoot;
    private RefreshDossierReceiver refreshDossierReceiver;
    //private ExpandView mExpandView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
        stationInfoService = ((NMBSApplication) getApplication()).getStationInfoService();
        setContentView(R.layout.activity_dossier_detail);

        getIntentValues();
        bindAllViewElements();

        //initData();
        generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
        GoogleAnalyticsUtil.getInstance().sendScreen(DossierDetailActivity.this, TrackerConstant.DOSSIER_DETAILS);
        RatingUtil.saveDossierDetails(getApplicationContext());
    }

    private void getIntentValues(){
        dossier = (Dossier) getIntent().getSerializableExtra(Intent_Key_Dossier);
        dossierSummary = (DossierSummary) getIntent().getSerializableExtra(Intent_Key_DossierSummary);


        /*Map<String, Object> objectMap = null;
        List<Object> objectList = null;
        objectMap = dossierDetailsService.getRealTimeInfoRequesMap(dossier, NMBSApplication.getInstance().getMasterService().loadGeneralSetting());
        objectList = dossierDetailsService.getRealTimeInfoRequestList(objectMap);*/
        //Log.e("RealTime", "DossierDetailActivity...key..." + realTimeInfoRequestParameter);
        realTimeInfoRequestParameter = dossierDetailsService.getRealTimeInfoRequestParameterForDossier(dossier);
        //realTimeInfoRequestParameter.setMap(objectMap);

       // Log.e("RealTime", "DossierDetailActivity...key..." + realTimeInfoRequestParameter.getRealTimeInfoRequests());

    }
    private void initBtns(){
        if(dossierSummary != null){
            if(!dossierSummary.isDossierPushEnabled()){
                if (dossierDetailsService.showOverlay(dossier)){
                    btnSetAlert.setVisibility(View.VISIBLE);
                }else{
                    btnSetAlert.setVisibility(View.GONE);
                }
            }else{
                btnSetAlert.setVisibility(View.VISIBLE);
            }
        }
        if(realTimeInfoRequestParameter != null){
            if(realTimeInfoRequestParameter.getMap() != null && realTimeInfoRequestParameter.getMap().size() > 0){
                btnRealTime.setVisibility(View.VISIBLE);
            }else{
                btnRealTime.setVisibility(View.GONE);
            }
        }
        if(btnSetAlert.getVisibility() == View.GONE && btnRealTime.getVisibility() == View.GONE){
            llDossierFooter.setVisibility(View.GONE);
        }else{

        }
    }
    private void initData(){
        initBtns();
        if(dossier != null){
            if(DossierDetailsService.DossierStateConfirmed.equalsIgnoreCase(dossier.getDossierState())
                    && dossier.isFullfilmentFailed() == false && dossier.isMissingPDFs() == false){
                clickToCallScenarioId = ClickToCallService.DossierQuestion;
            }
            if(!DossierDetailsService.DossierStateConfirmed.equalsIgnoreCase(dossier.getDossierState())
                    && dossier.isFullfilmentFailed() == true && dossier.isMissingPDFs() == true){
                clickToCallScenarioId = ClickToCallService.DossierIssue;
            }
            initBtns();
            initBooking();
            bindAllViewElements();
            llDossierTravelSegments.removeAllViews();
            dossierDetailTicketAdapter = new DossierDetailTicketAdapter(this);
            List<String> codes = new ArrayList<>();
            Map<String, StationIsVirtualText> segmentMap = new HashMap<>();
            List<StationIsVirtualText> texts = new ArrayList<>();

            for(DossierTravelSegment dossierTravelSegment : dossier.getTravelSegments()){
                if(dossierTravelSegment != null){
                    //Log.e("", "" + dossierTravelSegment.getSortedDate())
                    if(!codes.contains(dossierTravelSegment.getOriginStationRcode())){
                        codes.add(dossierTravelSegment.getOriginStationRcode());
                    }
                    if(!codes.contains(dossierTravelSegment.getDestinationStationRcode())){
                        codes.add(dossierTravelSegment.getDestinationStationRcode());
                    }
                    texts = dossierDetailsService.getStationsIsVirtualTextFromSegment(dossierTravelSegment, texts);

                    //Log.e(TAG, "shouldRefresh...key..." + shouldRefresh);
                }
            }
            for(DossierTravelSegment dossierTravelSegment : dossier.getParentTravelSegments()){
                boolean shouldRefresh = dossierDetailsService.shouldRefresh(realTimeInfoRequestParameter, dossierTravelSegment, null);
                dossierDetailTicketAdapter.getTravelSegmentView(dossierTravelSegment, llDossierTravelSegments, dossier, dossierSummary, shouldRefresh, realTimeInfoRequestParameter);
            }

            for(StationIsVirtualText stationIsVirtualText : texts){
                if(!segmentMap.containsKey(stationIsVirtualText.getStationCode())){
                    segmentMap.put(stationIsVirtualText.getStationCode(), stationIsVirtualText);
                }
            }
            llDossierStationInfoTexts.removeAllViews();
            dossierDetailStationInfoTextAdapter = new DossierDetailStationInfoTextAdapter(this);
            if(segmentMap.size() > 0){
                for (StationIsVirtualText stationIsVirtualText : segmentMap.values()){
                    dossierDetailStationInfoTextAdapter.getStationInfoView(stationIsVirtualText, llDossierStationInfoTexts);
                }
            }

            llDossierConnections.removeAllViews();
            dossierDetailConnectionAdapter = new DossierDetailConnectionAdapter(this);
            for(Connection connection : dossier.getConnections()){
                if(connection != null){
                    if(!codes.contains(connection.getOriginStationRcode())){
                        codes.add(connection.getOriginStationRcode());
                    }
                    if(!codes.contains(connection.getDestinationStationRcode())){
                        codes.add(connection.getDestinationStationRcode());
                    }
                    //Log.e("RealTime", "should..................");

                    boolean shouldRefresh = dossierDetailsService.shouldRefresh(realTimeInfoRequestParameter, null, connection);
                    //Log.e("RealTime", "shouldRefresh......" + shouldRefresh);
                    RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(connection.getConnectionId(), getApplicationContext());
                    //Log.e("RealTime", "shouldRefresh......" + realTimeInfoResponse);

                    RealTimeConnection realTimeConnection =  (RealTimeConnection) dossierDetailsService.getRealTime(realTimeInfoResponse);

                    dossierDetailConnectionAdapter.getConnectionsView(connection, dossier, llDossierConnections, shouldRefresh, realTimeConnection);
                }
            }
            if(dossier.getConnections() == null || dossier.getConnections().size() == 0){
                rlConnections.setVisibility(View.GONE);
                //llDossierConnectionsInfo.setVisibility(View.GONE);
            }else{
                rlConnections.setVisibility(View.VISIBLE);
                //llDossierConnectionsInfo.setVisibility(View.VISIBLE);
            }
            StationInfoResponse stationInfoResponse = stationInfoService.getStationInfoResponseInLocal(getApplicationContext());
            List<StationInfo> stationInfos = dossierDetailsService.getStationInfoFromDossier(stationInfoResponse, codes);
            llDossierStationInfoNames.removeAllViews();
            dossierDetailStationInfoAdapter = new DossierDetailStationInfoNameAdapter(this);
            for(StationInfo stationInfo : stationInfos){
                if(stationInfo != null){
                    dossierDetailStationInfoAdapter.getStationInfoView(stationInfo, llDossierStationInfoNames);
                }
            }
            if(stationInfos == null || stationInfos.size() == 0){
                rlStationifo.setVisibility(View.GONE);
                //llDossierStationInfo.setVisibility(View.GONE);
            }else{
                rlStationifo.setVisibility(View.VISIBLE);
                //llDossierStationInfo.setVisibility(View.VISIBLE);
            }
        }
    }
    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
    private void initBooking(){
        tvDossierId.setText(dossier.getDossierId());
        if(dossier.isHasGreenpointsPayment()){
            rlTotalDossier.setVisibility(View.GONE);
        }else{
            rlTotalDossier.setVisibility(View.VISIBLE);
            tvTotal.setText(dossier.getDossierCurrency() + " " + String.valueOf(dossier.getTotalDossierValue()));
        }
        if(dossier.getReservationCode().length == 0){
            rlReservation.setVisibility(View.GONE);
        }else{
            rlReservation.setVisibility(View.VISIBLE);
            tvReservation.setText(Utils.arrayToString(dossier.getReservationCode()));
        }
        if(dossier.getPickUpStationNames().length == 0){
            rlPickupStation.setVisibility(View.GONE);
        }else{
            if(dossier.isPincodeNeeded()){
                rlPickupStation.setVisibility(View.VISIBLE);
                tvPickupStation.setText(Utils.arrayToString(dossier.getPickUpStationNames()));
                String attention = getResources().getString(R.string.dossier_detail_attention);
                tvAttention.setText(Html.fromHtml("<b>" + attention + "</b> " + getResources().getString(R.string.dossier_detail_attention_info)));
            }
        }
        if(dossier.isHasInsurance()){
            rlInsurance.setVisibility(View.VISIBLE);
        }else{
            rlInsurance.setVisibility(View.GONE);
        }
        if(DossierDetailsService.DossierStateError.equalsIgnoreCase(dossier.getDossierState())){
            tvDossierError.setVisibility(View.VISIBLE);
        }else {
            tvDossierError.setVisibility(View.GONE);
        }
        if(DossierDetailsService.DossierStateInProgress.equalsIgnoreCase(dossier.getDossierState())){
            tvDossierInprogres.setVisibility(View.VISIBLE);
        }else {
            tvDossierInprogres.setVisibility(View.GONE);
        }
        if(DossierDetailsService.DossierStateConfirmed.equalsIgnoreCase(dossier.getDossierState()) && dossier.isFullfilmentFailed()){
            tvDossierFullfilmentFailed.setVisibility(View.VISIBLE);
        }else{
            tvDossierFullfilmentFailed.setVisibility(View.GONE);
        }
        if(dossier.isMissingPDFs()){
            tvDossierMissingPDF.setVisibility(View.VISIBLE);
        }else {
            tvDossierMissingPDF.setVisibility(View.GONE);
        }
        if(!dossierSummary.isBarcodeSuccessfully() || !dossierSummary.isPDFSuccessfully()){
            tvDossierMissingPDFOrBarcode.setVisibility(View.VISIBLE);
        }else {
            tvDossierMissingPDFOrBarcode.setVisibility(View.GONE);
        }
    }

    private void bindAllViewElements(){
        tvDossierError = (TextView) findViewById(R.id.tv_dossier_error);
        tvDossierFullfilmentFailed = (TextView) findViewById(R.id.tv_dossier_fullfilment_failed);
        tvDossierInprogres = (TextView) findViewById(R.id.tv_dossier_inprogress);
        tvDossierMissingPDF = (TextView) findViewById(R.id.tv_dossier_missing_pdf);
        tvDossierMissingPDFOrBarcode = (TextView) findViewById(R.id.tv_dossier_missing_pdf_or_barcode);
        //Booking info part
        llDossierDetailBookingInfo = (LinearLayout)findViewById(R.id.ll_dossier_detail_bookinginfo);
        rlTotalDossier = (RelativeLayout)findViewById(R.id.rl_total_dossier);
        tvDossierId = (TextView) findViewById(R.id.tv_dossier_id);
        rlInsurance = (RelativeLayout) findViewById(R.id.rl_insurance);
        slRoot = (ScrollView) findViewById(R.id.sl_root);
        llRoot = (LinearLayout) findViewById(R.id.ll_root);

        //rlBookingInfo = (RelativeLayout)findViewById(R.id.rl_bookinginfo);
        tvTotal = (TextView) findViewById(R.id.tv_total_value);
        rlReservation = (RelativeLayout)findViewById(R.id.rl_reservation);
        tvReservation = (TextView) findViewById(R.id.tv_reservation_value);
        rlPickupStation = (RelativeLayout)findViewById(R.id.rl_pickup_station);

        tvPickupStation = (TextView) findViewById(R.id.tv_pickup_station_value);
        tvAttention = (TextView) findViewById(R.id.tv_attention);
        ivBookingInfoExpand = (ImageView) findViewById(R.id.iv_bookinginfo_expand_action);
        initBookingInfoView();
        //Tickets part
        rlTickets = (RelativeLayout)findViewById(R.id.rl_dossier_detail_tickets);
        ivTicketsExpand = (ImageView) findViewById(R.id.iv_tickets_expand_action);
        llDossierDetailTickets = (LinearLayout)findViewById(R.id.ll_dossier_detail_tickets);
        llDossierTravelSegments = (LinearLayout)findViewById(R.id.ll_dossier_travelsegments);
        tvTicketsIntroduction = (TextView) findViewById(R.id.tv_tickets_introduction);

        btnPdf = (Button)findViewById(R.id.btn_pdf);
        initTicketsView();
        //Connection part
        llDossierConnectionsInfo = (LinearLayout)findViewById(R.id.ll_dossier_detail_connection);
        llDossierConnections = (LinearLayout)findViewById(R.id.ll_dossier_connections);
        rlConnections = (RelativeLayout)findViewById(R.id.rl_dossier_detail_connections);
        ivConnectionExpand = (ImageView) findViewById(R.id.iv_dossier_detail_connections_expand_action);
        initConnectionView();
        /*LinearLayout root = (LinearLayout)findViewById(R.id.root);
        root.setVisibility(View.GONE);*/

        rlStationifo = (RelativeLayout)findViewById(R.id.rl_stationifo);
        ivStationInfoExpand = (ImageView) findViewById(R.id.iv_dossier_detail_stationinfo_expand_action);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //final LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.layout_expand, null);
        llDossierStationInfo = (LinearLayout)findViewById(R.id.rl_dossier_detail_stationinfos);

        llDossierStationInfoTexts = (LinearLayout)findViewById(R.id.ll_dossier_station_texts);
        llDossierStationInfoNames = (LinearLayout)findViewById(R.id.ll_dossier_station_names);
        //mExpandView = (ExpandView) findViewById(R.id.expandView);

        btnSetAlert = (Button)findViewById(R.id.btn_set_alert);
        btnRealTime = (Button)findViewById(R.id.btn_realtime);
        llDossierFooter = (LinearLayout)findViewById(R.id.ll_dossier_footer);
        initStationInfoView();
    }

    public void openPDF(View view){
        List<PDF> pdfs = dossierDetailsService.getPdfs(this, dossier, dossier.getPdfs());
        if(pdfs != null && pdfs.size() > 0){
            if(pdfs.size() > 1){
                dossierDetailsService.showPDFDialog(this, pdfs, dossier);
            }else{
                PDF pdf = pdfs.get(0);
                if(pdf != null){
                    pdfId = pdf.getPdfId();
                    dossierDetailsService.openPDF(dossier.getDossierId(), pdf.getPdfId());
                }
            }
        }
    }

    private void initBookingInfoView(){
        if(isExpandBookingInfo){
            llDossierDetailBookingInfo.setVisibility(View.VISIBLE);
            ivBookingInfoExpand.setImageResource(R.drawable.ic_minus);
        }else{
            llDossierDetailBookingInfo.setVisibility(View.GONE);
            ivBookingInfoExpand.setImageResource(R.drawable.ic_plus);
        }
    }

    private void initTicketsView(){
        if(isExpandTicket){
            ivTicketsExpand.setImageResource(R.drawable.ic_minus);
            llDossierDetailTickets.setVisibility(View.VISIBLE);
        }else{
            ivTicketsExpand.setImageResource(R.drawable.ic_plus);
            llDossierDetailTickets.setVisibility(View.GONE);
        }
        if(dossierSummary != null){
            if(dossierSummary.isPDFSuccessfully()){
                btnPdf.setVisibility(View.VISIBLE);
            }else{
                btnPdf.setVisibility(View.GONE);
            }
        }
    }

    private void initConnectionView(){
//        Log.e("isExpandConnection", "isExpandConnection..." + isExpandConnection);
        if(isExpandConnection){
            llDossierConnectionsInfo.setVisibility(View.VISIBLE);
            ivConnectionExpand.setImageResource(R.drawable.ic_minus);
        }else{
            llDossierConnectionsInfo.setVisibility(View.GONE);
            ivConnectionExpand.setImageResource(R.drawable.ic_plus);
        }
    }

    private void initStationInfoView(){
        if(isExpandStationInfo){
            llDossierStationInfo.setVisibility(View.VISIBLE);
            ivStationInfoExpand.setImageResource(R.drawable.ic_minus);
        }else{
            llDossierStationInfo.setVisibility(View.GONE);
            ivStationInfoExpand.setImageResource(R.drawable.ic_plus);
        }
    }

    public void showBookingInfo(View view){
        slRoot.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        if(isExpandBookingInfo){
            isExpandBookingInfo = false;
            ivBookingInfoExpand.setImageResource(R.drawable.ic_plus);
            llDossierDetailBookingInfo.setVisibility(View.GONE);
        }else{
            isExpandBookingInfo = true;
            ivBookingInfoExpand.setImageResource(R.drawable.ic_minus);
            llDossierDetailBookingInfo.setVisibility(View.VISIBLE);
        }
    }

    public void showTickets(View view){
        slRoot.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        if(isExpandTicket){
            isExpandTicket = false;
            ivTicketsExpand.setImageResource(R.drawable.ic_plus);
            llDossierDetailTickets.setVisibility(View.GONE);
        }else{
            isExpandTicket = true;
            llDossierDetailTickets.setVisibility(View.VISIBLE);
        }

    }

    public void showConnections(View view){
        slRoot.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        if(isExpandConnection){
            isExpandConnection = false;
            ivConnectionExpand.setImageResource(R.drawable.ic_plus);
            llDossierConnectionsInfo.setVisibility(View.GONE);

        }else{
            isExpandConnection = true;
            ivConnectionExpand.setImageResource(R.drawable.ic_minus);
            llDossierConnectionsInfo.setVisibility(View.VISIBLE);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            try {Thread.sleep(100);} catch (InterruptedException e) {}
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    slRoot.fullScroll(View.FOCUS_DOWN);
                    slRoot.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
                }
            });
        }
    };

    public void showStationInfo(View view){
        slRoot.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        if(isExpandStationInfo){
            isExpandStationInfo = false;
            ivStationInfoExpand.setImageResource(R.drawable.ic_plus);
            llDossierStationInfo.setVisibility(View.GONE);

        }else{

            isExpandStationInfo = true;
            ivStationInfoExpand.setImageResource(R.drawable.ic_minus);
            llDossierStationInfo.setVisibility(View.VISIBLE);
        }
    }

    public void back(View view){
        finish();
    }

    public void showWizard(View view){
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY,TrackerConstant.DOSSIER_SELECT_WIZARD,"");
        startActivity(WizardActivity.createIntent(this, WizardActivity.Wizard_MyTickets));
    }

    public void refresh(View view){
        reEnableState();
    }

    public void click2call(View view){
        startActivity(com.nmbs.activities.CallCenterActivity.createIntent(DossierDetailActivity.this, clickToCallScenarioId, null, dossier));
    }
    public void exchange(View view){
        startActivity(com.nmbs.activities.CallCenterActivity.createIntent(DossierDetailActivity.this, ClickToCallService.Exchange, null, dossier));
    }

    public void termsConditions(View view){
        try {
            if(generalSetting != null){
                String pdf = generalSetting.getInsuranceConditionsPdf();
                String fileName = Utils.sha1(pdf) + ".pdf";
                File file = FileManager.getInstance().getExternalStoragePrivateFile(getApplicationContext(), "GeneralSetting", fileName);
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //Log.e(TAG, "ActivityNotFoundException, Open PDF Failed", e);
                    }
                } else {
                    Uri path = Uri.fromFile(new File("file:///android_asset/TermsAndConditions_NL.htm"));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //Log.e(TAG, "ActivityNotFoundException, Open PDF Failed", e);
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setAlert(View view){
        DialogSubNotification dialog = new DialogSubNotification(this, dossier, false, NMBSApplication.PAGE_Dossier_Details);
        dialog.setReloadCallback(new DialogSubNotification.ReloadCallback() {
            public void reloadData() {
                initData();
            }
        });
        dialog.show();
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

    public void refreshRealTime(View view){
        if(RealTimeInfoAsyncTask.isRefreshing != true){

            refreshRealTime();
        }
    }

    private void refreshRealTime(){
        RealTimeInfoAsyncTask.isRefreshing = true;
        initData();
        btnRealTime.setBackgroundColor(getResources().getColor(R.color.summary_grey_blue_text_color));
        RealTimeInfoAsyncTask asyncTask = new RealTimeInfoAsyncTask(getApplicationContext(), settingService, dossierDetailsService,
                realTimeHandler, realTimeInfoRequestParameter);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private Handler realTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            RealTimeInfoAsyncTask.isRefreshing = false;
            btnRealTime.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
            switch (msg.what) {
                case RealTimeInfoAsyncTask.REALTIMEMESSAGE:
                    initData();
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
            dossierDetailsService.setCurrentDossier(dossierResponse.getDossier());

            DossierSummary dossierSummary = dossierDetailsService.getDossier(dossierResponse.getDossier().getDossierId());
            dossierDetailsService.setCurrentDossierSummary(dossierSummary);
            this.dossierSummary = dossierDetailsService.getCurrentDossierSummary();
            this.dossier = dossierDetailsService.getCurrentDossier();
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
        initData();
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
                    DialogError dialogError = new DialogError(DossierDetailActivity.this,
                            getResources().getString(R.string.alert_mytickets_refresh_failed), responseErrorMessage);

                    dialogError.show();
                    //Log.d(TAG, "Upload dossier failure, the error msg is.........." + responseErrorMessage);
                    break;
            }
        }
    };

    // show progressDialog.
    private void showWaitDialog() {

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

    public static Intent createIntent(Context context, Dossier dossier, DossierSummary dossierSummary) {
        //Log.d(TAG, "Dossier is.........." + dossier.getDossierId());
        Intent intent = new Intent(context, DossierDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent_Key_Dossier, dossier);
        intent.putExtra(Intent_Key_DossierSummary, dossierSummary);

        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dossierDetailsService.deleteUndecryptPdfFile(dossier.getDossierId(), pdfId);
        if(serviceRealTimeReceiver != null){
            unregisterReceiver(serviceRealTimeReceiver);
        }
        if(refreshDossierReceiver != null){
            unregisterReceiver(refreshDossierReceiver);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(RefreshDossierAsyncTask.isRefreshing){
            showWaitDialog();
        }
        if (serviceRealTimeReceiver == null) {
            serviceRealTimeReceiver = new ServiceRealTimeReceiver();
            registerReceiver(serviceRealTimeReceiver, new IntentFilter(RealTimeInfoAsyncTask.RealTime_Broadcast));
        }
        if(dossierDetailsService.getCurrentDossier() != null){
            dossier = dossierDetailsService.getCurrentDossier();

        }
        if(dossierDetailsService.getCurrentDossierSummary() != null){
            dossierSummary = dossierDetailsService.getCurrentDossierSummary();
        }
        if (refreshDossierReceiver == null) {
            refreshDossierReceiver = new RefreshDossierReceiver();
            registerReceiver(refreshDossierReceiver, new IntentFilter(RefreshDossierAsyncTask.RefreshDossier_Broadcast));
        }
        //Log.d("Connection", "initData()..........");
        initData();
    }

    class RefreshDossierReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RefreshDossierReceiver", "RefreshDossierReceiver...onReceive...");
            //realTimeFinished();
            //llTicketMigrating.setVisibility(View.GONE);
            //llTicketRefresh.setVisibility(View.GONE);
            if(dossierDetailsService.getCurrentDossier() != null){
                dossier = dossierDetailsService.getCurrentDossier();
            }
            hideWaitDialog();
            initData();
        }
    }

    class ServiceRealTimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("RealTime", "ServiceRealTimeReceiver...onReceive...");
            RealTimeInfoAsyncTask.isRefreshing = false;
            initData();
        }
    }
}
