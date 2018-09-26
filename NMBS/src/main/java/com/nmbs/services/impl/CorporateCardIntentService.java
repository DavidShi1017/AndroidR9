package com.nmbs.services.impl;

import java.io.IOException;
import org.json.JSONException;
import com.nmbs.dataaccess.restservice.IOfferDataService;
import com.nmbs.dataaccess.restservice.impl.OfferDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.GreenPointsResponse;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;



/**
 * IntentService runs in a new thread the executions of the Service
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 * 
 */
public class CorporateCardIntentService extends IntentService {
	
	private static Context mContext;
	private IOfferDataService offerDataServcie;
   
	public CorporateCardIntentService() {
		super(".intentservices.CorporateCardIntentService");
	}
	private Intent broadcastIntent = new Intent();

	private String greenpointNumber;
	private GreenPointsResponse response = null;
	@Override
	protected void onHandleIntent(Intent intent) {

		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		greenpointNumber = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG_CORPORATE_CARD_GREENPONIT_NUMBER);
			
		try {
			offerDataServcie = new OfferDataService();
			response = offerDataServcie.executeCorporateCard(greenpointNumber, languageBeforSetting, mContext.getApplicationContext());
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_CORPORATE_CARD_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, response);
			sendBroadcast(broadcastIntent);
		}catch (IOException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (TimeOutError e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (InvalidJsonError e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (JSONException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (RequestFail e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (ConnectionError e) {
			
			catchError(NetworkError.CONNECTION,e);
		}catch (BookingTimeOutError e) {			
			catchError(NetworkError.TIMEOUT,e);
		}

	}
	
	
	/**
	 * Start this IntentService.
	 * @param context
	 */
	public static void startService(Context context, String greenpointNumber, String languageBeforSetting){
		if (greenpointNumber == null){
			throw new NullPointerException("parameter cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, CorporateCardIntentService.class);
		
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG_CORPORATE_CARD_GREENPONIT_NUMBER, greenpointNumber);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		context.startService(msgIntent);		
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param AsyncRegisterResponse.Error
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_CORPORATE_CARD_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}