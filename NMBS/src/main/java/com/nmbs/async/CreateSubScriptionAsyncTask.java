package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CreateSubscriptionParameter;
import com.nmbs.model.HafasUser;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.preferences.SettingsPref;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.DateUtils;

public class CreateSubScriptionAsyncTask extends AsyncTask<Void, Void, Void>{
	private IPushService pushService;
	private Context mContext;
	private String language;
	private RealTimeConnection realTimeConnection;
	private static final String TAG = CreateSubScriptionAsyncTask.class.getSimpleName();
	public CreateSubScriptionAsyncTask(IPushService pushService, RealTimeConnection realTimeConnection,String language, Context mContext){
		this.mContext = mContext;
		this.language = language;
		this.pushService = pushService;
		this.realTimeConnection = realTimeConnection;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			HafasUser hafasUser = pushService.getUser();
			if(hafasUser==null||"".equals(hafasUser.getUserId())){
				String userId = pushService.retryCreateUser(language);
				LogUtils.d(TAG, "createSubscription ...." + userId);
				if("".equals(userId)){
					Intent broadcastIntent = new Intent(ServiceConstant.PUSH_CREATE_SUBSCRIPTION_ACTION);
					broadcastIntent.putExtra(ActivityConstant.CREATE_SUBSCRIPTION_RESULT,"");
					mContext.sendBroadcast(broadcastIntent);
				}else{
					hafasUser = pushService.getUser();
					executeCreateAction(hafasUser);
				}

			}else{
				LogUtils.d(TAG, "createSubscription ...." + hafasUser.getUserId());
				executeCreateAction(hafasUser);
			}
		}catch (Exception e){
			return null;
		}
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}

	private void executeCreateAction(HafasUser hafasUser){
		String subScriptionId = "";
		/*ifString notificationTime = SettingsPref.getStartNotifiTime(mContext);
		f(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
			notificationTime = notificationTime.replace("min.","").trim();
		}else{
			notificationTime = "5";
		}

		String minDelay = SettingsPref.getDelayNotifiTime(mContext);
		if(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
			minDelay = notificationTime.replace("min.","").trim();
		}else{
			minDelay = "5";
		}*/
		int start = NMBSApplication.getInstance().getSettingService().getStartNotifiTimeIntger();
		int delay = NMBSApplication.getInstance().getSettingService().getDelayNotifiTimeIntger();
		CreateSubscriptionParameter subScription = new CreateSubscriptionParameter(hafasUser.getUserId(),
				this.realTimeConnection.getReconCtx(), DateUtils.dateToString(this.realTimeConnection.getDeparture()),
				DateUtils.dateToString(this.realTimeConnection.getDeparture()), delay, delay,
				start, hafasUser.getRegisterId(), 1);

		try {
			subScriptionId = this.pushService.createSubScription(subScription);
			if("".equals(subScriptionId)){   // retry
				subScriptionId = this.pushService.createSubScription(subScription);
			}
		}catch (Exception e){
			subScriptionId = "";
		}finally {
			if (mContext != null) {
				//Log.d(TAG, "createSubscription send broadcast....");
				Intent broadcastIntent = new Intent(ServiceConstant.PUSH_CREATE_SUBSCRIPTION_ACTION);
				broadcastIntent.putExtra(ActivityConstant.CREATE_SUBSCRIPTION_RESULT,subScriptionId);
				mContext.sendBroadcast(broadcastIntent);

				Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
				mContext.sendBroadcast(alertCountIntent);
			}
		}
	}
}
