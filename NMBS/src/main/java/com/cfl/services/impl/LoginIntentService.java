package com.cfl.services.impl;


import com.cfl.dataaccess.restservice.IPersonDataService;
import com.cfl.dataaccess.restservice.impl.PersonDataService;

import com.cfl.exceptions.NetworkError;
import com.cfl.model.Account;
import com.cfl.model.Person;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * LoginIntentService runs in a new thread the executions of the LoginService
 */
public class LoginIntentService extends IntentService {
	
	private static final String TAG = LoginIntentService.class.getSimpleName();    
    private IPersonDataService personDataService = new PersonDataService();
    private static Context mContext;
	public LoginIntentService() {
		super(".intentservices.LoginIntentService");
	}
	private Intent broadcastIntent = new Intent();

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Account account = (Account) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		//Log.i(TAG, "Account = null? : " + (account == null));
		Person person;
		try {
			person = personDataService.executeLogin(account, mContext.getApplicationContext());
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_LOGIN_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, person);
			sendBroadcast(broadcastIntent);
			
		} catch (Exception e) {
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
		Intent msgIntent = new Intent(context, LoginIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, account);
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param NetworkError
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_LOGIN_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}

}