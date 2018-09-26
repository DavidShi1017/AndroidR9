package com.nmbs.dataaccess.restservice;

import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;

import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.DonotContainTicket;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.JourneyPast;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Order;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.StationBoardResponse;
import com.nmbs.model.StationDetailResponse;

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
