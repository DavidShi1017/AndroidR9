package com.nmbs.services.impl;

import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.CheckOptionBaseService;
import com.nmbs.dataaccess.database.LoginDataBaseService;
import com.nmbs.dataaccess.restservice.impl.LoginDataService;

import com.nmbs.log.LogUtils;
import com.nmbs.model.Account;
import com.nmbs.model.Account.RegisterFeedBackTypes;
import com.nmbs.model.CheckOption;
import com.nmbs.model.ForgotPwdInfoResponse;
import com.nmbs.model.LastUpdateTimestampPassword;
import com.nmbs.model.LoginResponse;
import com.nmbs.model.LogonInfo;
import com.nmbs.model.ResendInfoResponse;
import com.nmbs.util.DateUtils;
import com.nmbs.util.DecryptUtils;
import com.nmbs.util.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Control Login Data and View communicate.
 */ 
public class LoginService {
	
	private final static String TAG = LoginService.class.getSimpleName();
	private SharedPreferences prefs;
	private Context applicationContext;
	private Account account;
	public final static String LOGIN_STATE_SUCCESS = "Success";
	public final static String LOGIN_STATE_BLOCKED = "Blocked";
	public final static String LOGIN_STATE_NOTACTIVATED = "NotActivated";
	public final static String LOGIN_STATE_NOTFOUND = "NotFound";
	public final static String LOGIN_STATE_TECHNICALERROR = "TechnicalError";

	public final static String LOGIN_STATE_CUSTOMERBLOCKED = "CustomerBlocked";
	public final static String LOGIN_STATE_QUERYCUSTOMERFAILURE = "QueryCustomerFailure";
	public final static String LOGIN_STATE_READCUSTOMERFAILURE = "ReadCustomerFailure";
	public final static String LOGIN_STATE_CREATECUSTOMERFAILURE = "CreateCustomerFailure";
	public final static String LOGIN_STATE_UPDATECUSTOMERFAILURE = "UpdateCustomerFailure";
	public final static String LOGIN_STATE_RESOLVELOGINFAILURE = "ResolveLoginFailure";
	public final static String LOGIN_STATE_EMAILMISSING = "EmailMissing";

	public final static String LOGIN_PROVIDER_CRIS = "CRIS";
	public final static String LOGIN_PROVIDER_GOOGLE = "Google";
	public final static String LOGIN_PROVIDER_FACEBOOK = "Facebook";

	private LogonInfo loginInfo;
	
	public LoginService(Context context){
		this.applicationContext = context;
	}


	public LoginResponse login(String email, String pwd, String loginProvider, String language) throws Exception {

		LoginDataService loginDataService = new LoginDataService();
		LoginResponse loginResponse = loginDataService.executeLogon(applicationContext, language, email, pwd, loginProvider, null);

		return loginResponse;
	}

	public LoginResponse loginSocial(String loginProvider, String language, String token) throws Exception {

		LoginDataService loginDataService = new LoginDataService();
		LoginResponse loginResponse = loginDataService.executeLogon(applicationContext, language, null, null, loginProvider, token);

		return loginResponse;
	}

	public ResendInfoResponse resend(String customerId, String email, String language) throws Exception {

		LoginDataService loginDataService = new LoginDataService();
		ResendInfoResponse resendInfoResponse = loginDataService.executeResend(applicationContext, language, customerId, email);

		return resendInfoResponse;
	}

	public ForgotPwdInfoResponse forgotPwd(String email, String language) throws Exception {

		LoginDataService loginDataService = new LoginDataService();
        ForgotPwdInfoResponse forgotPwdInfoResponse = loginDataService.executeForgotPwd(applicationContext, language, email);
        if(forgotPwdInfoResponse != null && forgotPwdInfoResponse.getForgotPwdInfo() != null && forgotPwdInfoResponse.getForgotPwdInfo().isResetLogin()){
        	setEmail("");
		}

		return forgotPwdInfoResponse;
	}

	public void setFirstName(String firstName){
		LoginDataBaseService loginDataBaseService = new LoginDataBaseService(applicationContext);
		LogonInfo logonInfo = loginDataBaseService.getLogonInfo();
		if(logonInfo != null){
			loginDataBaseService.deleteLogonInfo();
			LogonInfo logonInfo1Update = new LogonInfo(logonInfo.getCustomerId(), firstName, logonInfo.getEmail(), logonInfo.getCode(),
					logonInfo.getPhoneNumber(), logonInfo.getState(), logonInfo.getStateDescription(), logonInfo.getPersonId(),
					logonInfo.getLastUpdateTimestampPassword(), logonInfo.getLoginProvider());
			loginDataBaseService.insertLogonInfo(logonInfo1Update);
		}
	}

