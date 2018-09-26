package com.nmbs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nmbs.R;
import com.nmbs.adapter.StationInfoMapItemAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.model.Parking;
import com.nmbs.model.StationInfo;
import com.nmbs.model.StationMapInfo;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.AppLanguageUtils;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class StationInfoMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private final static String STATION_INFO_MAP_SERIALIZABLE_KEY = "STATION_INFO_MAP";
    private GoogleMap mMap;
    private StationInfo stationInfo;
    private TextView mapTitle;
    private ListView stationListView;
    private StationInfoMapItemAdapter stationInfoMapItemAdapter;
    private List<StationMapInfo> stationMapInfoList;
    private SettingService settingService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        setContentView(R.layout.activity_station_info_map);
        bindAllViewElements();
        getIntentValue();
        init();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.station_info_map);
        mapFragment.getMapAsync(this);

    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
    }
    private void bindAllViewElements(){
        this.mapTitle = (TextView) findViewById(R.id.tv_station_info_map_title);
        this.stationListView = (ListView) findViewById(R.id.lv_station_info_map_parking_list);
    }

    public void backToOverView(View view){
        finish();
    }

    private void getIntentValue(){
        this.stationInfo = (StationInfo) getIntent().getSerializableExtra(STATION_INFO_MAP_SERIALIZABLE_KEY);
    }

    private void init(){
        this.mapTitle.setText(this.stationInfo.getName());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        stationMapInfoList = new ArrayList<StationMapInfo>();
        LatLng stationLocation = null;
        double latitude = convertStringToDouble(this.stationInfo.getLatitude());
        double longitude = convertStringToDouble(this.stationInfo.getLongitude());
        //Log.e("latitude", "latitude..." + latitude);
        //Log.e("longitude", "longitude..." + longitude);
        if(latitude != 0 && longitude != 0){
            stationMapInfoList.add(new StationMapInfo(this.stationInfo.getName(),this.stationInfo.getStreet() + " "
                    + this.stationInfo.getNumber() + " " + this.stationInfo.getPostalCode()
                    + " " + this.stationInfo.getCity() + " " + this.stationInfo.getCountry(), false, latitude, longitude));
            stationLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(stationLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.stationinfo_train)));
        }
        for(int i=0;i<this.stationInfo.getParkings().size();i++){
            Parking parking = this.stationInfo.getParkings().get(i);
            if(parking != null){
                double latitudeParking = convertStringToDouble(parking.getLatitude());
                double longitudeParking = convertStringToDouble(parking.getLongitude());
                if(latitudeParking != 0 && longitudeParking != 0){
                    stationMapInfoList.add(new StationMapInfo(parking.getTitle(), parking.getAddress(), true, latitudeParking, longitudeParking));
                    stationLocation = new LatLng(latitudeParking, longitudeParking);
                    mMap.addMarker(new MarkerOptions().position(stationLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.stationinfo_parking)));
                }
            }
        }

        stationInfoMapItemAdapter = new StationInfoMapItemAdapter(this,stationMapInfoList);
        this.stationListView.setDivider(null);
        this.stationListView.setAdapter(stationInfoMapItemAdapter);
        this.stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(stationMapInfoList.get(position).getLatitude(),
                        stationMapInfoList.get(position).getLongitude())));
            }
        });
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        if(stationLocation!=null)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stationLocation));
        GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoMapActivity.this, "Station_Map_"+this.stationInfo.getCode());
    }

    public static Intent createIntent(Context context, StationInfo stationInfo) {
        Intent intent = new Intent(context, StationInfoMapActivity.class);
        intent.putExtra(STATION_INFO_MAP_SERIALIZABLE_KEY, stationInfo);
        return intent;
    }

    private double convertStringToDouble(String value){
        try{
            if(value != null && !"".equals(value)){
                int firstDot = 0;
                firstDot = value.indexOf(".");
                value = value.replace(".", "");
                StringBuilder  sb = new StringBuilder (value);
                sb.insert(firstDot, ".");
                return Double.parseDouble(sb.toString());
            }
        }catch (Exception e){
            return 0;
        }
        return 0;
    }
}
