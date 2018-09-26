package com.nmbs.dataaccess.restservice;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.Context;


import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.Station;
import com.nmbs.model.StationBoard;

import com.nmbs.model.StationBoardCollection;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.StationBoardResponse;

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
