package com.cfl.services;

import com.cfl.model.AdditionalScheduleQueryParameter;
import com.cfl.model.ExtensionScheduleQuery;
import com.cfl.model.RealTimeConnection;
import com.cfl.model.ScheduleDetailRefreshModel;
import com.cfl.model.ScheduleQuery;
import com.cfl.model.TrainIcon;
import com.cfl.services.impl.AsyncScheduleResponse;

import java.util.List;

/**
 * Created by Richard on 3/8/16.
 */
public interface IScheduleService {
    public RealTimeConnection refreshScheduleDetail(ScheduleDetailRefreshModel scheduleDetailRefreshModel, ISettingService settingService);
    public AsyncScheduleResponse searchSchedule(ScheduleQuery scheduleQuery, ISettingService settingService);
    public AsyncScheduleResponse searchScheduleTrains(AdditionalScheduleQueryParameter additionalScheduleQueryParameter,ISettingService settingService);
    public boolean insertLastQuery(String scheduleQueryID, String originCode, String desCode,
                                String viaCode, String dateTime, String timePreference, String trainNr,String originName, String desName, String viaName);
    public ExtensionScheduleQuery getLastQuery(String scheduleQueryID);
    public List<TrainIcon> getTrainIcons();
}
