package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.cflint.application.NMBSApplication;
import com.cflint.model.CreateSubscriptionParameter;
import com.cflint.model.HafasUser;
import com.cflint.model.RealTimeConnection;
import com.cflint.preferences.SettingsPref;
import com.cflint.services.IPushService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.util.ActivityConstant;
import com.cflint.util.DateUtils;

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
			Log.e(TAG, "createSubscription ...." + hafasUser);
			if(hafasUser==null||"".equals(hafasUser.getUserId())){
				String userId = pushService.retryCreateUser(language);
				Log.e(TAG, "createSubscription ...." + userId);
				if("".equals(userId)){
					Intent broadcastIntent = new Intent(ServiceConstant.PUSH_CREATE_SUBSCRIPTION_ACTION);
					broadcastIntent.putExtra(ActivityConstant.CREATE_SUBSCRIPTION_RESULT,"");
					mContext.sendBroadcast(broadcastIntent);
				}else{
					hafasUser = pushService.getUser();
					executeCreateAction(hafasUser);
				}

			}else{
				Log.d(TAG, "createSubscription ...." + hafasUser.getUserId());
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
