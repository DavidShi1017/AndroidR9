package com.cfl.dataaccess.restservice;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;


import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.Station;
import com.cfl.model.StationBoard;

import com.cfl.model.StationBoardCollection;
import com.cfl.model.StationBoardQuery;
import com.cfl.model.StationBoardResponse;

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
