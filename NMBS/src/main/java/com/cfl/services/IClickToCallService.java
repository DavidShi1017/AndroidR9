package com.cfl.services;

import com.cfl.model.ClickToCallAftersalesParameter;
import com.cfl.model.ClickToCallAftersalesResponse;
import com.cfl.model.ClickToCallParameter;
import com.cfl.model.ClickToCallScenario;
import com.cfl.model.GeneralSetting;
import com.cfl.services.impl.AsyncClickToCallResponse;

public interface IClickToCallService {

	public AsyncClickToCallResponse sendClickToCall(ClickToCallParameter clickToCallParameter, ISettingService settingService);
	public String getPhoneNumber(ClickToCallScenario clickToCallScenario, String simOperator);
	public GeneralSetting getGeneralSetting();
	public void savePhoneNumberAndPrefix(String prefix, String phoneNumber, int which);
	public String getPhoneNumberPrefix();
	public String getPhoneNumber();
	public int getPhoneNumberWhich();
	public void deletePhoneNumber();
	public ClickToCallAftersalesResponse aftersales(ClickToCallAftersalesParameter clickToCallAftersalesParameter) throws Exception;
}
