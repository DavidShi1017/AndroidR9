package com.nmbs.services.impl;

import com.nmbs.exceptions.NetworkError;
import com.nmbs.model.StationDetailResponse;

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
public class AsyncStationDetailResponse extends BroadcastReceiver {
	
	//private final static String TAG = AsyncStationDetailResponse.class.getSimpleName();
	private Handler mHandler;
	private StationDetailResponse stationDetailResponse;
	
	/**
	 * Registers the call back Handler, to be used when the response is received	 
	 * @param handler
	 */
	public void registerHandler(Handler handler){
		this.mHandler = handler;
	}
	
	/**
	 * Unregisters the call back Handler, to be used when the Activity is paused (onPause) so no invalid Handler is kept in this.
	 */
	public void unregisterHandler(){
		this.mHandler = null;
	}
	
	/**
	 * When the response is received it is stored for later usage.	
	 * @return StationDetailResponse
	 */
	public StationDetailResponse getStationDetailResponse(){
		return this.stationDetailResponse;
	}
	
	/**
	 * If receive right response , call this handler sent right information.
	 */
	private void callSuccessHandler(){
		if (this.mHandler != null){
			mHandler.sendEmptyMessage(1);
		}
	}
	
	/**
	 * If receive exception response , call this handler sent exception information.
	 */
	private void callErrorHandler(Bundle bundle){
		if (this.mHandler != null){
			Message message = new Message();   
            message.what = 2;   
            message.setData(bundle);
            mHandler.sendMessage(message);  
		}
	}
	
	/**
	 * BroadcastReceiver receive method, this method is called when the response is received and broadcasted 
	 * via an Intent with the right Action and Category.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String receiveAction = intent.getAction();
		//if connection error
		if (ServiceConstant.INTENT_ACTION_STATION_DETAIL_ERROR.equals(receiveAction)) {
			Bundle bundle = new Bundle();
			NetworkError error = (NetworkError) intent.getSerializableExtra(ServiceConstant.PARAM_OUT_ERROR);
			bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, error);
			callErrorHandler(bundle);
			context.unregisterReceiver(this);
		}
		//if receive response success
		if (ServiceConstant.INTENT_ACTION_STATION_DETAIL_RESPONSE.equals(receiveAction)) {
			//Log.i(TAG,"Response received...");
			StationDetailResponse StationDetailResponse = (StationDetailResponse) intent.getExtras().get(ServiceConstant.PARAM_OUT_MSG);
			this.stationDetailResponse = StationDetailResponse;
			callSuccessHandler();
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
		filter.addAction(ServiceConstant.INTENT_ACTION_STATION_DETAIL_RESPONSE);
		filter.addAction(ServiceConstant.INTENT_ACTION_STATION_DETAIL_ERROR);
		filter.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
		applicationContext.registerReceiver(this, filter);
	}
	
}