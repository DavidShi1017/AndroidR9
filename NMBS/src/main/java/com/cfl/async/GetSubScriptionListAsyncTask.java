package com.cfl.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;


import android.widget.Toast;

import com.cfl.application.NMBSApplication;
import com.cfl.model.HafasUser;
import com.cfl.services.IPushService;
import com.cfl.services.impl.ServiceConstant;

public class GetSubScriptionListAsyncTask extends AsyncTask<Void, Void, Void>{
	private IPushService pushService;
	private Context mContext;
	private String language;
	public static boolean isAlertSubscriptionFinished = false;
	private static final String TAG = GetSubScriptionListAsyncTask.class.getSimpleName();
	public GetSubScriptionListAsyncTask(IPushService pushService, String language, Context mContext){
		this.mContext = mContext;
		this.language = language;
		this.pushService = pushService;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			isAlertSubscriptionFinished = false;
			HafasUser hafasUser = pushService.getUser();
			//Log.e(TAG, "getSubscriptionsByUserId===");
			if(hafasUser==null||"".equals(hafasUser.getUserId())){
				String userId = pushService.retryCreateUser(language);
				if(!"".equals(userId)){
					boolean result = this.pushService.getSubscriptionsByUserId(hafasUser.getUserId());
					if(!result){
						this.pushService.getSubscriptionsByUserId(hafasUser.getUserId());
					}
				}else{
					//Toast.makeText(mContext,"Get UserId Failed!",Toast.LENGTH_LONG).show();
					isAlertSubscriptionFinished = true;
				}

			}else{
				if(NMBSApplication.getInstance().getTestService().isDelaySubscription()){
					Thread.sleep(1800);
				}

				boolean result = this.pushService.getSubscriptionsByUserId(hafasUser.getUserId());
				if(!result){
					this.pushService.getSubscriptionsByUserId(hafasUser.getUserId());
				}
			}
			isAlertSubscriptionFinished = true;

		}catch (Exception e){
			e.printStackTrace();
			isAlertSubscriptionFinished = true;
		}finally {
			isAlertSubscriptionFinished = true;
			if (mContext != null) {
				Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
				mContext.sendBroadcast(alertCountIntent);
			}
		}
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}
}
