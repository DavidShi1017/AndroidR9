package com.nmbs.services.impl;


import java.util.ArrayList;
import java.util.List;

import com.nmbs.exceptions.NetworkError;

import com.nmbs.log.LogUtils;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.Order;


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
public class AsyncDossierAfterSaleResponse extends BroadcastReceiver{

	private final static String TAG = AsyncDossierAfterSaleResponse.class.getSimpleName();
	
	private Handler handler;	
	private DossierDetailsResponse dossierResponse;

	private boolean isReceiveResponse;//notification Registered success return true	
	private boolean hasError = false;
	//private int stationBoardBulkCallErrorCount;
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
	 * @return DossierAftersalesResponse
	 */
	public DossierDetailsResponse getDossierAftersalesResponse(){
		return this.dossierResponse;
	}
	
/*	public int getStationBoardBulkCallErrorCount() {
		return stationBoardBulkCallErrorCount;
	}*/

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
	
	public boolean isHasError() {
		return hasError;
	}

	/**
	 * BroadcastReceiver receive method, this method is called when the response 
	 * is received and broadcasted via an Intent with the right Action and Category.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String receiveAction = intent.getAction();
		//if connection error
		if (ServiceConstant.INTENT_ACTION_DOSSIER_DETAIL_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			this.dossierResponse = new DossierDetailsResponse();
			this.hasError = true;
			NetworkError error = (NetworkError) intent.getSerializableExtra(ServiceConstant.PARAM_OUT_ERROR);
			String errorMessage = intent.getStringExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, errorMessage);
			LogUtils.e(TAG, "Dossier.....catchError error" + error);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, error);
			callErrorHandler(bundle);
			context.unregisterReceiver(this);
		}
		//if receive response
		if (ServiceConstant.INTENT_ACTION_DOSSIER_DETAIL_RESPONSE.equals(receiveAction)) {
			LogUtils.i(TAG,"Dossier received.....");
			DossierDetailsResponse dossierResponse = (DossierDetailsResponse) intent.getExtras().get(ServiceConstant.PARAM_OUT_MSG);
		
			this.dossierResponse = dossierResponse;
			
			@SuppressWarnings("unchecked")
			List<Order> orders = (List<Order>) intent.getExtras().getSerializable("newOrders");
			hasError = intent.getExtras().getBoolean("hasError");
			//stationBoardBulkCallErrorCount = intent.getExtras().getInt("stationBoardBulkCallErrorCount");
			//Log.i(TAG,"stationBoardBulkCallErrorCount======== " + stationBoardBulkCallErrorCount);
			
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
		filter.addAction(ServiceConstant.INTENT_ACTION_DOSSIER_DETAIL_RESPONSE);
		
		filter.addAction(ServiceConstant.INTENT_ACTION_DOSSIER_DETAIL_ERROR);
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		applicationContext.registerReceiver(this, filter);
	}
}


