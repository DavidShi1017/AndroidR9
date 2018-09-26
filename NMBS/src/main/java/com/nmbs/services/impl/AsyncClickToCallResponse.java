package com.nmbs.services.impl;


import com.nmbs.exceptions.NetworkError;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * This is a BroadcastReceiver, Receive intent Service sent information , 
 * deal with result and call handler inform view refresh data. 
 */
public class AsyncClickToCallResponse extends BroadcastReceiver{

	//private final static String TAG = AsyncClickToCallResponse.class.getSimpleName();
	
	private Handler handler;	

	private boolean isReceiveResponse;//notification Registered success return true	

	/**
	 * Registers the callback Handler to be used when the response is received
	 * If no Handler is received to response can also be retrieved via getDossierResponse();
	 * 
	 * @param handler
	 */
	public void registerHandler(Handler handler){
		this.handler = handler;
	}
	
	/**
	 * When the response is received it is stored for later usage. 
	 * @return
	 */
	public boolean isReceiveResponse(){
		return isReceiveResponse;
	}
	/**
	 * Unregisters the callback Handler, to be used when the Activity is paused (onPause) 
	 * so no invalid Handler is kept in this.
	 */
	public void unregisterHandler(){
		this.handler = null;
	}
	

	/**
	 * If receive right response , call this handler sent right information.
	 */
	private void callHandler(int what){
		if (this.handler != null){
			handler.sendEmptyMessage(what);
		}
	}
	/**
	 * If receive exception response , call this handler sent exception information.
	 */
	private void callErrorHandler(Bundle bundle){
		if (this.handler != null){
			 Message message = new Message();   
	            message.what = 2;   
	            message.setData(bundle);
	            handler.sendMessage(message);  
		}
	}
	/**
	 * BroadcastReceiver receive method, this method is called when the response 
	 * is received and broadcasted via an Intent with the right Action and Category.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String receiveAction = intent.getAction();
		//if connection error
		if (ServiceConstant.INTENT_ACTION_CLICK_TO_CALL_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			NetworkError error = (NetworkError) intent.getSerializableExtra(ServiceConstant.PARAM_OUT_ERROR);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, error);
			callErrorHandler(bundle);
			context.unregisterReceiver(this);
		}
		//if receive response
		if (ServiceConstant.INTENT_ACTION_CLICK_TO_CALL_RESPONSE.equals(receiveAction)) {
			//Log.i(TAG,"CityDetailResponse received");

			//Log.i(TAG,"CityDetailResponse received" + weatherInformationResponse.getWeatherInformation().getTemperature());
			callHandler(1);
			
			context.unregisterReceiver(this);
		}
		
	}

	
	/**
	 * Registers the Receiver needed to receive the response when ready. This method is called by the Service layer.
	 * 
	 * Must be done each time a request is done, as a Receiver is only valid for 1 call.
	 * 
	 * @param applicationContext
	 */
	public void registerReceiver(Context applicationContext){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServiceConstant.INTENT_ACTION_CLICK_TO_CALL_ERROR);
		
		filter.addAction(ServiceConstant.INTENT_ACTION_CLICK_TO_CALL_RESPONSE);
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		applicationContext.registerReceiver(this, filter);
	}
}


