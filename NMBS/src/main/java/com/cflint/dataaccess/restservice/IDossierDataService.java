package com.cflint.dataaccess.restservice;

import android.content.Context;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.DBooking343Error;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.NoTicket;
import com.cflint.exceptions.RefreshConirmationError;
import com.cflint.exceptions.RequestFail;
import com.cflint.model.DossierParameter;
import com.cflint.model.DossierResponse;
import com.cflint.model.RealTimeInfoRequestParameter;
import com.cflint.model.RealTimeInfoResponse;

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
