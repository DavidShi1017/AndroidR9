package com.cflint.dataaccess.restservice;

import android.content.Context;

import com.cflint.model.StationInfo;
import com.cflint.model.StationInfoResponse;

import java.io.File;

/**
 * Created by Richard on 1/25/16.
 */
public interface IStationInfoDataService {
    public StationInfoResponse getStationInfoResponse(Context context, String language, boolean isChangeLanguage) throws Exception;
    public void storeDefaultData(Context context);
    public File getStationPdf(Context context, StationInfo stationInfo);
    public StationInfoResponse getStationInfoResponseInLocal(Context context)throws Exception;
    public StationInfoResponse storeStationInfo( Context context, String language) throws Exception;
    public File getStationFloorPlan(Context context, String stationCode, String language);
    public boolean isAssetStationPDFAvailable(Context context, String stationCode, String language);;
}
