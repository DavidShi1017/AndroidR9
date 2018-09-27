package com.cfl.services;

import com.cfl.model.Account;
import com.cfl.services.impl.AsyncRegisterResponse;

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
