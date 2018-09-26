package com.nmbs.dataaccess.restservice;

import android.content.Context;

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
import com.nmbs.model.RealTimeConnectionResponse;
import com.nmbs.model.ScheduleDetailRefreshModel;
import com.nmbs.model.ScheduleQuery;
import com.nmbs.model.ScheduleResponse;
import com.nmbs.services.ISettingService;

import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.IOException;

public interface IScheduleDataService {
	public ScheduleResponse executeScheduleQuery(ScheduleQuery scheduleQuery, String language, Context context)
			throws InvalidJsonError, JSONException, TimeOutError, RequestFail,
			ParseException, IOException, ConnectionError, BookingTimeOutError,
			DBooking343Error, CustomError, DBookingNoSeatAvailableError;

	public ScheduleResponse executeSearchTrains(Context context,AdditionalScheduleQueryParameter
			additionalScheduleQueryParameter, String languageBeforSetting)
			throws InvalidJsonError, RequestFail, IOException, JSONException, NoTicket, NumberFormatException,
			java.text.ParseException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;

	public RealTimeConnectionResponse refreshScheduleDetail(Context context, String languageBeforSetting, ScheduleDetailRefreshModel scheduleDetailRefreshModel, ISettingService settingService) throws InvalidJsonError, RequestFail, IOException, JSONException, NoTicket, NumberFormatException,
			java.text.ParseException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;;
}
