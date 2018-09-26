package com.nmbs.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.CheckAppUpdate;
import com.nmbs.model.CityDetails;
import com.nmbs.model.Currency;
import com.nmbs.model.EPR;
import com.nmbs.model.WizardResponse;
import com.nmbs.util.SharedPreferencesUtils;
import com.nmbs.util.Utils;



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
