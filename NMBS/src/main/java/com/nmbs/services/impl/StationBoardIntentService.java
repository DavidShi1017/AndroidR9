package com.nmbs.services.impl;


import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.json.JSONException;

import com.nmbs.dataaccess.restservice.IAssistantDataService;
import com.nmbs.dataaccess.restservice.IStationBoardDataService;
import com.nmbs.dataaccess.restservice.impl.AssistantDataService;
import com.nmbs.dataaccess.restservice.impl.StationBoardDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.StationBoardResponse;
import com.nmbs.util.DateUtils;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;



/**
 * StationBoardIntentService runs in a new thread the executions of the AssistantService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 * 
 */
public class StationBoardIntentService extends IntentService {
	
	private static Context mContext;
	//private static final String TAG = StationBoardIntentService.class.getSimpleName();
	private IStationBoardDataService stationBoardDataService = new StationBoardDataService();
   
	public StationBoardIntentService() {
		super(".intentservices.StationBoardIntentService");
	}
	private Intent broadcastIntent = new Intent();

	private StationBoardQuery stationBoardQuery;

	private StationBoardResponse response = null;
	@Override
	protected void onHandleIntent(Intent intent) {

		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		stationBoardQuery = (StationBoardQuery) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG_STATION_BOARD_RCODE);
	
		try {
			response = stationBoardDataService.executeStationBoard(stationBoardQuery, languageBeforSetting, mContext);
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_STATION_BOARD_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, response);
			sendBroadcast(broadcastIntent);
		}catch (CustomError e) {
			catchError(NetworkError.CustomError, e);
			e.printStackTrace();
		} catch (Exception e) {
			catchError(NetworkError.TIMEOUT, e);
		}
	}
	
	
	/**
	 * Start this IntentService.
	 * @param context
	 */
	public static void startService(Context context, StationBoardQuery stationBoardQuery, String languageBeforSetting){
/*		if (stationBoardQuery == null){
			throw new NullPointerException("parameter cannot be null");
		}*/
		mContext = context;
		Intent msgIntent = new Intent(context, StationBoardIntentService.class);
		
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG_STATION_BOARD_RCODE, stationBoardQuery);

		
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		context.startService(msgIntent);		
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param AsyncRegisterResponse.Error
	 * @param Exception
	 */
	public void catchError(NetworkError error, Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_STATION_BOARD_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}