package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cflint.model.RealTimeConnection;
import com.cflint.model.ScheduleDetailRefreshModel;
import com.cflint.services.IScheduleService;
import com.cflint.services.ISettingService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.util.ActivityConstant;

public class ScheduleRefreshAsyncTask extends AsyncTask<ScheduleDetailRefreshModel, Void, Void>{
	private IScheduleService scheduleService;
	private ISettingService settingService;
	private Context mContext;
	private boolean serviceStatus;
	private RealTimeConnection realTimeConnection;
	private static final String TAG = ScheduleRefreshAsyncTask.class.getSimpleName();
	public ScheduleRefreshAsyncTask(IScheduleService scheduleService, ISettingService settingService, Context mContext){
		this.scheduleService = scheduleService;
		this.settingService = settingService;
		this.mContext = mContext;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(ScheduleDetailRefreshModel... params) {
		refreshMessages(params[0]);
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}

	public boolean refreshMessages(ScheduleDetailRefreshModel scheduleDetailRefreshModel){
		try {
			realTimeConnection = scheduleService.refreshScheduleDetail(scheduleDetailRefreshModel, settingService);
			//System.out.println(realTimeConnection.getOriginStationName()+"$$$$$$$$$$"+realTimeConnection.getDestinationStationName());
			serviceStatus = true;
		} catch (Exception e) {
			serviceStatus = false;
			e.printStackTrace();
		}finally{
			if (mContext != null) {
				Intent broadcastIntent = new Intent(ServiceConstant.SCHEDULE_DETAIL_SERVICE_ACTION);
				broadcastIntent.putExtra(ActivityConstant.SCHEDULE_QUERY_REFRESH, realTimeConnection);
				mContext.sendBroadcast(broadcastIntent);
			}
		}
		return serviceStatus;
	}
}
