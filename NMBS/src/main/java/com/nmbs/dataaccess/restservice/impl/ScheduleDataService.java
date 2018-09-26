package com.nmbs.dataaccess.restservice.impl;

import android.content.Context;

import com.nmbs.R;
import com.nmbs.dataaccess.converters.CustomErrorMessager;
import com.nmbs.dataaccess.converters.RealTimeConnectionConverter;
import com.nmbs.dataaccess.converters.ScheduleResponseConverter;
import com.nmbs.dataaccess.restservice.IScheduleDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.AdditionalScheduleQueryParameter;
import com.nmbs.model.Message;
import com.nmbs.model.RealTimeConnectionResponse;
import com.nmbs.model.RestResponse;
import com.nmbs.model.ScheduleDetailRefreshModel;
import com.nmbs.model.ScheduleQuery;
import com.nmbs.model.ScheduleResponse;
import com.nmbs.services.ISettingService;
import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.HttpStatusCodes;
import com.nmbs.util.ObjectToJsonUtils;

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
				languageBeforSetting, HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "",
				HTTPRestServiceCaller.API_VERSION_VALUE_7);

		RestResponse restResponse = realTimeConnectionConverter.parseRefreshConnection(responseFirst);
		super.throwErrorMessage(restResponse, context);

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
		super.throwErrorMessage(restResponse, context);
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
		super.throwErrorMessage(scheduleResponse, context);
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
		super.throwErrorMessage(restResponse, context);
		scheduleResponse = scheduleResponseConverter.parseSchedule(response);
		super.throwErrorMessage(scheduleResponse, context);
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
