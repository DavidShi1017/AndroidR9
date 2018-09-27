package com.cfl.services.impl;

import android.content.Context;

import com.cfl.dataaccess.database.ScheduleQueryDataBaseService;
import com.cfl.dataaccess.database.TrainIconsDatabaseService;
import com.cfl.dataaccess.restservice.IScheduleDataService;
import com.cfl.dataaccess.restservice.impl.ScheduleDataService;
import com.cfl.model.AdditionalScheduleQueryParameter;
import com.cfl.model.ExtensionScheduleQuery;
import com.cfl.model.RealTimeConnection;
import com.cfl.model.RealTimeConnectionResponse;
import com.cfl.model.ScheduleDetailRefreshModel;
import com.cfl.model.ScheduleQuery;
import com.cfl.model.TrainIcon;
import com.cfl.services.IScheduleService;
import com.cfl.services.ISettingService;

import java.util.List;

/**
 * Created by Richard on 3/8/16.
 */
public class ScheduleService implements IScheduleService {
    private Context applicationContext;

    public ScheduleService(Context context) {
        this.applicationContext = context;
    }

    public AsyncScheduleResponse searchSchedule(ScheduleQuery scheduleQuery,ISettingService settingService){
            AsyncScheduleResponse asyncScheduleResponse = new AsyncScheduleResponse();
            asyncScheduleResponse.registerReceiver(applicationContext);
            ScheduleIntentService.startService(applicationContext, scheduleQuery, settingService.getCurrentLanguagesKey());
            return asyncScheduleResponse;
    }

    public AsyncScheduleResponse searchScheduleTrains(AdditionalScheduleQueryParameter additionalScheduleQueryParameter,ISettingService settingService){
        AsyncScheduleResponse asyncScheduleResponse = new AsyncScheduleResponse();
        asyncScheduleResponse.registerReceiver(applicationContext);
        ScheduleTrainsIntentService.startService(applicationContext, additionalScheduleQueryParameter, settingService.getCurrentLanguagesKey());
        return asyncScheduleResponse;
    }

    public RealTimeConnection refreshScheduleDetail(ScheduleDetailRefreshModel scheduleDetailRefreshModel, ISettingService settingService) {
        IScheduleDataService scheduleDataService = new ScheduleDataService();
        RealTimeConnection realTimeConnection = null;
        try {
            RealTimeConnectionResponse realTimeConnectionResponse = scheduleDataService.refreshScheduleDetail(applicationContext, settingService.getCurrentLanguagesKey(), scheduleDetailRefreshModel, settingService);
            realTimeConnection = realTimeConnectionResponse.getRealTimeConnection();
        } catch (Exception e) {

        }
        return realTimeConnection;
    }

    public boolean insertLastQuery(String scheduleQueryID, String originCode, String desCode,
                                String viaCode, String dateTime, String timePreference, String trainNr,String originName,String desName, String viaName){
        ScheduleQueryDataBaseService scheduleQueryDataBaseService = new ScheduleQueryDataBaseService(applicationContext);
        return scheduleQueryDataBaseService.insertScheduleQuery(scheduleQueryID,originCode,desCode,viaCode,dateTime,timePreference,trainNr,originName,desName, viaName);
    }
    public ExtensionScheduleQuery getLastQuery(String scheduleQueryID){
        ScheduleQueryDataBaseService scheduleQueryDataBaseService = new ScheduleQueryDataBaseService(applicationContext);
        return scheduleQueryDataBaseService.readScheduleQuery(scheduleQueryID);
    }

    public List<TrainIcon> getTrainIcons(){
        TrainIconsDatabaseService trainIconsDatabaseService = new TrainIconsDatabaseService(applicationContext);
        return trainIconsDatabaseService.selectTrainIcons();
    }
}
