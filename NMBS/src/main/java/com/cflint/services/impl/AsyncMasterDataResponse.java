package com.cflint.services.impl;

import com.cflint.exceptions.NetworkError;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * This is a BroadcastReceiver, Receive intent Service sent information , deal with result and call handler inform view refresh data. 
 */
public class AsyncMasterDataResponse extends BroadcastReceiver{

	//private final static String TAG = AsyncMasterDataResponse.class.getSimpleName();
	
	private static Handler masterHandler;
	private boolean isReceiveResponse;//notification Registered success return true
	
	/**
	 * Registers the callback Handler to be used when the response is received
	 * @param handler
	 */
	public void registerHandler(Handler handler){
		masterHandler = handler;
	}
	
	/**
	 * Unregisters the callback Handler, to be used when the Activity is paused (onPause) so no invalid Handler is kept in this.
	 */
	public void unregisterHandler(){
		masterHandler = null;
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
		//Log.d(TAG, "masterHandler is null? " + String.valueOf(masterHandler == null));	
		if (masterHandler != null){
			masterHandler.sendEmptyMessage(1);
		}
	}
	
	/**
	 * If receive exception response , call this handler sent exception information.
	 */
	private void callErrorHandler(Bundle bundle){
		if (masterHandler != null){
			 Message message = new Message();   
	            message.what = 2;   
	            message.setData(bundle);
	            masterHandler.sendMessage(message);  
		}
	}
	
	/**
	 * BroadcastReceiver receive method, this method is called when the response is received and broadcasted via an Intent with the right Action and Category.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String receiveAction = intent.getAction();
		//if connection error
		if (ServiceConstant.INTENT_ACTION_MASTER_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			NetworkError error = (NetworkError) intent.getSerializableExtra(ServiceConstant.PARAM_OUT_ERROR);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, error);
			this.isReceiveResponse = true;
			callErrorHandler(bundle);
			try {
				context.unregisterReceiver(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		//if receive response
		if (ServiceConstant.INTENT_ACTION_MASTER_RESPONSE.equals(receiveAction)) {
			//Log.d(TAG,"AsyncMasterDataResponse received");
			this.isReceiveResponse = true;
			callHandler();
			try {
				context.unregisterReceiver(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Registers the Receiver needed to receive the response when ready. This method is called by the Service layer.
	 * @param applicationContext
	 */
	public void registerReceiver(Context applicationContext){
		isReceiveResponse = false;
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServiceConstant.INTENT_ACTION_MASTER_ERROR);
		filter.addAction(ServiceConstant.INTENT_ACTION_MASTER_RESPONSE);
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		//Log.d(TAG, "registerReceiver.....");	
		
		applicationContext.registerReceiver(this, filter);
	}

}
