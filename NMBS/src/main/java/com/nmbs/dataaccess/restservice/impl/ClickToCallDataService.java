package com.nmbs.dataaccess.restservice.impl;

import java.io.IOException;


import org.json.JSONException;

import android.content.Context;


import com.nmbs.R;

import com.nmbs.dataaccess.converters.ClickToCallResponseConverter;
import com.nmbs.dataaccess.restservice.IClickToCallDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.model.ClickToCallAftersalesParameter;
import com.nmbs.model.ClickToCallAftersalesResponse;
import com.nmbs.model.ClickToCallParameter;


import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.ObjectToJsonUtils;

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
