package com.nmbs.services;

import com.nmbs.model.Account;
import com.nmbs.services.impl.AsyncRegisterResponse;

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
