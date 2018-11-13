package com.cflint.dataaccess.restservice.impl;

import java.io.IOException;


import org.json.JSONException;

import android.content.Context;


import com.cflint.R;

import com.cflint.dataaccess.converters.ClickToCallResponseConverter;
import com.cflint.dataaccess.restservice.IClickToCallDataService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.NoTicket;
import com.cflint.exceptions.RequestFail;
import com.cflint.model.ClickToCallAftersalesParameter;
import com.cflint.model.ClickToCallAftersalesResponse;
import com.cflint.model.ClickToCallParameter;


import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.ObjectToJsonUtils;

public class ClickToCallDataService implements IClickToCallDataService {
	//private static final String TAG = DossierDataService.class.getSimpleName();
	
	public void executeClickToCall(ClickToCallParameter clickToCallParameter,
			Context context, String languageBeforSetting) throws RequestFail,
			IOException, InvalidJsonError, NumberFormatException,
			JSONException, org.apache.http.ParseException, NoTicket, ConnectionError, BookingTimeOutError{
		
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = ObjectToJsonUtils.getPostClickToCallParameterStr(clickToCallParameter);			
		String urlString = context.getString(R.string.server_url_get_click_to_call_context);
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		new ClickToCallResponseConverter().parseClickToCall(response);
		//Log.i(TAG, "response is? : " + response);

	}

	public ClickToCallAftersalesResponse executeAftersales(ClickToCallAftersalesParameter clickToCallAftersalesParameter,
								   Context context, String languageBeforSetting) throws RequestFail,
			IOException, InvalidJsonError, NumberFormatException,
			JSONException, org.apache.http.ParseException, NoTicket, ConnectionError, BookingTimeOutError{

		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = ObjectToJsonUtils.getPostClickToCallAftersalesParameterStr(clickToCallAftersalesParameter);
		String urlString = context.getString(R.string.server_url_get_aftersales);
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting,
				HTTPRestServiceCaller.HTTP_POST_METHOD, 20000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		ClickToCallAftersalesResponse clickToCallAftersalesResponse = new ClickToCallResponseConverter().parsesClickToCallAftersalesResponse(response);
		//Log.i(TAG, "response is? : " + response);
		return clickToCallAftersalesResponse;
	}


}
