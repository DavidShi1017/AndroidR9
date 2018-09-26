package com.nmbs.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.GeneralSettingDatabaseService;
import com.nmbs.dataaccess.restservice.impl.ClickToCallDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.model.ClickToCallAftersalesParameter;
import com.nmbs.model.ClickToCallAftersalesResponse;
import com.nmbs.model.ClickToCallParameter;
import com.nmbs.model.ClickToCallScenario;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.ProviderSetting;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.ISettingService;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class ClickToCallService implements IClickToCallService {
	//private final static String TAG = MasterService.class.getSimpleName();
	private Context applicationContext;
	private final static String CALL_CENTER_PHONE_NUMBER_SHAREDPREFERENCES_KEY = "Call_Center_Phone_Number";
	private final static String PHONE_NUMBER = "Phone_Number";
	private final static String PHONE_NUMBER_PREFIX = "Phone_Number_Prefix";
	private final static String PHONE_which = "Phone_which";
	public final static int DossierQuestion = 1;
	public final static int DossierIssue  = 2;
	public final static int Exchange  = 3;

	public ClickToCallService(Context context){
		this.applicationContext = context;
	}

	/**
	 * Start new thread and post ClickToCallParameter to web Service.
	 * @return AsyncClickToCallResponse
	 */
	public GeneralSetting getGeneralSetting(){
		GeneralSettingDatabaseService generalSettingDatabaseService = new GeneralSettingDatabaseService(
				applicationContext);
		GeneralSetting generalSettingInDataBase = generalSettingDatabaseService
				.selectGeneralSetting();
		return generalSettingInDataBase;
	}

	public void savePhoneNumberAndPrefix(String prefix, String phoneNumber, int which){
		SharedPreferences phoneNumberPreference = applicationContext.getSharedPreferences(CALL_CENTER_PHONE_NUMBER_SHAREDPREFERENCES_KEY, 0);
		SharedPreferences.Editor localEditor = phoneNumberPreference.edit();
		localEditor.putString(PHONE_NUMBER, phoneNumber);
		localEditor.putString(PHONE_NUMBER_PREFIX, prefix);
		localEditor.putInt(PHONE_which, which);
		localEditor.commit();
	}
	public int getPhoneNumberWhich(){
		SharedPreferences phoneNumberPreference = applicationContext.getSharedPreferences(CALL_CENTER_PHONE_NUMBER_SHAREDPREFERENCES_KEY, 0);
		Log.d("SharedPreferences", "PHONE_which...." + phoneNumberPreference.getInt(PHONE_which, 2));
		return phoneNumberPreference.getInt(PHONE_which, 2);
	}

	public String getPhoneNumberPrefix(){
		SharedPreferences phoneNumberPreference = applicationContext.getSharedPreferences(CALL_CENTER_PHONE_NUMBER_SHAREDPREFERENCES_KEY, 0);
		return phoneNumberPreference.getString(PHONE_NUMBER_PREFIX,"+32");
	}

	public String getPhoneNumber(){
		SharedPreferences phoneNumberPreference = applicationContext.getSharedPreferences(CALL_CENTER_PHONE_NUMBER_SHAREDPREFERENCES_KEY, 0);
		return phoneNumberPreference.getString(PHONE_NUMBER,"");
	}

	public void deletePhoneNumber(){
		SharedPreferences phoneNumberPreference = applicationContext.getSharedPreferences(CALL_CENTER_PHONE_NUMBER_SHAREDPREFERENCES_KEY, 0);
		SharedPreferences.Editor localEditor = phoneNumberPreference.edit();
		localEditor.remove(PHONE_NUMBER);
		localEditor.remove(PHONE_NUMBER_PREFIX);
		localEditor.commit();
	}

	public AsyncClickToCallResponse sendClickToCall(ClickToCallParameter ClickToCallParameter, ISettingService settingService) {

		//Log.d(TAG, "enter ClickToCallService");
		AsyncClickToCallResponse asyncClickToCallResponse = new AsyncClickToCallResponse();
		asyncClickToCallResponse.registerReceiver(applicationContext);

		ClickToCallIntentService.startService(applicationContext, ClickToCallParameter , settingService.getCurrentLanguagesKey());

		// Return the async response who will receive the final return
		return asyncClickToCallResponse;
		//return null;
	}

	public ClickToCallAftersalesResponse aftersales(ClickToCallAftersalesParameter clickToCallAftersalesParameter) throws Exception{

		ClickToCallDataService clickToCallDataService = new ClickToCallDataService();
		ClickToCallAftersalesResponse clickToCallAftersalesResponse = clickToCallDataService.executeAftersales(clickToCallAftersalesParameter, applicationContext,
				NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
		// Return the async response who will receive the final return
		return clickToCallAftersalesResponse;
		//return null;
	}

	public String getPhoneNumber(ClickToCallScenario clickToCallScenario, String simOperator){

		String phoneNumber = "";
		if (clickToCallScenario != null) {
			phoneNumber = clickToCallScenario.getDefaultPhoneNumber();
			List<ProviderSetting> getProviderSettings = clickToCallScenario.getProviderSettings();
			if (getProviderSettings != null) {
				//Log.d(TAG, "getProviderSettings? " + getProviderSettings.size());
				for (ProviderSetting providerSetting : getProviderSettings) {
					//Log.d(TAG, "providerSetting? " + providerSetting.getProvider());
					if(StringUtils.endsWithIgnoreCase(simOperator, providerSetting.getProvider())){

						phoneNumber = providerSetting.getPhoneNumber();
						break;
					}
				}
			}
		}
		return phoneNumber;

	}
}
