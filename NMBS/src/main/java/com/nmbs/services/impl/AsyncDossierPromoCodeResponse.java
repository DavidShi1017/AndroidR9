package com.nmbs.services.impl;

import com.nmbs.exceptions.NetworkError;
import com.nmbs.model.DossierResponse;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * This is a BroadcastReceiver, Receive intent Service sent information , 
 * deal with result and call handler inform view refresh data. 
 */
public class AsyncDossierPromoCodeResponse extends BroadcastReceiver{

	private final static String TAG = AsyncDossierPromoCodeResponse.class.getSimpleName();
	
	private Handler handler;
	
	private DossierResponse dossierResponse;
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
	 * When the response is received it is stored for later usage. 
	 * 
	 * @return DossierResponse
	 */
	public DossierResponse getDossierResponse(){
		return this.dossierResponse;
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
	private void callErrorHandler(Bundle bundle, int messageWhat){
		if (this.handler != null){
			 Message message = new Message();   
	            message.what = messageWhat;   
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
		if (ServiceConstant.INTENT_ACTION_DOSSIER_PROMO_CODE_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			this.dossierResponse = new DossierResponse();
			NetworkError error = (NetworkError) intent.getSerializableExtra(ServiceConstant.PARAM_OUT_ERROR);
			String errorMessage = intent.getStringExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
			DossierResponse dossierResponse = (DossierResponse) intent.getExtras().get(ServiceConstant.PARAM_OUT_MSG);
			this.dossierResponse = dossierResponse;
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, error);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, errorMessage);
			
			callErrorHandler(bundle, ServiceConstant.MESSAGE_WHAT_ERROR);
			context.unregisterReceiver(this);
		}

		//if receive response
		if (ServiceConstant.INTENT_ACTION_DOSSIER_PROMO_CODE_RESPONSE.equals(receiveAction)) {
			//Log.i(TAG,"DossierResponse received");
			DossierResponse dossierResponse = (DossierResponse) intent.getExtras().get(ServiceConstant.PARAM_OUT_MSG);

			this.dossierResponse = dossierResponse;
			callHandler(ServiceConstant.MESSAGE_WHAT_OK);
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
		filter.addAction(ServiceConstant.INTENT_ACTION_DOSSIER_PROMO_CODE_RESPONSE);
		filter.addAction(ServiceConstant.INTENT_ACTION_DOSSIER_PROMO_CODE_ERROR);		
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		
		applicationContext.registerReceiver(this, filter);
	}
}


