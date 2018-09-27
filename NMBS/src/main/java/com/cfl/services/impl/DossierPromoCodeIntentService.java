package com.cfl.services.impl;



import com.cfl.dataaccess.restservice.IDossierPromoCodeDataService;

import com.cfl.dataaccess.restservice.impl.DossierPromoCodeDataService;

import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.NetworkError;

import com.cfl.model.DossierResponse;
import com.cfl.model.PromoParameter;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * DossierIntentService runs in a new thread the executions of the
 * DossierService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class DossierPromoCodeIntentService extends IntentService {
	private static Context mContext;
	private static final String TAG = DossierPromoCodeIntentService.class.getSimpleName();
	private Intent broadcastIntent = new Intent();

	public DossierPromoCodeIntentService() {
		super(".intentservices.DossierPromoCodeIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String languageBeforSetting = intent.getStringExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		
		PromoParameter promoParameter = (PromoParameter) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		
		IDossierPromoCodeDataService dossierPromoCodeDataService = new DossierPromoCodeDataService();
		DossierResponse dossierResponse = null;
		try {
			dossierResponse = dossierPromoCodeDataService.executePromoCode(promoParameter, mContext, languageBeforSetting);
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_PROMO_CODE_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
			sendBroadcast(broadcastIntent);
		} catch (CustomError e) {
			catchError(NetworkError.CustomError, e, dossierResponse);
		} catch (Exception e) {
			catchError(NetworkError.TIMEOUT, e, dossierResponse);
		} 
	}

	/**
	 * Start this IntentService.
	 * 
	 * @param context
	 * @param account
	 */
	public static void startService(Context context, PromoParameter promo, String languageBeforSetting, int optionFlag) {
		if (promo == null) {
			throw new NullPointerException("PromoParameter cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, DossierPromoCodeIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, promo);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		// Log.i(TAG, "languageBeforSetting : " + languageBeforSetting);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_FLAG, optionFlag);
		
		context.startService(msgIntent);
	}

	/**
	 * Set error broadcast action,and sent broadcast.
	 * 
	 * @param NetworkError
	 * @param Exception
	 */
	public void catchError(NetworkError error, Exception e, DossierResponse dossierResponse) {
		
		Log.d(TAG, "catchError.....");
		Log.d(TAG, "dossierResponse....." + dossierResponse);
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_PROMO_CODE_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
		Log.i(TAG, "Error message : " + e.getMessage());
		sendBroadcast(broadcastIntent);
		
		if (e != null) {
			e.printStackTrace();
		}
	}

}
