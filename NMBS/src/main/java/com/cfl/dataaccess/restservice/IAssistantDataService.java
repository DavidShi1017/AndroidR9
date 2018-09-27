package com.cfl.dataaccess.restservice;

import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.DonotContainTicket;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.JourneyPast;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.GeneralSetting;
import com.cfl.model.Order;
import com.cfl.model.StationBoardQuery;
import com.cfl.model.StationBoardResponse;
import com.cfl.model.StationDetailResponse;

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
