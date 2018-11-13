package com.cflint.dataaccess.restservice;

import java.io.IOException;


import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.NoTicket;
import com.cflint.exceptions.RequestFail;

import com.cflint.model.ClickToCallParameter;



public interface IClickToCallDataService {
	public void executeClickToCall(
			ClickToCallParameter clickToCallParameter, Context context,
			String languageBeforSetting) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError;
}
