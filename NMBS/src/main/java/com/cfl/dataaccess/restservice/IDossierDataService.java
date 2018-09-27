package com.cfl.dataaccess.restservice;

import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RefreshConirmationError;
import com.cfl.exceptions.RequestFail;
import com.cfl.model.DossierParameter;
import com.cfl.model.DossierResponse;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.RealTimeInfoResponse;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


public interface IDossierDataService {

	public DossierResponse executeSearchDossier(DossierParameter dossierParameter, 
			Context context, String languageBeforSetting)
			throws RequestFail, IOException, InvalidJsonError, NumberFormatException, 
			JSONException, ParseException, NoTicket, ConnectionError, BookingTimeOutError, 
			RefreshConirmationError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;
	public DossierResponse executeUpdateDossier(DossierParameter dossierParameter, Context context, String languageBeforSetting,
			int optionFlag) throws RequestFail, IOException, InvalidJsonError, NumberFormatException, JSONException, ParseException, 
			NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;;
	public Map<String, Object> executeInitPayment(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError, CustomError;
	public DossierResponse executeRefreshPayment(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError;
	public DossierResponse executeCancelDossier(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError;

}
