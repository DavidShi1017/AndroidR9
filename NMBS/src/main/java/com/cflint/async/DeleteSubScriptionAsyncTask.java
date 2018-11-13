package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;


import com.cflint.model.HafasUser;
import com.cflint.services.IPushService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.util.ActivityConstant;

public class DeleteSubScriptionAsyncTask extends AsyncTask<Void, Void, Void>{
	private IPushService pushService;
	private Context mContext;
	private String language;
	private String subscriptionId;
	private static final String TAG = DeleteSubScriptionAsyncTask.class.getSimpleName();
	public DeleteSubScriptionAsyncTask(IPushService pushService, String language, Context mContext,String subscriptionId){
		this.mContext = mContext;
		this.language = language;
		this.pushService = pushService;
		this.subscriptionId = subscriptionId;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		//Log.e(TAG, "DeleteSubScriptionAsyncTask doInBackground....");
		boolean isSuccess = false;
		try {
			HafasUser hafasUser = pushService.getUser();
			if(hafasUser!=null&&!"".equals(hafasUser.getUserId())){
				isSuccess = this.pushService.deleteSubscription(hafasUser.getUserId(),subscriptionId);
				if(!isSuccess){
					isSuccess = this.pushService.deleteSubscription(hafasUser.getUserId(),subscriptionId);
				}
				if(isSuccess){
					this.pushService.deleteSubscriptionInLocal(subscriptionId);
				}
			}else{
				String userId = pushService.retryCreateUser(language);
				if(!"".equals(userId)){
					isSuccess = this.pushService.deleteSubscription(userId,subscriptionId);
					if(!isSuccess){
						isSuccess = this.pushService.deleteSubscription(userId,subscriptionId);
					}
					if(isSuccess){
						this.pushService.deleteSubscriptionInLocal(subscriptionId);
					}
				}
			}

		}catch (Exception e){
			isSuccess = false;
		}finally {
			//Log.e(TAG, "DeleteSubScriptionAsyncTask doInBackground....");
			if (mContext != null) {
				//Log.d(TAG, "createSubscription send broadcast....");
				Intent broadcastIntent = new Intent(ServiceConstant.PUSH_DELETE_SUBSCRIPTION_ACTION);
				broadcastIntent.putExtra(ActivityConstant.DELETE_SUBSCRIPTION_RESULT,isSuccess);
				mContext.sendBroadcast(broadcastIntent);

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
