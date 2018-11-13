package com.cflint.dataaccess.restservice;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;


import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.DBooking343Error;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.DossierAftersalesResponse;
import com.cflint.model.Station;
import com.cflint.model.StationBoard;

import com.cflint.model.StationBoardCollection;
import com.cflint.model.StationBoardQuery;
import com.cflint.model.StationBoardResponse;

public interface IStationBoardDataService {

	public void createStationBoard(Context context, DossierAftersalesResponse dossierAftersalesResponse, String currentLanguage) throws JSONException, InvalidJsonError;
	 public StationBoardCollection getStationBoardWithTypeIsA(Context context, String dnr);
	public int executeStationBoardBulkQuery(StationBoardCollection stationBoardCollection, String language, Context context)
			throws InvalidJsonError, JSONException, TimeOutError, RequestFail,
			ParseException, IOException, ConnectionError, BookingTimeOutError, 
			DBooking343Error, CustomError, DBookingNoSeatAvailableError;
	public StationBoardResponse executeStationBoard(StationBoardQuery stationBoardQuery, String language, Context context)
			throws InvalidJsonError, JSONException, TimeOutError, RequestFail,
			ParseException, IOException, ConnectionError, BookingTimeOutError, 
			DBooking343Error, CustomError, DBookingNoSeatAvailableError;
	
	public List<StationBoard> getStationBoards(Context context);
	public List<Station> readHafasStations(Context context, String language) throws JSONException, InvalidJsonError;
	public List<StationBoard> getRealTimeForTravelSegments(Context context, String travelSegmentId, String dnr, boolean isAll);
	public StationBoard getParentTravelSegment(Context context, String travelSegmentId);
	
	public void saveCreateStationBoardStatus(Context context, boolean isSucced);
	public boolean getCreateStationBoardStatus(Context context);
	public List<StationBoard> getDuplicatedStationBoard(Context context, String id);
	public void saveStationBoardLastQuery(StationBoardQuery stationBoardQuery, Context context);
}
