package com.nmbs.services.impl;

import java.io.IOException;
import org.json.JSONException;

import com.nmbs.dataaccess.converters.AssistantConverter;
import com.nmbs.dataaccess.restservice.IAssistantDataService;
import com.nmbs.dataaccess.restservice.impl.AssistantDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;

import com.nmbs.model.StationDetailResponse;
import com.nmbs.util.FileManager;
import com.nmbs.util.NetworkUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;



/**
 * IntentService runs in a new thread the executions of the AssistantService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 * 
 */
public class StationDetailIntentService extends IntentService {
	
	//private static final String TAG = StationDetailIntentService.class.getSimpleName();
	private static Context mContext;
	//private static final String TAG = StationBoardIntentService.class.getSimpleName();
	private IAssistantDataService assistantDataService = new AssistantDataService();
   
	public StationDetailIntentService() {
		super(".intentservices.StationDetailIntentService");
	}
	private Intent broadcastIntent = new Intent();

	private String stationCode;
	private StationDetailResponse response = null;
	@Override
	protected void onHandleIntent(Intent intent) {

		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		stationCode = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG_STATION_DETAIL_CODE);
		
		try {
			if (NetworkUtils.getNetworkState(mContext) > 0) {
				response = assistantDataService.executeStationDetail(stationCode, languageBeforSetting, mContext.getApplicationContext());
				
				broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_STATION_DETAIL_RESPONSE);
				broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
				broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, response);
				sendBroadcast(broadcastIntent);
			}else {
				getStationDetailFromFile();
			}
			
		}catch (IOException e) {
			getStationDetailFromFile();
		}catch (TimeOutError e) {
			getStationDetailFromFile();
		}catch (InvalidJsonError e) {
			getStationDetailFromFile();
		}catch (JSONException e) {
			getStationDetailFromFile();
		}catch (RequestFail e) {
			getStationDetailFromFile();
		}catch (ConnectionError e) {			
			catchError(NetworkError.TIMEOUT,e);
		}catch (BookingTimeOutError e) {			
			catchError(NetworkError.TIMEOUT,e);
		}
	}
	
	private StationDetailResponse getStationDetailFromFile(){
		String string;
		StationDetailResponse response = null;
		try {
			//Log.d(TAG, "Read StationDetail response from sd card!!");
			
			string = FileManager.getInstance().readExternalStoragePrivateFile(mContext, null, stationCode + ".json");
			response = new AssistantConverter().parsesStationDetail(string);
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_STATION_DETAIL_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, response);
			sendBroadcast(broadcastIntent);
		} catch (Exception e) {
			//Log.e(TAG, "Read StationDetail response from sd card has Error...");
			catchError(NetworkError.TIMEOUT,e);
		}	
		return response;
	}
	
	/**
	 * Start this IntentService.
	 * @param context
	 */
	public static void startService(Context context, String stationCode, String languageBeforSetting){
		if (stationCode == null){
			throw new NullPointerException("parameter cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, StationDetailIntentService.class);
		
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG_STATION_DETAIL_CODE, stationCode);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		context.startService(msgIntent);		
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param AsyncRegisterResponse.Error
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_STATION_DETAIL_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}