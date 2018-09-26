package com.nmbs.services.impl;


import com.nmbs.exceptions.NetworkError;
import com.nmbs.model.OfferResponse;

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
public class AsyncOfferResponse extends BroadcastReceiver {
	
	//private final static String TAG = AsyncOfferResponse.class.getSimpleName();
	
	private Handler handler;
	private OfferResponse offerResponse;
	
	/**
	 * Registers the callback Handler to be used when the response is received
	 * If no Handler is received to response can also be retrieved via getOfferResponse();
	 * 
	 * @param handler
	 */
	public void registerHandler(Handler handler){
		this.handler = handler;
	}
	
	/**
	 * Unregisters the callback Handler, to be used when the Activity is paused (onPause) so no invalid Handler is kept in this.
	 */
	public void unregisterHandler(){
		this.handler = null;
	}
	
	/**
	 * When the response is received it is stored for later usage. 
	 * 
	 * @return OfferResponse
	 */
	public OfferResponse getOfferResponse(){
		return this.offerResponse;
	}
	
	/**
	 * If receive right response , call this handler sent right information.
	 */
	private void callHandler(){
		if (this.handler != null){
			handler.sendEmptyMessage(1);
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
	 * BroadcastReceiver receive method, this method is called when the response is received and broadcasted via an Intent with the right Action and Category.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String receiveAction = intent.getAction();
		//if connection error
		if (ServiceConstant.INTENT_ACTION_OFFER_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			this.offerResponse = new OfferResponse();
			NetworkError error = (NetworkError) intent.getSerializableExtra(ServiceConstant.PARAM_OUT_ERROR);
			String errorMessage = intent.getStringExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, error);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, errorMessage);
			callErrorHandler(bundle);
			context.unregisterReceiver(this);
		}
		//if receive response
		if (ServiceConstant.INTENT_ACTION_OFFER_RESPONSE.equals(receiveAction)) {
			//Log.i(TAG,"OfferResponse received");
			OfferResponse offerResponse = (OfferResponse) intent.getExtras().get(ServiceConstant.PARAM_OUT_MSG);
			this.offerResponse = offerResponse;
			callHandler();
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
		filter.addAction(ServiceConstant.INTENT_ACTION_OFFER_RESPONSE);
		filter.addAction(ServiceConstant.INTENT_ACTION_OFFER_ERROR);
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		applicationContext.registerReceiver(this, filter);
	}
	
}