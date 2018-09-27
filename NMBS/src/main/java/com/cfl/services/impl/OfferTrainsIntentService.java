package com.cfl.services.impl;


import java.io.IOException;
import org.apache.http.ParseException;
import org.json.JSONException;

import com.cfl.dataaccess.restservice.impl.OfferDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NetworkError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RequestFail;
import com.cfl.model.AdditionalConnectionsParameter;
import com.cfl.model.OfferQuery;
import com.cfl.model.OfferResponse;
import com.cfl.util.ActivityConstant;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;


/**
 * OfferTrainsIntentService runs in a new thread the executions of the OfferTrainsIntentService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class OfferTrainsIntentService extends IntentService {
	
//	private static final String TAG = OfferTrainsIntentService.class.getSimpleName();
    private static Context mContext;
	public OfferTrainsIntentService() {
		super(".intentservices.OfferTrainsIntentService");
	}
	Intent broadcastIntent = new Intent();

	@Override
	protected void onHandleIntent(Intent intent) {
		AdditionalConnectionsParameter additionalConnectionsParameter = (AdditionalConnectionsParameter) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		OfferQuery offerQuery = (OfferQuery) intent.getSerializableExtra(ActivityConstant.OFFER_QUERY);
		OfferResponse response;
		try {
			response = new OfferDataService().executeSearchTrains(mContext.getApplicationContext(),additionalConnectionsParameter, languageBeforSetting, offerQuery.getComforClass());
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_OFFER_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, response);
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
		} catch (java.text.ParseException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (ConnectionError e) {
			
			catchError(NetworkError.TIMEOUT,e);
		}catch (BookingTimeOutError e) {			
			catchError(NetworkError.TIMEOUT,e);
		}catch (DBooking343Error e) {
			catchError(NetworkError.TIMEOUT,e);
		} catch (CustomError e) {
			catchError(NetworkError.TIMEOUT,e);
			e.printStackTrace();
		} catch (DBookingNoSeatAvailableError e) {
			catchError(NetworkError.TIMEOUT, e);
		}
		

	}
	
	/**
	 * Start this IntentService.
	 * @param context
	 * @param account
	 */
	public static void startService(Context context, OfferQuery offerRequest, AdditionalConnectionsParameter additionalConnectionsParameter, String languageBeforSetting){
		if (additionalConnectionsParameter == null){
			throw new NullPointerException("additionalConnectionsParameter cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, OfferTrainsIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, additionalConnectionsParameter);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		msgIntent.putExtra(ActivityConstant.OFFER_QUERY, offerRequest);
		
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param NetworkError
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_OFFER_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}