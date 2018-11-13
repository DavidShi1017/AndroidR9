package com.cflint.dataaccess.restservice;

import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.DBooking343Error;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.DonotContainTicket;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.JourneyPast;
import com.cflint.exceptions.NoTicket;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.DossierAftersalesResponse;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Order;
import com.cflint.model.StationBoardQuery;
import com.cflint.model.StationBoardResponse;
import com.cflint.model.StationDetailResponse;

public interface IAssistantDataService {
	public DossierAftersalesResponse executeDossierAfterSale(
			Context context, Order order, String languageBeforSetting, boolean isNeedCatchCustomerError, boolean isUpload)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;
	
	/**
	 * Call web service asynchronously and get Json file
	 * @param context
	 * @return StationBoardResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws NoTicket
	 * @throws TimeOutError
	 * @throws RequestFail
	 * @throws ParseException
	 * @throws IOException
	 */
	/*public StationBoardResponse executeStationBoard(StationBoardQuery stationBoardQuery, String language, Context context) 
			throws InvalidJsonError,JSONException, TimeOutError, RequestFail, ParseException, 
			IOException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError;*/
	
	/**
	 * Call web service asynchronously and get Json file
	 * @param context
	 * @return StationBoardResponse
	 * @throws InvalidJsonError
	 * @throws JSONException
	 * @throws NoTicket
	 * @throws TimeOutError
	 * @throws RequestFail
	 * @throws ParseException
	 * @throws IOException
	 */
	public StationDetailResponse executeStationDetail(String stationCode , String language, Context context) 
			throws InvalidJsonError,JSONException, TimeOutError, RequestFail, ParseException, IOException, 
			ConnectionError, BookingTimeOutError;
	
	public boolean reBuildOrder(Context context, DossierAftersalesResponse dossierAftersalesResponse, Order order, 
			String dossierAftersalesLifetime) throws DonotContainTicket, JourneyPast;
	
	
	public void deletePastedFile(Context context, DossierAftersalesResponse dossierAftersalesResponse);
	public GeneralSetting executeGeneralSetting(Context context) throws Exception;
	public DossierAftersalesResponse getDossierAftersalesResponseFromFile(Context context, String DNR);
}
