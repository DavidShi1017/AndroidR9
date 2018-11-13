package com.cflint.services.impl;


import java.io.IOException;

import org.json.JSONException;

import com.cflint.dataaccess.restservice.IPersonDataService;
import com.cflint.dataaccess.restservice.impl.PersonDataService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.NetworkError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.Account;
import com.cflint.model.Person;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * IntentService runs in a new thread the executions of the RegisterService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 * 
 */
public class RegisterIntentService extends IntentService {
	
	private static final String TAG = RegisterIntentService.class.getSimpleName();
	private IPersonDataService personDataService = new PersonDataService();
    private static Context mContext;
	public RegisterIntentService() {
		super(".intentservices.RegisterIntentService");
	}
	private Intent broadcastIntent = new Intent();

	@Override
	protected void onHandleIntent(Intent intent) {
		Account registerAccount = (Account) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		//Log.i(TAG, "accountRequest = null? : " + (registerAccount == null));
		Person response = null;
		try {
			response = personDataService.executeRegister(registerAccount, mContext.getApplicationContext());
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_REGISTER_RESPONSE);
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
	public static void startService(Context context, Account account){
		if (account == null){
			throw new NullPointerException("account cannot be null");
		}
		mContext = context;
		Intent msgIntent = new Intent(context, RegisterIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, account);
		context.startService(msgIntent);		
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param AsyncRegisterResponse.Error
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_REGISTER_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}