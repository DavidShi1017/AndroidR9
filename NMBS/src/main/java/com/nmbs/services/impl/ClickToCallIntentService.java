package com.nmbs.services.impl;


import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import com.nmbs.dataaccess.restservice.impl.ClickToCallDataService;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.model.ClickToCallParameter;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;



/**
 * ClickToCallIntentService runs in a new thread the executions of the ClickToCallService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class ClickToCallIntentService extends IntentService {
	
	//private static final String TAG = ClickToCallIntentService.class.getSimpleName();
    private static Context mContext;
	public ClickToCallIntentService() {
		super(".intentservices.ClickToCallIntentService");
	}
	Intent broadcastIntent = new Intent();

	@Override
	protected void onHandleIntent(Intent intent) {
		
		ClickToCallParameter clickToCallParameter = (ClickToCallParameter) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		//Log.i(TAG, "ClickToCallParameter = null? : " + (clickToCallParameter == null));

		try {
			new ClickToCallDataService().executeClickToCall(clickToCallParameter, mContext, languageBeforSetting);
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_CLICK_TO_CALL_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			//broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, response);
			sendBroadcast(broadcastIntent);
		}catch (InvalidJsonError e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (JSONException e) {
			catchError(NetworkError.TIMEOUT,e);
		} catch (ParseException e) {
			catchError(NetworkError.TIMEOUT,e);
		} catch (RequestFail e) {
			catchError(NetworkError.TIMEOUT,e);
		} catch (IOException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (NoTicket e) {
			catchError(NetworkError.NOTICKET,e);
		}catch (NumberFormatException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (ConnectionError e) {
			//Log.i(TAG, "cityDetailResponse : ConnectionError" );
			catchError(NetworkError.TIMEOUT,e);
		}catch (BookingTimeOutError e) {			
			catchError(NetworkError.TIMEOUT,e);
		}
	}
	
	/**
	 * Start this IntentService.
	 * @param context
	 * @param account
	 */
	public static void startService(Context context, ClickToCallParameter clickToCallParameter, String languageBeforSetting){
		if (clickToCallParameter == null){
			throw new NullPointerException("ClickToCallParameter cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, ClickToCallIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, clickToCallParameter);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param NetworkError
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_CLICK_TO_CALL_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}