	public void setEmail(String email){
		LoginDataBaseService loginDataBaseService = new LoginDataBaseService(applicationContext);
		LogonInfo logonInfo = loginDataBaseService.getLogonInfo();
		LogUtils.e("email", "setEmail email--------->" + email);
		if(logonInfo != null){
			loginDataBaseService.deleteLogonInfo();
			LogonInfo logonInfo1Update = new LogonInfo(logonInfo.getCustomerId(), logonInfo.getFirstName(), email, logonInfo.getCode(),
					logonInfo.getPhoneNumber(), logonInfo.getState(), logonInfo.getStateDescription(), logonInfo.getPersonId(),
					logonInfo.getLastUpdateTimestampPassword(), logonInfo.getLoginProvider());
			loginDataBaseService.insertLogonInfo(logonInfo1Update);
		}else{
			LogonInfo logonInfo1Update = new LogonInfo("", "", email, "",
					"", "", "", "",
					null, "");
			loginDataBaseService.insertLogonInfo(logonInfo1Update);
		}
	}

	public void setPhoneNumber(String code, String phoneNumber){
		LoginDataBaseService loginDataBaseService = new LoginDataBaseService(applicationContext);
		LogonInfo logonInfo = loginDataBaseService.getLogonInfo();
		if(logonInfo != null){
			loginDataBaseService.deleteLogonInfo();
			LogonInfo logonInfo1Update = new LogonInfo(logonInfo.getCustomerId(), logonInfo.getFirstName(), logonInfo.getEmail(), code,
					phoneNumber, logonInfo.getState(), logonInfo.getStateDescription(), logonInfo.getPersonId(),
					logonInfo.getLastUpdateTimestampPassword(), logonInfo.getLoginProvider());
			loginDataBaseService.insertLogonInfo(logonInfo1Update);
		}else{
			LogonInfo logonInfo1Update = new LogonInfo("", "", "", code,
					phoneNumber, "", "", "",
					null, "");
			loginDataBaseService.insertLogonInfo(logonInfo1Update);
		}
	}

	public void logOut(){
		LoginDataBaseService loginDataBaseService = new LoginDataBaseService(applicationContext);
		LogonInfo logonInfo = loginDataBaseService.getLogonInfo();
		if(logonInfo != null){
			loginDataBaseService.deleteLogonInfo();
			LogonInfo logonInfo1Update = new LogonInfo("", "", logonInfo.getEmail(), logonInfo.getCode(),
					logonInfo.getPhoneNumber(), "", "", "",
					null, "");
			loginDataBaseService.insertLogonInfo(logonInfo1Update);
		}

		loginInfo = null;
	}

	public void saveProfileViaUrl(String url){
		String email = Utils.getUrlValue(url, "email");
		String firstname = Utils.getUrlValue(url, "firstname");
		String mobilePhone = Utils.getUrlValue(url, "mobile phone");
		if(email != null && Utils.checkEmailPattern(email)){
			setEmail(email);
		}
		if(mobilePhone != null && !mobilePhone.isEmpty()){
			String countryCode = Utils.getPhoneCode(mobilePhone);
			String phoneNumber = Utils.getPhoneNumber(mobilePhone);
			setPhoneNumber(countryCode, phoneNumber);
		}
		if(firstname != null && !firstname.isEmpty()){
			setFirstName(firstname);
		}
	}

	public void saveCheckOptionViaUrl(String url){
		//String countStr = Utils.getUrlValue(url, "count");
		//String expirationStr = Utils.getUrlValue(url, "expiration");
		LogUtils.d(TAG, "saveCheckOptionViaUrl...." + url);
		if(url.contains("#")){
			String str = url.substring(url.indexOf("#") + 1);
			String countStr = str.substring(0, 1);
			String expirationStr = str.substring(2);
			LogUtils.d(TAG, "countStr...." + countStr);
			LogUtils.d(TAG, "expirationStr...." + expirationStr);
			if(countStr != null && !countStr.isEmpty()){
				LoginDataService dataService = new LoginDataService();
				int count = Integer.valueOf(countStr);
				if(count > 0){
					if(expirationStr != null && !expirationStr.isEmpty()){
						Date expiration = DateUtils.stringToDateWithUnderline(expirationStr);
						dataService.saveCheckOption(applicationContext, count, expiration);
						LogUtils.d(TAG, "expiration...." + expiration);
					}
				}else {
					CheckOption checkOption = new CheckOption(count, null);
					dataService.cancelCheckOption(applicationContext, checkOption);
				}
			}
		}

	}

	public void logOutFully(){
		LoginDataBaseService loginDataBaseService = new LoginDataBaseService(applicationContext);
		loginDataBaseService.deleteLogonInfo();
		loginInfo = null;
	}

