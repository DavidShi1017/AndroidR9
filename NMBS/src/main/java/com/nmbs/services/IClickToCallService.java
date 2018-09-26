package com.nmbs.services;

import com.nmbs.model.ClickToCallAftersalesParameter;
import com.nmbs.model.ClickToCallAftersalesResponse;
import com.nmbs.model.ClickToCallParameter;
import com.nmbs.model.ClickToCallScenario;
import com.nmbs.model.GeneralSetting;
import com.nmbs.services.impl.AsyncClickToCallResponse;

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
