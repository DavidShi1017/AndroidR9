package com.nmbs.services.impl;

import com.nmbs.dataaccess.restservice.impl.NotificationDataService;
import com.nmbs.dataaccess.restservice.impl.NotificationDataService.ErrorNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * This is a BroadcastReceiver, Receive intent Service sent information , deal with result and call handler inform view refresh data. 
 */
public class AsyncNotificationResponse extends BroadcastReceiver {
	
	private final static String TAG = AsyncNotificationResponse.class.getSimpleName();
	private static AsyncNotificationResponse instance;
	private static Handler notificationHandler;
	private static boolean isReceiveResponse;//notification Registered success return true
	
	/**
	 * Registers the callback Handler to be used when the response is received
	 * @param handler
	 */
	public void registerHandler(Handler handler){
		notificationHandler = handler;
	}
	
	/**
	 * Unregisters the callback Handler, to be used when the Activity is paused (onPause) so no invalid Handler is kept in this.
	 */
	public static void unregisterHandler(){
		notificationHandler = null;
	}
	
	/**
	 * When the response is received it is stored for later usage. 
	 * @return
	 */
	public boolean isReceiveResponse(){
		return isReceiveResponse;
	}
	
	/**
	 * If receive right response , call this handler sent right information.
	 */
	private void callHandler(){
		
		if (notificationHandler != null){
			notificationHandler.sendEmptyMessage(1);
		}
	}
	
	/**
	 * If receive exception response , call this handler sent exception information.
	 */
	private void callErrorHandler(Bundle bundle){
		if (notificationHandler != null){
			 Message message = new Message();   
	            message.what = 2;   
	            message.setData(bundle);
	            notificationHandler.sendMessage(message);  
		}
	}
	
	/**
	 * BroadcastReceiver receive method, this method is called when the response is received and broadcasted via an Intent with the right Action and Category.
	 */
	@SuppressWarnings("static-access")
	@Override
	public void onReceive(Context context, Intent intent) {
		this.isReceiveResponse = true;
		String receiveAction = intent.getAction();
		//if connection error
		if (ServiceConstant.INTENT_ACTION_NOTIFICATION_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			ErrorNotification error = (ErrorNotification) intent.getSerializableExtra(NotificationDataService.PARAM_OUT_ERROR);
			bundle.putSerializable(NotificationDataService.PARAM_OUT_ERROR, error);
			callErrorHandler(bundle);
			context.unregisterReceiver(this);
		}
		//if receive response
		if (ServiceConstant.INTENT_ACTION_NOTIFICATION_RESPONSE.equals(receiveAction)) {
			Log.i(TAG,"LoginResponse received");
			callHandler();
			context.unregisterReceiver(this);
		}
		
	}
	
	/**
	 * Registers the Receiver needed to receive the response when ready. This method is called by the Service layer.
	 * @param applicationContext
	 */
	public void registerReceiver(Context applicationContext){
		isReceiveResponse = false;
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServiceConstant.INTENT_ACTION_NOTIFICATION_ERROR);
		filter.addAction(ServiceConstant.INTENT_ACTION_NOTIFICATION_RESPONSE);
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		applicationContext.registerReceiver(this, filter);
	}
	/**
	 * Realize singleton , only one AsyncNotificationResponse object.
	 * @return AsyncNotificationResponse
	 */
	public static AsyncNotificationResponse getInstance(){
		if(instance == null){
			instance = new AsyncNotificationResponse();			
		}
		return instance;
	}
}