	public void syncProfileInfo(){
		LoginDataService loginDataService = new LoginDataService();
		try {
			loginDataService.executeProfileInfoSync(applicationContext, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
                    getLogonInfo().getCustomerId(), getControl(getLogonInfo()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void syncCheckOption(){
		LoginDataService loginDataService = new LoginDataService();
		try {
			loginDataService.executeCheckOption(applicationContext, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
					getLogonInfo().getCustomerId(), getControl(getLogonInfo()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void syncCheckPwd(){
		LoginDataService loginDataService = new LoginDataService();
		try {
			LastUpdateTimestampPassword parseResponse = loginDataService.executeCheckPwd(applicationContext,
					NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
					getLogonInfo().getPersonId(), getLogonInfo());
			if(parseResponse != null && parseResponse.getMessages() != null && parseResponse.getMessages().size() == 0){
				LogUtils.d("syncCheckPwd", "parseResponse------>" + parseResponse.getLastUpdateTimestampPassword());
				LogUtils.d("syncCheckPwd", "getLogonInfo------>" + getLogonInfo().getLastUpdateTimestampPassword());
				if(parseResponse.getLastUpdateTimestampPassword() != null){
					if(parseResponse.getLastUpdateTimestampPassword().after(getLogonInfo().getLastUpdateTimestampPassword())){
						logOut();
						LogUtils.d("syncCheckPwd", "syncCheckPwd----logOut--->");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CheckOption getCheckOption(Context context){
		CheckOptionBaseService dataBase = new CheckOptionBaseService(context);
		return dataBase.getCheckOption();
	}

	public LogonInfo getLogonInfo(){
		//if(loginInfo == null){
			LoginDataService loginDataService = new LoginDataService();
			loginInfo = loginDataService.getLogonInfo(applicationContext);
		//}
		return loginInfo;
	}

	public boolean isLogon(){
		//boolean isLogon = false;
		//if(loginInfo == null){
		loginInfo = getLogonInfo();
		//}
		if(loginInfo != null && (loginInfo.getCustomerId() != null
				&& !loginInfo.getCustomerId().isEmpty())){
			return true;
		}else {
			return false;
		}
		//return isLogon;
	}

	public boolean isFullyLogout(){
		if(loginInfo == null || (loginInfo.getPhoneNumber() == null && loginInfo.getEmail() == null)
				|| (loginInfo.getPhoneNumber().isEmpty() && loginInfo.getEmail().isEmpty())){
			return true;
		}else {
			return false;
		}
	}

	public String getControl(LogonInfo logonInfo){
		String salt = NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getRestSalt();
		LogUtils.e("getControl", "decrypStr------->" + salt);
		String decrypStr = DecryptUtils.decryptData(salt);
		String sha1 = "";
		if(logonInfo != null){
			try {
				LogUtils.e("getControl", "CustomerId------->" + logonInfo.getCustomerId());
				LogUtils.e("getControl", "decrypStr------->" + decrypStr);
				sha1 = Utils.sha1(logonInfo.getCustomerId() + decrypStr);
				LogUtils.e("getControl", "sha1------->" + sha1);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return sha1;
	}
	
	/**
	 * Get Account from LoginService.
	 * @return Account
	 */
	public Account getAccount() {
		if (this.account == null) {
			this.account = new Account();
		}
		return this.account;
	}
	/**
	 * Get login status.
	 * @param context
	 * @return boolean 
	 */
	public boolean getLoginStatus(Context context) {
		if (this.prefs == null) {
			this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return this.prefs.getBoolean(ServiceConstant.LOGIN_STATUS, false);
	}
	/**
	 * Get login email info.
	 * @param context
	 * @return string
	 */
	public String getLoginEmail(Context context) {
		if (this.prefs == null) {
			this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return this.prefs.getString(ServiceConstant.LOGIN_EMAIL,"");
	}
	
	/**
	 * Call PersonDataService and login
	 * @param account
	 * @return AsyncLoginResponse
	 */
	public AsyncLoginResponse login(Account account) {

		if (account.validateLogin().equals(RegisterFeedBackTypes.REGISTERCORRECT)) {
			Log.d(TAG, "enter loginService");
			AsyncLoginResponse aresponse = new AsyncLoginResponse();
			aresponse.registerReceiver(applicationContext);
			//Offload processing to IntentService
			LoginIntentService.startService(applicationContext, account);
			
			// Return the async response who will receive the final return
			return aresponse;
		}else {
			return null;
		}
	
	}



	/**
	 * Login success , save login info.
	 * @param context
	 * @param status
	 * @param email
	 */
	public void setLoginInfo(Context context, boolean status, String email) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = this.prefs.edit();
		editor.putString(ServiceConstant.LOGIN_EMAIL, email);
		editor.putBoolean(ServiceConstant.LOGIN_STATUS, status);
		editor.commit();
	}

	/**
	 *  Log off clear loginInfo change status.
	 */
	public void clearLoginInfo() {
		if (this.prefs != null) {
			Editor editor = this.prefs.edit();
			editor.putString(ServiceConstant.LOGIN_EMAIL, "");
			editor.putBoolean(ServiceConstant.LOGIN_STATUS, false);
			editor.commit();
		}
	}
	

}
