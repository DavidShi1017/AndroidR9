package com.cfl.dataaccess.restservice.impl;

import android.content.Context;

import com.cfl.R;
import com.cfl.dataaccess.converters.CustomErrorMessager;
import com.cfl.dataaccess.converters.RealTimeConnectionConverter;
import com.cfl.dataaccess.converters.ScheduleResponseConverter;
import com.cfl.dataaccess.restservice.IScheduleDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.AdditionalScheduleQueryParameter;
import com.cfl.model.Message;
import com.cfl.model.RealTimeConnectionResponse;
import com.cfl.model.RestResponse;
import com.cfl.model.ScheduleDetailRefreshModel;
import com.cfl.model.ScheduleQuery;
import com.cfl.model.ScheduleResponse;
import com.cfl.services.ISettingService;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.HttpStatusCodes;
import com.cfl.util.ObjectToJsonUtils;

import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class ScheduleDataService extends CustomErrorMessager implements IScheduleDataService{
	private static final String TAG = ScheduleDataService.class.getSimpleName();
	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	public static String GUID;
	private String currentLanguage;

	public RealTimeConnectionResponse refreshScheduleDetail(Context context,String languageBeforSetting,ScheduleDetailRefreshModel scheduleDetailRefreshModel, ISettingService settingService) throws InvalidJsonError, RequestFail, IOException, JSONException, NoTicket, NumberFormatException,
			java.text.ParseException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{
		RealTimeConnectionResponse realTimeConnectionResponse = null;
		String postJsonString = ObjectToJsonUtils.getScheduleDetailRefreshStr(scheduleDetailRefreshModel);
		String refreshScheduleUrl = context.getString(R.string.server_url_refresh_schedule_detail);
		RealTimeConnectionConverter realTimeConnectionConverter = new RealTimeConnectionConverter();

		String responseFirst = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, refreshScheduleUrl,
				languageBeforSetting, HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);

		RestResponse restResponse = realTimeConnectionConverter.parseRefreshConnection(responseFirst);
		super.throwErrorMessage(restResponse, context, "");

		realTimeConnectionResponse = realTimeConnectionConverter.parseConnection(responseFirst);
		return realTimeConnectionResponse;
	}

	public ScheduleResponse executeScheduleQuery(ScheduleQuery scheduleQuery, String language, Context context)
			throws InvalidJsonError, JSONException, TimeOutError, RequestFail,
			ParseException, IOException, ConnectionError, BookingTimeOutError,
			DBooking343Error, CustomError, DBookingNoSeatAvailableError {
		//System.out.println("==================");
		ScheduleResponse scheduleResponse = null;
		String postJsonString = ObjectToJsonUtils.getScheduleQueryStr(scheduleQuery);
		String urlStringOfOfferGuid = context.getString(R.string.server_url_schedule_query);
		ScheduleResponseConverter scheduleResponseConverter = new ScheduleResponseConverter();

		String responseFirst = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlStringOfOfferGuid,
				language, HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		//Log.e("ScheduleResponse", "ScheduleResponse....First" + responseFirst);

		RestResponse restResponse = scheduleResponseConverter.parseSearchSchedule(responseFirst);
		super.throwErrorMessage(restResponse, context, "");
		GUID = getGUID(restResponse);

		String urlStringOfOfferQueries = context.getString(R.string.server_url_schedule_query) +"/" + GUID;
		String responseSecond = httpRestServiceCaller.executeHTTPRequest(context, null, urlStringOfOfferQueries,
				language, HTTPRestServiceCaller.HTTP_GET_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		//Log.e("ScheduleResponse", "ScheduleResponse....responseSecond" + responseSecond);
		/*
		InputStream is = context.getResources().openRawResource(R.raw.schedule_test_query_result);
		String responseSecond = FileManager.getInstance().readFileWithInputStream(is);
		*/
		//System.out.println("@@@@@@@@@@@@@"+responseSecond);
		scheduleResponse = scheduleResponseConverter.parseSchedule(responseSecond);
		super.throwErrorMessage(scheduleResponse, context, "");
		return scheduleResponse;
	}

	public ScheduleResponse executeSearchTrains(Context context,AdditionalScheduleQueryParameter
			additionalScheduleQueryParameter, String languageBeforSetting)
			throws InvalidJsonError, RequestFail, IOException, JSONException, NoTicket, NumberFormatException,
			java.text.ParseException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{
		ScheduleResponse scheduleResponse = null;
		String postJsonString = ObjectToJsonUtils.getPostAdditionalScheduleQueryParameterStr(additionalScheduleQueryParameter);
		String urlString = context.getString(R.string.server_url_schedule_query) + "/" + GUID + "/additional-connections";

		String response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString ,languageBeforSetting,
				HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
		ScheduleResponseConverter scheduleResponseConverter = new ScheduleResponseConverter();
		RestResponse restResponse = scheduleResponseConverter.parseSearchSchedule(response);
		super.throwErrorMessage(restResponse, context, "");
		scheduleResponse = scheduleResponseConverter.parseSchedule(response);
		super.throwErrorMessage(scheduleResponse, context, "");
		return scheduleResponse;
	}

	/**
	 * Separate response , get GUID String.
	 * @param restResponse
	 * @return String
	 */
	private String getGUID(RestResponse restResponse) {
		List<Message> messages = restResponse.getMessages();
		String GUID = "";
		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			String statusCode = message.getStatusCode();
			if (HttpStatusCodes.SC_CREATED == Integer.parseInt(statusCode)) {
				String GUIDTemp = message.getDescription();
				GUID = GUIDTemp.substring(GUIDTemp.lastIndexOf(" ")+1);
				break;
			}
		}
		return GUID;
	}

}
