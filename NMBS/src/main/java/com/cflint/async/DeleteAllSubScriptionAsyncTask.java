package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;


import com.cflint.application.NMBSApplication;
import com.cflint.model.DossierSummary;
import com.cflint.model.HafasUser;
import com.cflint.model.Subscription;
import com.cflint.model.SubscriptionResponse;
import com.cflint.model.SubscriptionResponseResult;
import com.cflint.services.IPushService;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.util.ActivityConstant;

import java.util.List;

public class DeleteAllSubScriptionAsyncTask extends AsyncTask<Void, Void, Void>{
	public final static int Need_To_Clear_Subscription_Dnr = 1; //Delete dnr
	public final static int Need_To_Delete_Subscription = 2; // Disable
	public final static int Not_Need_Handle_Subscription = 0; // Refresh

	private IPushService pushService;
	private Context mContext;
	private String language;
	private List<Subscription> subscriptionList;
	private boolean isDnrReference = true;
	private List<String> dnrList;
	public int deleteOrClearAction;
	private boolean isCallback;
	private static final String TAG = DeleteAllSubScriptionAsyncTask.class.getSimpleName();
	public DeleteAllSubScriptionAsyncTask(List<Subscription> subscriptionList,IPushService pushService, String language,
										  Context mContext,boolean isDnrReference,List<String> dnrList,int deleteOrClearAction, boolean isCallback){
		this.mContext = mContext;
		this.language = language;
		this.pushService = pushService;
		this.subscriptionList = subscriptionList;
		this.isDnrReference = isDnrReference;
		this.dnrList = dnrList;
		this.deleteOrClearAction = deleteOrClearAction;
		this.isCallback = isCallback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		HafasUser hafasUser = pushService.getUser();
		int result = 0;
		if(hafasUser!=null&&!"".equals(hafasUser.getUserId())){
			 result = executeAction(hafasUser.getUserId());
			if(result == 0){
				result = executeAction(hafasUser.getUserId());
			}
		}else{
			try {
				String userId = pushService.retryCreateUser(language);
				if(!"".equals(userId)){
					result = executeAction(userId);
					if(result == 0){
						result = executeAction(userId);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		//Log.d(TAG, "successFlag..." + result);
		if(isCallback){
			Intent broadcastIntent = new Intent(ServiceConstant.PUSH_DELETE_SUBSCRIPTION_ACTION);
			broadcastIntent.putExtra(ActivityConstant.DELETE_SUBSCRIPTION_RESULT, result);
			mContext.sendBroadcast(broadcastIntent);
		}
		Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
		mContext.sendBroadcast(alertCountIntent);
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}

	public int executeAction(String userId){
		int successFlag = 0;
		boolean isSuccess = false;
		boolean isFailed = false;
		try {
			if(NMBSApplication.getInstance().getTestService().isDeleteSubscription()){
				return 0;
			}
			SubscriptionResponseResult subscriptionResponseResult = this.pushService.deleteAllSubscription(userId, this.subscriptionList);
			for(SubscriptionResponse subscriptionResponse:subscriptionResponseResult.getSubscriptionResponses()){
				if(subscriptionResponse.isSuccess()){
					isSuccess = true;
					this.pushService.deleteSubscriptionInLocal(subscriptionResponse.getSubscriptionId());
				}else if(subscriptionResponse.getErrorType() == SubscriptionResponse.ErrorType.NOSUCHSUBSCRIPTION){
					if(deleteOrClearAction == Need_To_Delete_Subscription){
						this.pushService.deleteSubscriptionInLocal(subscriptionResponse.getSubscriptionId());
					}
					isSuccess = true;
				}else{
					if(deleteOrClearAction == Need_To_Clear_Subscription_Dnr){
						this.pushService.clearSubscriptionDnrById(subscriptionResponse.getSubscriptionId());
					}
					isFailed = true;
				}
			}
			if(isDnrReference&&subscriptionResponseResult.isSuccess()){
				DossierDetailsService dossierDetailDataService = NMBSApplication.getInstance().getDossierDetailsService();
				for(String dnr:dnrList){
					DossierSummary dossierSummary = dossierDetailDataService.getDossier(dnr);
					if(dossierSummary != null){
						dossierSummary.setDossierPushEnabled(false);
						//Log.d("PushEnabled", "PushEnabled..." + dossierSummary.isDossierPushEnabled());
						dossierDetailDataService.updateDossier(dossierSummary);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();

		}finally {
			if (mContext != null) {
				if(isFailed&&isSuccess){
					successFlag = 1; //part of success
				}

				if(isFailed&&!isSuccess){
					successFlag = 0; //all failed;
				}

				if(!isFailed&&isSuccess){
					successFlag = 2; //all success;
				}
			}
		}
		return successFlag;
	}
}
