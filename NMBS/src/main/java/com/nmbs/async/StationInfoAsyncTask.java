package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.nmbs.model.StationInfoResponse;
import com.nmbs.services.ISettingService;
import com.nmbs.services.IStationInfoService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.ActivityConstant;

public class StationInfoAsyncTask extends AsyncTask<Void, Void, Void>{
	private IStationInfoService stationInfoService;
	private ISettingService settingService;
	public static boolean isStationInfoFinished;
	private Context mContext;
	private StationInfoResponse stationInfoResponse;
	private boolean isChangeLanguage;
	public StationInfoAsyncTask(IStationInfoService messageService, ISettingService settingService,Context mContext, boolean isChangeLanguage){
		this.stationInfoService = messageService;
		this.settingService = settingService;
		this.mContext = mContext;
		this.isChangeLanguage = isChangeLanguage;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {

		downlaodStationInfo();
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}

	public void downlaodStationInfo(){
		try {
			stationInfoResponse = stationInfoService.getStationInfo(settingService, isChangeLanguage);
			isStationInfoFinished = false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			isStationInfoFinished = true;
			if (mContext != null) {
				Intent broadcastIntent = new Intent(ServiceConstant.STATION_INFO_SERVICE_ACTION);
				broadcastIntent.putExtra(ActivityConstant.STATION_INFO_RESPONSE, stationInfoResponse);
				mContext.sendBroadcast(broadcastIntent);
			}
		}
	}
}
