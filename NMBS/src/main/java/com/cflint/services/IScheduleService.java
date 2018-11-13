package com.cflint.services;

import com.cflint.model.AdditionalScheduleQueryParameter;
import com.cflint.model.ExtensionScheduleQuery;
import com.cflint.model.RealTimeConnection;
import com.cflint.model.ScheduleDetailRefreshModel;
import com.cflint.model.ScheduleQuery;
import com.cflint.model.TrainIcon;
import com.cflint.services.impl.AsyncScheduleResponse;

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
