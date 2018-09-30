package com.cfl.services.impl;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.json.JSONException;

import com.cfl.dataaccess.restservice.impl.DossierDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingCATError;
import com.cfl.exceptions.DBookingInvalidFtpError;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NetworkError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RefreshConirmationError;
import com.cfl.exceptions.RequestFail;
import com.cfl.model.DossierParameter;
import com.cfl.model.DossierResponse;
import com.cfl.util.ActivityConstant;



import android.app.IntentService;
import android.content.Context;
import android.content.Intent;




/**
 * DossierIntentService runs in a new thread the executions of the DossierService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class DossierIntentService extends IntentService {
	private static Context mContext;
	private static final String TAG = DossierIntentService.class.getSimpleName();
	private Intent broadcastIntent = new Intent();
	private int optionFlag;
	public DossierIntentService() {
		super(".intentservices.DossierIntentService");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		DossierParameter dossierParameter = (DossierParameter) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		String languageBeforSetting = intent.getStringExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		
		optionFlag = (Integer)intent.getSerializableExtra(ServiceConstant.PARAM_IN_FLAG);
		//Log.i(TAG, "DossierParameter = null? : " + (dossierParameter == null));
		DossierResponse dossierResponse;
		
		try {
			switch (optionFlag) {
				case DossierService.DOSSIER_CREATE:
					
					dossierResponse = new DossierDataService().executeSearchDossier(dossierParameter, mContext.getApplicationContext(), languageBeforSetting);
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_RESPONSE);
					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					sendBroadcast(broadcastIntent);
				break;
				
				case DossierService.DOSSIER_UPDATE_INSURANCE_DELIVERYMETHOD:
					//Log.i(TAG, "Update Insurance and DeliveryMethod...... " );
					dossierResponse = new DossierDataService().executeUpdateDossier(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, optionFlag);
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_RESPONSE);
					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					sendBroadcast(broadcastIntent);
					break;
					
				case DossierService.DOSSIER_UPDATE_CUSTOMER_PAYMENT:
					//Log.i(TAG, "Update Insurance and DeliveryMethod...... " );
					dossierResponse = new DossierDataService().executeUpdateDossier(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, optionFlag);
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_RESPONSE);
					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					sendBroadcast(broadcastIntent);
					break;	
				case DossierService.DOSSIER_INIT_PAYMENT:
					//Log.i(TAG, "Init Payment...... " );
					Map<String, Object> initPaymentResponse = new HashMap<String, Object>();
					initPaymentResponse = new DossierDataService().executeInitPayment(dossierParameter, mContext.getApplicationContext(), languageBeforSetting);
					dossierResponse = (DossierResponse)initPaymentResponse.get("dossierResponse");
					String paymentUrl = (String)initPaymentResponse.get("paymentUrl");
					
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_PAYMENT_RESPONSE);
					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					broadcastIntent.putExtra(ActivityConstant.PAYMENT_LOCATION_URL, paymentUrl);
					sendBroadcast(broadcastIntent);
					break;
				case DossierService.DOSSIER_REFRESH_PAYMENT:
					//Log.i(TAG, "Refresh payment...... " );
					dossierResponse = new DossierDataService().executeRefreshPayment(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, optionFlag);
					if (dossierResponse != null && dossierResponse.getPaymentState() == null) {
						catchError(NetworkError.REFRESHPAYMENTFAIL, null);
					}else {
						broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_RESPONSE);					
						broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
						broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
						sendBroadcast(broadcastIntent);
					}
					
					break;	
				case DossierService.DOSSIER_ABORT_PAYMENT:
					//Log.i(TAG, "Abort payment...... " );
					dossierResponse = new DossierDataService().executeAbortPayment(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, optionFlag);
					new AsyncAbortPaymentDossierResponse().registerReceiver(mContext);
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_ABORT_PAYMENT_RESPONSE);					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					sendBroadcast(broadcastIntent);
					break;	
				case DossierService.DOSSIER_REFRESH_ORDER_STATE:
					//Log.i(TAG, "REFRESHING DOSSIER ORDER STATE...... " );
					dossierResponse = new DossierDataService().executeRefreshOrderState(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, DossierService.GUID);
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_RESPONSE);					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					sendBroadcast(broadcastIntent);
					break;	
				case DossierService.DOSSIER_DELETE:
					//Log.i(TAG, "DELETEING DOSSIER...... " );
					dossierResponse = new DossierDataService().executeCancelDossier(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, optionFlag);
					DossierService.GUID = null;
	            	
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_DELETE_RESPONSE);					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					break;	
				case DossierService.DOSSIER_REFRESH_CONFIRMATION:
					//Log.i("RefreshConfirmation", "REFRESH CONFIRMATION DOSSIER...... " );
					dossierResponse = new DossierDataService().executeRefreshConfirmation(dossierParameter, mContext.getApplicationContext(), languageBeforSetting, optionFlag);
					DossierService.GUID = null;
	            	
					broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_REFRESH_CONFIRMATION);					
					broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
					broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
					sendBroadcast(broadcastIntent);
					break;	
					
			}
			
		}catch (CustomError e) {
			catchError(NetworkError.CustomError, e);
		}catch (DBooking343Error e) {
			catchError(NetworkError.DBOOKINGERROR,e);
		}catch (DBookingNoSeatAvailableError e) {
			catchError(NetworkError.DBookingNoSeatAvailableError, e);
			//Log.e(TAG, "DBookingNoSeatAvailableError...... " );
		}
		catch (InvalidJsonError e) {
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
			catchError(NetworkError.TIMEOUT,e);
		}catch (NumberFormatException e) {
			catchError(NetworkError.TIMEOUT,e);
		} catch (java.text.ParseException e) {
			catchError(NetworkError.TIMEOUT,e);
		}catch (ConnectionError e) {
			if(optionFlag == DossierService.DOSSIER_REFRESH_PAYMENT){
				catchError(NetworkError.REFRESHPAYMENTFAIL, e);
			}else {
				catchError(NetworkError.TIMEOUT,e);
			}
			
		}
		catch (BookingTimeOutError e) {			
			catchError(NetworkError.BOOKINGTIMEOUT,e);
		}catch (RefreshConirmationError e) {		
			if(optionFlag == DossierService.DOSSIER_REFRESH_CONFIRMATION){
				catchError(NetworkError.RefreshConirmationError,e);
			}else{
				catchError(NetworkError.TIMEOUT,e);
			}			
		}
	}  

	
	/**
	 * Start this IntentService.
	 * @param context
	 * @param /account
	 */
	public static void startService(Context context, DossierParameter dossierParameter, String languageBeforSetting, int optionFlag){
		if (dossierParameter == null){
			throw new NullPointerException("offerRequest cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, DossierIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, dossierParameter);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		//Log.i(TAG, "languageBeforSetting : " + languageBeforSetting);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_FLAG, optionFlag);
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param /NetworkError
	 * @param /Exception
	 */
	public void catchError(NetworkError error, Exception e){
		if(optionFlag == DossierService.DOSSIER_DELETE){
			
		}else if(optionFlag == DossierService.DOSSIER_ABORT_PAYMENT){
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_ABORT_PAYMENT_ERROR);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
			//Log.e(TAG, "errorMessage......error... " + e.getMessage());
			sendBroadcast(broadcastIntent);
		}else if(optionFlag == DossierService.DOSSIER_REFRESH_CONFIRMATION){
			
			/*broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_REFRESH_CONFIRMATION_ERROR);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
			sendBroadcast(broadcastIntent);*/
		}else if(optionFlag == DossierService.DOSSIER_REFRESH_PAYMENT  && NetworkError.REFRESHPAYMENTFAIL == error){
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_REFRESH_PAYMENT_ERROR);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, NetworkError.REFRESHPAYMENTFAIL);
			sendBroadcast(broadcastIntent);
		}
		else{
			
			//Log.e(TAG, "sendBroadcast...... " );
			//Log.e(TAG, "DBookingNoSeatAvailableError......error... " + error);
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_ERROR);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
			sendBroadcast(broadcastIntent);
		}		
		if (e != null) {
			e.printStackTrace();
		}
		
	}
	
}