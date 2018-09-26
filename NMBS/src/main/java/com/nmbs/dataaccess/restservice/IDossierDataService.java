package com.nmbs.dataaccess.restservice;

import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RefreshConirmationError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.model.DossierParameter;
import com.nmbs.model.DossierResponse;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.model.RealTimeInfoResponse;

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
