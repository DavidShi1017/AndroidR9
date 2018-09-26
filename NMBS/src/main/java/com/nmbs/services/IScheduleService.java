package com.nmbs.services;

import com.nmbs.model.AdditionalScheduleQueryParameter;
import com.nmbs.model.ExtensionScheduleQuery;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.ScheduleDetailRefreshModel;
import com.nmbs.model.ScheduleQuery;
import com.nmbs.model.TrainIcon;
import com.nmbs.services.impl.AsyncScheduleResponse;

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
