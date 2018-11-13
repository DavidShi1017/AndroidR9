package com.cflint.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.CheckAppUpdate;
import com.cflint.model.CityDetails;
import com.cflint.model.Currency;
import com.cflint.model.EPR;
import com.cflint.model.WizardResponse;
import com.cflint.util.SharedPreferencesUtils;
import com.cflint.util.Utils;



public interface ICheckUpdateService {
	
	public void checkAppVersion(String language)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError;
	public CheckAppUpdate getCheckAppUpdate();
	public void setCheckAppUpdate(CheckAppUpdate checkAppUpdate);
	public String getLastCheckUpdateTime();
	public boolean isExecuteCheckAppUpdate();
	public void setCheckAppManually(boolean isManually);
	public boolean isShowCheckAppUpdateInfo();
	public void setIsReady(boolean isReady);
	public boolean getIsReady();
	public void deleteLastCheckUpdateTime(Context context);
	public void saveCheckStutes(Context context);

}
