package com.cflint.services.impl;

import android.content.Context;

import com.cflint.model.Account;
import com.cflint.services.IRegisterService;
/**
 * Control Register Data and View communicate.
 */ 
public class RegisterService implements IRegisterService {

	private Context applicationContext;	
	private Account account;
	
	/**
	 * Call PersonDataService and register user info
	 * @param Account
	 * @return AsyncRegisterResponse
	 */
	public AsyncRegisterResponse registerInfo(Account account) {
		if (account.validateRegister().equals(Account.RegisterFeedBackTypes.REGISTERCORRECT)) {
			AsyncRegisterResponse aresponse = new AsyncRegisterResponse();
			aresponse.registerReceiver(applicationContext);
			//Offload processing to IntentService
			RegisterIntentService.startService(applicationContext, account);
			// Return the async response who will receive the final return
			return aresponse;
		}else {
			return null;
		}
		
	}
	
	public RegisterService(Context context){
		this.applicationContext = context;
	}
	
	/**
	 * Get Account form RegisterService.
	 * @return Account
	 */
	public Account getAccount() {
		if (this.account == null) {
			this.account = new Account();
		}
		return this.account;
	}

}
