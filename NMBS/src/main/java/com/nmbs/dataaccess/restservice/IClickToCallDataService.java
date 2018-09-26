package com.nmbs.dataaccess.restservice;

import java.io.IOException;


import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;

import com.nmbs.model.ClickToCallParameter;



public interface IClickToCallDataService {
	public void executeClickToCall(
			ClickToCallParameter clickToCallParameter, Context context,
			String languageBeforSetting) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError;
}
