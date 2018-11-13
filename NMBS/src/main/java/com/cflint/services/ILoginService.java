package com.cflint.services;
import android.content.Context;
import com.cflint.model.Account;
import com.cflint.services.impl.AsyncLoginResponse;
/**
 * Control Login Data and View communicate.
 */ 
public interface ILoginService{

	/**
	 * Call PersonDataService and login.
	 *@param Account
	 *@return AsyncLoginResponse
	 */
	public AsyncLoginResponse login(Account account);
	
	/**
	 * Get Account from LoginService.
	 * @return Account
	 */
	public Account getAccount();
	
	/**
	 * Get login status.
	 * @param context
	 * @return boolean 
	 */
	public boolean getLoginStatus(Context context);
	
	/**
	 * Get login email info.
	 * @param context
	 * @return string
	 */
	public String getLoginEmail(Context context);
	
	/**
	 * Log off clear loginInfo change status.
	 * 
	 */
	public void clearLoginInfo();
	
	/**
	 * login success , save login info.
	 * @param context
	 * @param status
	 * @param email
	 */
	public void setLoginInfo(Context context, boolean status,String email);
	
}
