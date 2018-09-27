package com.cfl.dataaccess.restservice;

import android.content.Context;

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
import com.cfl.model.RealTimeConnectionResponse;
import com.cfl.model.ScheduleDetailRefreshModel;
import com.cfl.model.ScheduleQuery;
import com.cfl.model.ScheduleResponse;
import com.cfl.services.ISettingService;

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
