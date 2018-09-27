package com.cfl.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.CheckAppUpdate;
import com.cfl.model.CityDetails;
import com.cfl.model.Currency;
import com.cfl.model.EPR;
import com.cfl.model.WizardResponse;
import com.cfl.util.SharedPreferencesUtils;
import com.cfl.util.Utils;



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
