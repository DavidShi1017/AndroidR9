package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.nmbs.application.NMBSApplication;
import com.nmbs.model.Connection;
import com.nmbs.model.CreateSubscriptionParameter;
import com.nmbs.model.Dossier;
import com.nmbs.model.HafasUser;
import com.nmbs.model.Subscription;
import com.nmbs.model.SubscriptionResponse;
import com.nmbs.preferences.SettingsPref;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.PushService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.DateUtils;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateAllSubScriptionAsyncTask extends AsyncTask<Void, Void, Void>{

	public final static int Subscription_part_of_success = 1;
	public final static int Subscription_all_failed = 0;
	public final static int Subscription_all_success = 2;

	private IPushService pushService;
	private Context mContext;
	private Dossier dossier;
	private List<Connection> connections;
	private static final String TAG = CreateAllSubScriptionAsyncTask.class.getSimpleName();
	private Handler handler;
	private String language;
	public CreateAllSubScriptionAsyncTask(Handler handler,Dossier dossier, List<Connection> connections,IPushService pushService, Context mContext,String language){
		this.mContext = mContext;
		this.pushService = pushService;
		this.dossier = dossier;
		this.handler = handler;
		this.connections = connections;
		this.language = language;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		HafasUser hafasUser = pushService.getUser();
		Log.e(TAG, "CreateAllSubScriptionAsyncTask, hafasUser is..." + hafasUser);
		if(hafasUser != null&&!"".equals(hafasUser.getUserId())){
			Message message = executeAction(hafasUser);
			if(message.what == Subscription_all_failed){
				message = executeAction(hafasUser);
				if(handler != null){
					handler.sendMessage(message);
				}

				if(mContext != null){
					Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
					mContext.sendBroadcast(alertCountIntent);
				}
			}else{
				if(handler != null){
					handler.sendMessage(message);
				}
				if(mContext != null){
					Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
					mContext.sendBroadcast(alertCountIntent);
				}
			}
		}else{
			try{
				String userId = pushService.retryCreateUser(language);
				//Log.e(TAG, "CreateAllSubScriptionAsyncTask, hafasUser is null...");
				//Log.e(TAG, "CreateAllSubScriptionAsyncTask, userId is ..." + userId);
				if(!"".equals(userId)){
					hafasUser = pushService.getUser();
					Message message = executeAction(hafasUser);
					if(message.what == Subscription_all_failed){
						message = executeAction(hafasUser);
						if(handler != null){
							handler.sendMessage(message);
						}
						if(mContext != null){
							Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
							mContext.sendBroadcast(alertCountIntent);
						}
					}else{
						if(handler != null){
							handler.sendMessage(message);
						}

						if(mContext != null){
							Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
							mContext.sendBroadcast(alertCountIntent);
						}
					}
				}else{
					Message errorMessage = new Message();
					errorMessage.what = PushService.USER_ERROR;
					//Log.e(TAG, "CreateAllSubScriptionAsyncTask, userId is null...");
					//Log.e(TAG, "CreateAllSubScriptionAsyncTask, userId is null PushService.USER_ERROR...");
					if(handler != null){
						//Log.e(TAG, "CreateAllSubScriptionAsyncTask, handler != null...");
						handler.sendMessage(errorMessage);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}

	private Message executeAction(HafasUser hafasUser){
		Message message = new Message();
		if(NMBSApplication.getInstance().getTestService().isCreateSubscription()){
			//Log.e(TAG, "Test mode...Not trigger create subscriptions call..Subscription_all_failed....");
			message.what = Subscription_all_failed; //all success;
			return message;
		}
		List<CreateSubscriptionParameter> subscriptionParameters = new ArrayList<CreateSubscriptionParameter>();
		/*String notificationTime = SettingsPref.getStartNotifiTime(mContext);
		if(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
			notificationTime = notificationTime.replace("min.","").trim();
		}else{
			notificationTime = "5";
		}*/
		int i = 1;
		int start = NMBSApplication.getInstance().getSettingService().getStartNotifiTimeIntger();
		int delay = NMBSApplication.getInstance().getSettingService().getDelayNotifiTimeIntger();
		for(Connection connection:connections){
			CreateSubscriptionParameter subScription = new CreateSubscriptionParameter(i,hafasUser.getUserId(),
					connection.getReconCtx(), DateUtils.dateToString(connection.getDeparture()),
					DateUtils.dateToString(connection.getDeparture()),delay, delay, start,hafasUser.getRegisterId(),1);

			subscriptionParameters.add(subScription);
			i++;
		}
		boolean isSuccess = false;
		boolean isFailed = false;
		try {
			List<SubscriptionResponse> subscriptionResponses = this.pushService.createMultipleSubScription(subscriptionParameters);
			for(SubscriptionResponse subscriptionResponse:subscriptionResponses){
				if(subscriptionResponse.isSuccess()){
					Connection connection = connections.get(Integer.parseInt(subscriptionResponse.getId())-1);
					this.pushService.saveSubscriptionInLocal(new Subscription(subscriptionResponse.getSubscriptionId(), Utils.sha1(connection.getReconCtx()),connection.getReconCtx(),connection.getOriginStationRcode(),connection.getDestinationStationRcode(),
							DateUtils.getDateWithTimeZone(connection.getDeparture()),connection.getOriginStationName(),connection.getDestinationStationName(),dossier.getDossierId(), connection.getConnectionId()));
					isSuccess = true;
				}else{
					isFailed = true;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {


			if(isFailed&&isSuccess){
				message.what = Subscription_part_of_success; //part of success
			}

			if(isFailed&&!isSuccess){
				message.what = Subscription_all_failed; //all failed;
			}

			if(!isFailed&&isSuccess){
				message.what = Subscription_all_success; //all success;
			}

		}
		return message;
	}
}
