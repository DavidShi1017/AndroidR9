package com.cfl.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activity.BaseActivity;
import com.cfl.adapter.StationInfoDetailFacilitiesAdapter;
import com.cfl.adapter.StationInfoDetailInformationAdapter;
import com.cfl.adapter.StationInfoDetailParkingAdapter;
import com.cfl.application.NMBSApplication;
import com.cfl.dataaccess.restservice.impl.StationInfoDataService;
import com.cfl.log.LogUtils;
import com.cfl.model.StationInfo;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IStationInfoService;
import com.cfl.services.impl.SettingService;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.NetworkUtils;
import com.cfl.util.Utils;

import java.io.File;
import java.io.InputStream;

public class StationInfoDetailActivity extends BaseActivity {
    private final static String STATION_INFO_SERIALIZABLE_KEY = "STATION_INFO";
    private LinearLayout stationInformationListView;
    private LinearLayout stationFacilitiesListView;
    private LinearLayout stationParkingListView, llMap;
    private StationInfoDetailInformationAdapter stationInfoDetailInformationAdapter;
    private StationInfoDetailFacilitiesAdapter stationInfoDetailFacilitiesAdapter;
    private StationInfoDetailParkingAdapter stationInfoDetailParkingAdapter;
    private TextView stationName;
    private TextView informationTabView;
    private TextView facilitiesView;
    private TextView parkingView;
    private TextView openHoursView;
    private TextView addressStreetView;
    private TextView addressCityView;
    private StationInfo stationInfo;
    private SettingService settingService;
    private Button btnMoreInfo, btnPdf;
    private IStationInfoService stationInfoService;
    private IClickToCallService clickToCallService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        stationInfoService = ((NMBSApplication) getApplication()).getStationInfoService();
        clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
        setContentView(R.layout.activity_station_info_detail);
        bindAllViewElements();
        getIntentValue();
        init();
    }

    public void back(View view){
        finish();
    }
    private void bindAllViewElements(){
        this.stationFacilitiesListView = (LinearLayout) findViewById(R.id.lv_station_info_detail_facilities);
        this.stationInformationListView = (LinearLayout) findViewById(R.id.lv_station_info_detail_information);
        this.stationParkingListView = (LinearLayout) findViewById(R.id.lv_station_info_detail_parking);
        this.stationName = (TextView) findViewById(R.id.tv_station_info_detail_title);
        this.informationTabView = (TextView) findViewById(R.id.tv_station_info_detail_tab_information);
        this.facilitiesView = (TextView) findViewById(R.id.tv_station_info_detail_tab_facilities);
        this.parkingView = (TextView) findViewById(R.id.tv_station_info_detail_tab_parking);
        this.openHoursView = (TextView) findViewById(R.id.tv_station_info_detail_header_opening_hours);
        this.addressStreetView = (TextView) findViewById(R.id.tv_station_info_detail_header_address_street);
        this.addressCityView = (TextView) findViewById(R.id.tv_station_info_detail_header_address_city);
        this.btnMoreInfo = (Button) findViewById(R.id.btn_more_info);
        this.btnPdf = (Button) findViewById(R.id.btn_pdf);
        this.llMap = (LinearLayout) findViewById(R.id.ll_map);
    }

    private void getIntentValue(){
        this.stationInfo = (StationInfo) getIntent().getSerializableExtra(STATION_INFO_SERIALIZABLE_KEY);
    }

    private void init(){
        if(stationInfo != null){
            //Log.d("StationInfo", "stationInfo Latitude..." + stationInfo.getLatitude() + "...stationInfo Longitude..." + stationInfo.getLongitude());
            /*if((stationInfo.getLatitude() != null && !stationInfo.getLatitude().isEmpty())
                    || (stationInfo.getLongitude() != null && !stationInfo.getLongitude().isEmpty())){
                llMap.setVisibility(View.VISIBLE);
            }else{
                llMap.setVisibility(View.GONE);
            }*/
            Log.d("StationInfo", "stationInfo..." + stationInfo.getName());
            File file = stationInfoService.getStationPDF(stationInfo);
            LogUtils.e("StationInfo", "stationInfo..file...." + file);

            if(stationInfo.getFloorPlanDownloadURL() == null || stationInfo.getFloorPlanDownloadURL().isEmpty()){
                LogUtils.e("StationInfo", "stationInfo..file...url.....isEmpty....." );
                btnPdf.setVisibility(View.GONE);
            }else{
                btnPdf.setVisibility(View.VISIBLE);
                if (file == null || !file.exists()) {
                    LogUtils.e("StationInfo", "stationInfo..file....not exists....." + file);
                    boolean isAssetStationPDFAvailable = stationInfoService.isAssetStationPDFAvailable(getApplicationContext(), stationInfo.getCode(), settingService.getCurrentLanguagesKey());
                    if (isAssetStationPDFAvailable) {
                        btnPdf.setVisibility(View.VISIBLE);
                    }else{
                        btnPdf.setVisibility(View.GONE);
                    }
                }else{
                    btnPdf.setVisibility(View.VISIBLE);
                }
            }
            this.stationName.setText(this.stationInfo.getName());
            //stationInfo.setStations(null);
            //stationInfo.setStationFacilities(null);
            if(this.stationInfo.getStations() != null && this.stationInfo.getStations().size() > 0){
                this.stationInfoDetailInformationAdapter = new StationInfoDetailInformationAdapter(this,this.stationInfo.getStations());
                for(int i=0;i<this.stationInfo.getStations().size();i++){
                    this.stationInfoDetailInformationAdapter.getInformationView(i,this.stationInformationListView);
                }
            }else{
                if(this.stationInfo.getStationFacilities() != null && this.stationInfo.getStationFacilities().size() > 0){
                    goToFacilities(facilitiesView);
                }else{
                    goToParking(parkingView);
                }

                informationTabView.setVisibility(View.GONE);
            }

            if(this.stationInfo.getStationFacilities() != null && this.stationInfo.getStationFacilities().size() > 0){
                this.stationInfoDetailFacilitiesAdapter = new StationInfoDetailFacilitiesAdapter(this,this.stationInfo.getStationFacilities());
                for(int j=0;j<this.stationInfo.getStationFacilities().size();j++){
                    this.stationInfoDetailFacilitiesAdapter.getFacilitiesView(j, this.stationFacilitiesListView);
                }
            }else {
                if (this.stationInfo.getStations() != null && this.stationInfo.getStations().size() > 0){
                    goToInformation(informationTabView);
                }else {
                    goToParking(parkingView);
                }

                facilitiesView.setVisibility(View.GONE);
            }
            boolean hasGeocoordinate = false;
            if(this.stationInfo.getParkings() != null && this.stationInfo.getParkings().size() > 0){
                this.stationInfoDetailParkingAdapter = new StationInfoDetailParkingAdapter(this,this.stationInfo.getParkings());
                for(int k=0;k<this.stationInfo.getParkings().size();k++){
                    if(stationInfo.getParkings().get(k) != null){
                        if((stationInfo.getParkings().get(k).getLatitude() != null && !stationInfo.getParkings().get(k).getLatitude().isEmpty())
                                || (stationInfo.getParkings().get(k).getLongitude() != null && !stationInfo.getParkings().get(k).getLongitude().isEmpty())){
                            hasGeocoordinate = true;
                        }
                    }
                    this.stationInfoDetailParkingAdapter.getParkingView(k,this.stationParkingListView);
                }
                if (hasGeocoordinate){
                    llMap.setVisibility(View.VISIBLE);
                }else{
                    llMap.setVisibility(View.GONE);
                }
            }else{
                if (this.stationInfo.getStations() != null && this.stationInfo.getStations().size() > 0){
                    goToInformation(informationTabView);
                }else {
                    goToFacilities(parkingView);
                }
                parkingView.setVisibility(View.GONE);
            }



            if(this.stationInfo.getOpeningHours() != null && !this.stationInfo.getOpeningHours().isEmpty()){
                this.openHoursView.setVisibility(View.VISIBLE);
                this.openHoursView.setText(this.getString(R.string.stationinfo_view_opening_hours) + ":" + this.stationInfo.getOpeningHours());
            }else{
                this.openHoursView.setVisibility(View.GONE);
            }

            this.addressStreetView.setText(this.stationInfo.getStreet() + " " +this.stationInfo.getNumber());
            this.addressCityView.setText(this.stationInfo.getPostalCode() + " " + this.stationInfo.getCity());
            LogUtils.e("Station info", "MoreDetailsURL----------->" + stationInfo.getMoreDetailsURL());
            if(stationInfo.getMoreDetailsURL() != null && !stationInfo.getMoreDetailsURL().isEmpty()){
                btnMoreInfo.setVisibility(View.VISIBLE);
            }else{
                btnMoreInfo.setVisibility(View.GONE);
            }
            GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoDetailActivity.this, "Station_Detail_"+this.stationInfo.getCode());
        }
    }

    public static Intent createIntent(Context context, StationInfo stationInfo) {
        Intent intent = new Intent(context, StationInfoDetailActivity.class);
        intent.putExtra(STATION_INFO_SERIALIZABLE_KEY, stationInfo);
        return intent;
    }

    public void goToFacilities(View view){
        this.informationTabView.setBackgroundResource(R.color.background_secondaryaction);
        this.facilitiesView.setBackgroundResource(R.color.background_group_title);
        this.parkingView.setBackgroundResource(R.color.background_secondaryaction);
        this.stationInformationListView.setVisibility(View.GONE);
        this.stationParkingListView.setVisibility(View.GONE);
        this.stationFacilitiesListView.setVisibility(View.VISIBLE);
    }

    public void goToInformation(View view){
        this.informationTabView.setBackgroundResource(R.color.background_group_title);
        this.facilitiesView.setBackgroundResource(R.color.background_secondaryaction);
        this.parkingView.setBackgroundResource(R.color.background_secondaryaction);
        this.stationInformationListView.setVisibility(View.VISIBLE);
        this.stationParkingListView.setVisibility(View.GONE);
        this.stationFacilitiesListView.setVisibility(View.GONE);
    }

    public void goToParking(View view){
        this.informationTabView.setBackgroundResource(R.color.background_secondaryaction);
        this.facilitiesView.setBackgroundResource(R.color.background_secondaryaction);
        this.parkingView.setBackgroundResource(R.color.background_group_title);
        this.stationInformationListView.setVisibility(View.GONE);
        this.stationParkingListView.setVisibility(View.VISIBLE);
        this.stationFacilitiesListView.setVisibility(View.GONE);
    }

    public void goToStationInfoMap(View view){
        startActivity(StationInfoMapActivity.createIntent(StationInfoDetailActivity.this, this.stationInfo));
    }

    public void openMoreDetails(View view){
        if(this.stationInfo.getMoreDetailsURL()!=null&&!"".equals(this.stationInfo.getMoreDetailsURL())){
            //Utils.openProwser(getApplicationContext(), this.stationInfo.getMoreDetailsURL(), clickToCallService);
            if(NetworkUtils.isOnline(StationInfoDetailActivity.this)) {
                startActivity(WebViewOverlayActivity.createIntent(StationInfoDetailActivity.this,
                        Utils.getUrl(this.stationInfo.getMoreDetailsURL())));
            }
        }else {
            //Toast.makeText(this,"the url is invalid！",Toast.LENGTH_LONG).show();
        }

    }

    public void openPDF(View view){
        File file = stationInfoService.getStationPDF(stationInfo);
        Log.d("openPDF", "openPDF===" + file.getAbsolutePath());
        Log.d("openPDF", "openPDF file length------->" + file.length());
        InputStream is = null;
        String assetFileName = "";
        String existStationCode = new StationInfoDataService().getExistStationCode(stationInfo.getCode());
        if(existStationCode != null){
            assetFileName = stationInfo.getCode() + "_" + settingService.getCurrentLanguagesKey().substring(0,2).toLowerCase() + ".pdf";
        }else{
            assetFileName = stationInfo.getCode() + ".pdf";
        }
        //file = null;
        if (file != null && file.exists() && file.length() > 0) {
            startActivity(PDFViewActivity.createIntent(getApplicationContext(), file, assetFileName, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        } else {
            //file = stationInfoService.getStationFloorPlan(getApplicationContext(), stationInfo.getCode(), settingService.getCurrentLanguagesKey());

            Log.d("openPDF", "openPDF package---assetFileName---->" + assetFileName);

            startActivity(PDFViewActivity.createIntent(getApplicationContext(), null, assetFileName, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

            //Toast.makeText(applicationContext, applicationContext.getString(R.string.alert_status_service_not_available), Toast.LENGTH_SHORT).show();
        }
    }
}
