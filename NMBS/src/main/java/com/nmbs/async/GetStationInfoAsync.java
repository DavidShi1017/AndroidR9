package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.nmbs.model.StationInfoResponse;
import com.nmbs.services.ISettingService;
import com.nmbs.services.IStationInfoService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.ActivityConstant;

/**
 * Created by Richard on 4/18/16.
 */
public class GetStationInfoAsync extends AsyncTask<Void, Void, Void> {
    private IStationInfoService stationInfoService;
    private ISettingService settingService;
    private Context mContext;
    public static boolean isFinished = true;
    private static final String TAG = ScheduleRefreshAsyncTask.class.getSimpleName();
    public GetStationInfoAsync(IStationInfoService stationInfoService, ISettingService settingService, Context mContext){
        this.stationInfoService = stationInfoService;
        this.settingService = settingService;
        this.mContext = mContext;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        StationInfoResponse stationInfoResponse = null;
        isFinished = false;
        try {
            stationInfoResponse = stationInfoService.getStationInfo(settingService, false);
        } catch (Exception e) {
            isFinished = true;
            e.printStackTrace();
        }finally{
            if (mContext != null) {
                isFinished = true;
                Intent broadcastIntent = new Intent(ServiceConstant.STATION_INFO_SERVICE_ACTION);
                broadcastIntent.putExtra(ActivityConstant.STATION_INFO_RESPONSE, stationInfoResponse);
                mContext.sendBroadcast(broadcastIntent);
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);
    }
}
