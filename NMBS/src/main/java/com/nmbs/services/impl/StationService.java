package com.nmbs.services.impl;

import android.content.Context;
import android.util.Log;


import com.nmbs.R;
import com.nmbs.activities.ScheduleSearchActivity;
import com.nmbs.dataaccess.converters.MasterResponseConverter;
import com.nmbs.dataaccess.database.FavoriteStationsDatabaseService;
import com.nmbs.dataaccess.restservice.IStationInfoDataService;
import com.nmbs.dataaccess.restservice.impl.StationInfoDataService;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.model.Station;
import com.nmbs.model.StationInfo;
import com.nmbs.model.StationInfoResponse;
import com.nmbs.model.StationResponse;
import com.nmbs.services.ISettingService;
import com.nmbs.services.IStationInfoService;
import com.nmbs.util.FileManager;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Richard on 1/25/16.
 */
public class StationService{
    private Context applicationContext;
    private final static String TAG = StationService.class.getSimpleName();
    private StationInfoResponse stationInfoResponse;

    public StationService(Context context){
        this.applicationContext = context;
    }

    private List<Station> stationList = null;
    public List<Station> readHafasStations(String language) throws JSONException, InvalidJsonError {

        if(stationList == null){
            MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
            int resourcesId = 0;

            if (StringUtils.equalsIgnoreCase(language, "EN_GB")) {
                resourcesId = R.raw.station_en;
            }else if(StringUtils.equalsIgnoreCase(language, "FR_BE")){
                resourcesId = R.raw.station_fr;
            }else if(StringUtils.equalsIgnoreCase(language, "NL_BE")){
                resourcesId = R.raw.station_nl;
            }else {resourcesId = R.raw.station_de;
            }

            InputStream is = applicationContext.getResources().openRawResource(resourcesId);
            String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
            Log.e("Converter", "start.....");
            StationResponse stationResponse = masterResponseConverter.parseStation(stringHttpResponse);
            Log.e("Converter", "end.....");
            if (stationResponse != null) {
                stationList = stationResponse.getStations();

            }
        }

        return stationList;
    }

    public List<Station> readExtrastations(String language) throws JSONException, InvalidJsonError {
        List<Station> stations = new ArrayList<>();

            MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
            int resourcesId = 0;

            if (StringUtils.equalsIgnoreCase(language, "EN_GB")) {
                resourcesId = R.raw.station_null_en;
            }else if(StringUtils.equalsIgnoreCase(language, "FR_BE")){
                resourcesId = R.raw.station_null_fr;
            }else if(StringUtils.equalsIgnoreCase(language, "NL_BE")){
                resourcesId = R.raw.station_null_nl;
            }else {resourcesId = R.raw.station_null_de;
            }

            InputStream is = applicationContext.getResources().openRawResource(resourcesId);
            String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);

            StationResponse stationResponse = masterResponseConverter.parseStation(stringHttpResponse);

            if (stationResponse != null) {
                stations = stationResponse.getStations();

            }
        return stations;
    }


    public Station getStation(String code, String language){
        try {
            List<Station> stations = readHafasStations(language);
            for(Station station : stations){
                if(station != null && station.getCode() != null && station.getCode().equalsIgnoreCase(code)){
                    return station;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidJsonError invalidJsonError) {
            invalidJsonError.printStackTrace();
        }
        return null;
    }
    public Station getStationExtra(String code, String language){
        try {
            List<Station> stations = readExtrastations(language);
            for(Station station : stations){
                if(station != null && station.getCode() != null && station.getCode().equalsIgnoreCase(code)){
                    return station;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidJsonError invalidJsonError) {
            invalidJsonError.printStackTrace();
        }
        return null;
    }
    public void getAllStation(String language){
        if(stationList == null){
            try {
                readHafasStations(language);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InvalidJsonError invalidJsonError) {
                invalidJsonError.printStackTrace();
            }
        }
    }

    public void cleanAllStation(){
        stationList = null;
    }

    public List<Station> getStationWithoutFavorite(String language, List<Station> allStation){
        List<String> favoriteCodes = getStationFavorite();
        List<Station> noFavoriteStation = new ArrayList<>();
        FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(applicationContext);
        //boolean isExist = false;
        //Log.e("FavoriteStation", "getStationWithoutFavorite  allStation siaze ..." + allStation.size());
        //Log.e("FavoriteStation", "getStationWithoutFavorite  favoriteCodes siaze ..." + favoriteCodes.size());
        for(Station station : allStation){
            boolean isExist = false;
            //isExist = database.isExistStationFavorite(station.getCode());
            for(String favoriteCode : favoriteCodes){
                if(station.getCode().equalsIgnoreCase(favoriteCode)){
                    isExist = true;
                }
            }
            if (!isExist){
                noFavoriteStation.add(station);
            }
        }
        return noFavoriteStation;
    }

    public void addStationFavorite(String code){
        //Log.e("FavoriteStation", "addStationFavorite..." + code);
        FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(applicationContext);
        boolean isExist = database.isExistStationFavorite(code);
        //Log.e("FavoriteStation", "isExist..." + isExist);
        if(!isExist){
            database.insertStationCode(code);
        }
    }

    public void deleteStationFavorite(String code){
        FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(applicationContext);
        database.deleteStationCode(code);
    }

    public List<String> getStationFavorite(){
        FavoriteStationsDatabaseService database = new FavoriteStationsDatabaseService(applicationContext);
        List<String> favoriteStationsCode = database.readStationCodeList();
        return favoriteStationsCode;
    }

    public List<Station> getStationFavorite(List<Station> allStation) {
        List<Station> favoriteStation = new ArrayList<>();
        List<String> favoriteCodes = getStationFavorite();
        //Log.e("FavoriteStation", "favoriteCodes size ..." + favoriteCodes.size());
        //Log.e("FavoriteStation", "allStation size ..." + allStation.size());
        for (Station station : allStation) {
            for (String favoriteCode : favoriteCodes) {
                if (station != null && station.getCode() != null
                        && station.getCode().equalsIgnoreCase(favoriteCode)) {
                    favoriteStation.add(station);
                }
            }
        }
        return favoriteStation;
    }

    public void filterFavorite(List<Station> stationFavorite, boolean isFromSchedule, int selectionFlag,
                               String lastSelectedFromStationCode, String lastSelectedToStationCode, String lastSelectedViaStationCode){
        if(stationFavorite != null){
            //Log.e("FavoriteStation", "FavoriteStation siaze ..." + stationFavorite.size());
            Iterator<Station> iterator = stationFavorite.iterator();
            while(iterator.hasNext()){
                Station station  = iterator.next();
                if(isFromSchedule){
                    if(selectionFlag == ScheduleSearchActivity.REQUEST_FROM_STATION){
                        if(station.getCode().equals(lastSelectedToStationCode)  || station.getCode().equals(lastSelectedViaStationCode)){
                            iterator.remove();
                        }
                    }else if(selectionFlag == ScheduleSearchActivity.REQUEST_TO_STATION){
                        if(station.getCode().equals(lastSelectedFromStationCode)  || station.getCode().equals(lastSelectedViaStationCode)){
                            iterator.remove();
                        }
                    }else if(selectionFlag == ScheduleSearchActivity.REQUEST_TO_STATION){
                        if(station.getCode().equals(lastSelectedFromStationCode)  || station.getCode().equals(lastSelectedToStationCode)){
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

}
