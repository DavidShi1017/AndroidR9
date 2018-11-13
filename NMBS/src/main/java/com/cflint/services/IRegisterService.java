package com.cflint.services;

import com.cflint.model.Account;
import com.cflint.services.impl.AsyncRegisterResponse;

public interface IRegisterService{

	/**
	 * Call PersonDataService and register user info
	 * @param Account
	 * @return AsyncRegisterResponse
	 */
	public AsyncRegisterResponse registerInfo(Account account);
	
	/**
	 * Get Account form RegisterService.
	 * @return Account
	 */
	public Account getAccount();
}
