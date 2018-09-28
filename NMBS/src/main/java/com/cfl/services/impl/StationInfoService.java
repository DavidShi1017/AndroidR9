package com.cfl.services.impl;

import android.content.Context;

import com.cfl.application.NMBSApplication;
import com.cfl.dataaccess.restservice.IStationInfoDataService;
import com.cfl.dataaccess.restservice.impl.StationInfoDataService;
import com.cfl.model.StationInfo;
import com.cfl.model.StationInfoResponse;
import com.cfl.services.ISettingService;
import com.cfl.services.IStationInfoService;

import java.io.File;

/**
 * Created by Richard on 1/25/16.
 */
public class StationInfoService implements IStationInfoService{
    private Context applicationContext;
    private final static String TAG = StationInfoService.class.getSimpleName();
    private StationInfoResponse stationInfoResponse;
    public StationInfoService(Context context){
        this.applicationContext = context;
    }

    public StationInfoResponse getStationInfo(ISettingService settingService, boolean isChangeLanguage){
        IStationInfoDataService stationInfoDataService = new StationInfoDataService();
        //if(stationInfoResponse == null){
        try {
            stationInfoResponse = stationInfoDataService.getStationInfoResponse(applicationContext, settingService.getCurrentLanguagesKey(), isChangeLanguage);
        } catch (Exception e) {
            try {
                stationInfoResponse = stationInfoDataService.getStationInfoResponseInLocal(applicationContext);
                if(stationInfoResponse == null){
                    stationInfoResponse =  stationInfoDataService.storeStationInfo(applicationContext, settingService.getCurrentLanguagesKey());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if(stationInfoResponse != null){
            stationInfoResponse.getStations().get(0).getFloorPlanDownloadURL();
        }
        // }
        return stationInfoResponse;
    }

    public StationInfoResponse getStationInfoResponseInLocal(Context context){
        IStationInfoDataService stationInfoDataService = new StationInfoDataService();
        //if(stationInfoResponse == null){
        try {
            stationInfoResponse = stationInfoDataService.getStationInfoResponseInLocal(context);
            if(stationInfoResponse == null){
                stationInfoResponse = stationInfoDataService.storeStationInfo(applicationContext,
                        NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            }
        }catch (Exception e){
            try {
                stationInfoResponse = stationInfoDataService.storeStationInfo(applicationContext,
                        NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        // }
        return stationInfoResponse;
    }

    public File getStationPDF(StationInfo stationInfo){
        IStationInfoDataService stationInfoDataService = new StationInfoDataService();
        File file = stationInfoDataService.getStationPdf(applicationContext, stationInfo);
        return file;
    }
    public File getStationFloorPlan(Context context, String stationCode, String language){
        IStationInfoDataService stationInfoDataService = new StationInfoDataService();
        File file = stationInfoDataService.getStationFloorPlan(context, stationCode, language);
        return file;
    }
    public boolean isAssetStationPDFAvailable(Context context, String stationCode, String language){
        IStationInfoDataService stationInfoDataService = new StationInfoDataService();
        return stationInfoDataService.isAssetStationPDFAvailable(context, stationCode, language);
    }
}