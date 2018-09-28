package com.cfl.services;

import android.content.Context;

import com.cfl.model.StationInfo;
import com.cfl.model.StationInfoResponse;

import java.io.File;

/**
 * Created by Richard on 1/25/16.
 */
public interface IStationInfoService {
    public StationInfoResponse getStationInfo(ISettingService settingService, boolean isChangeLanguage) throws Exception;
    public File getStationPDF(StationInfo stationInfo);
    public StationInfoResponse getStationInfoResponseInLocal(Context context);
    public File getStationFloorPlan(Context context, String stationCode, String language);
    public boolean isAssetStationPDFAvailable(Context context, String stationCode, String language);;
}
