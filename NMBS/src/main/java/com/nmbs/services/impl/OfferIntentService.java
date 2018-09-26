package com.nmbs.services.impl;


import org.apache.http.ParseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import com.nmbs.dataaccess.restservice.impl.OfferDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.OfferResponse;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;



/**
 * OfferIntentService runs in a new thread the executions of the OfferService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class OfferIntentService extends IntentService {
	
	//private static final String TAG = OfferIntentService.class.getSimpleName();
    private static Context mContext;
	public OfferIntentService() {
		super(".intentservices.OfferIntentService");
	}
	Intent broadcastIntent = new Intent();

	@Override
	protected void onHandleIntent(Intent intent) {
		
		OfferQuery offerQuery = (OfferQuery) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		//Log.i(TAG, "OfferRequest = null? : " + (offerQuery == null));
		OfferResponse response;
		try {
			response = new OfferDataService().executeSearchOffer(offerQuery, mContext.getApplicationContext(), 
					languageBeforSetting, offerQuery.getComforClass());
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
		} catch (NoTicket e) {
			catchError(NetworkError.NOTICKET,e);
		}catch (NumberFormatException e) {
			catchError(NetworkError.TIMEOUT,e);
		} catch (java.text.ParseException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (ConnectionError e) {
			//Log.i(TAG, "cityDetailResponse : ConnectionError" );
			catchError(NetworkError.TIMEOUT,e);
		}catch (BookingTimeOutError e) {			
			catchError(NetworkError.TIMEOUT,e);
		}catch (ConnectTimeoutException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (DBooking343Error e) {
			catchError(NetworkError.TIMEOUT,e);
			//e.printStackTrace();
		} catch (CustomError e) {
			catchError(NetworkError.TIMEOUT,e);
			e.printStackTrace();
		} catch (DBookingNoSeatAvailableError e) {
			
			//e.printStackTrace();
		}
		

	}
	
	/**
	 * Start this IntentService.
	 * @param context
	 * @param account
	 */
	public static void startService(Context context, OfferQuery offerRequest, String languageBeforSetting){
		if (offerRequest == null){
			throw new NullPointerException("offerRequest cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, OfferIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, offerRequest);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param NetworkError
	 * @param Exception
	 */
	public void catchError(NetworkError error, Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_OFFER_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}