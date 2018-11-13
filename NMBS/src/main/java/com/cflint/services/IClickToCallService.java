package com.cflint.services;

import com.cflint.model.ClickToCallAftersalesParameter;
import com.cflint.model.ClickToCallAftersalesResponse;
import com.cflint.model.ClickToCallParameter;
import com.cflint.model.ClickToCallScenario;
import com.cflint.model.GeneralSetting;
import com.cflint.services.impl.AsyncClickToCallResponse;

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
