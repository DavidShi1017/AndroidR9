package com.cfl.dataaccess.restservice;

import java.io.IOException;


import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RequestFail;

import com.cfl.model.ClickToCallParameter;



public interface IClickToCallDataService {
	public void executeClickToCall(
			ClickToCallParameter clickToCallParameter, Context context,
			String languageBeforSetting) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError;
}
