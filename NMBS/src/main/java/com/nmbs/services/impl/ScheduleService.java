package com.nmbs.services.impl;

import android.content.Context;

import com.nmbs.dataaccess.database.ScheduleQueryDataBaseService;
import com.nmbs.dataaccess.database.TrainIconsDatabaseService;
import com.nmbs.dataaccess.restservice.IScheduleDataService;
import com.nmbs.dataaccess.restservice.impl.ScheduleDataService;
import com.nmbs.model.AdditionalScheduleQueryParameter;
import com.nmbs.model.ExtensionScheduleQuery;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.RealTimeConnectionResponse;
import com.nmbs.model.ScheduleDetailRefreshModel;
import com.nmbs.model.ScheduleQuery;
import com.nmbs.model.TrainIcon;
import com.nmbs.services.IScheduleService;
import com.nmbs.services.ISettingService;

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
