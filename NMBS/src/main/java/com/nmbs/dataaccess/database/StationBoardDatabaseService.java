package com.nmbs.dataaccess.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmbs.model.StationBoard;
import com.nmbs.model.StationBoardBulk;
import com.nmbs.model.StationBoardBulkResponse;

import com.nmbs.model.TravelRequest.TimePreference;
import com.nmbs.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class StationBoardDatabaseService {
	
	private static final String TAG = StationBoardDatabaseService.class.getSimpleName();
	public static final String DB_TABLE_STATIONBOARD = "StationBoard";
	public static final String ID = "_id";
	public static final String STATIONBOARD_ID = "StationBoardId";
	public static final String STATIONBOARD_CODE = "StationRCode";	
	public static final String STATIONBOARD_DATETIME = "DateTime";
	public static final String STATIONBOARD_TIMEPREFERENCE = "TimePreference";
	public static final String STATIONBOARD_TRAINCATEGORY = "TrainCategory";
	public static final String STATIONBOARD_TRAINNUMBER = "TrainNumber";
	public static final String STATIONBOARD_TYPE = "Type";
	public static final String STATIONBOARD_SORTDATE = "SortDate";
	public static final String STATIONBOARD_DNR = "Dnr";
	public static final String STATIONBOARD_ORIGIN_NAME = "OriginStationName";
	public static final String STATIONBOARD_DESTINATION_NAME = "DestinationStationName";
	
	public static final String STATIONBOARD_REALTIME_CALLSUCCESSFUL = "CallSuccessFul";	
	
	public static final String STATIONBOARD_REALTIME_DELAY = "Delay";
	public static final String STATIONBOARD_REALTIME_ISCANCELLED = "IsCancelled";
	public static final String STATIONBOARD_PARENT_ID = "ParentId";
	public static final String STATIONBOARD_TS_ID = "TsId";
    private SQLiteDatabase sqLiteDatabase;  
    private DatabaseHelper dbHelper;  
  
    public StationBoardDatabaseService(Context context) { 
        dbHelper = DatabaseHelper.getInstance(context);  
        sqLiteDatabase = dbHelper.getWritableDatabase();
    } 
    
    /**
     * Insert data to table.
     * @param StationBoard stationBoard
   	 * @return true means everything is OK, otherwise means failure
     */
    public boolean insertStationBoard(StationBoard stationBoard) {
		if (stationBoard != null ) {
			ContentValues contentValues = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			try{
				
				contentValues.put(STATIONBOARD_ID, stationBoard.getId());
				contentValues.put(STATIONBOARD_CODE, stationBoard.getStationRCode());
				contentValues.put(STATIONBOARD_DATETIME, DateUtils.dateTimeToString(stationBoard.getDateTime()));
				contentValues.put(STATIONBOARD_TIMEPREFERENCE, stationBoard.getTimePreference().ordinal());
				contentValues.put(STATIONBOARD_TRAINCATEGORY, stationBoard.getTrainCategory());
				contentValues.put(STATIONBOARD_TRAINNUMBER, stationBoard.getTrainNumber());
				contentValues.put(STATIONBOARD_TYPE, stationBoard.getType());
				contentValues.put(STATIONBOARD_SORTDATE, DateUtils.dateTimeToString(stationBoard.getSortDate()));
				contentValues.put(STATIONBOARD_DNR, stationBoard.getDnrStr());
				contentValues.put(STATIONBOARD_ORIGIN_NAME, stationBoard.getOriginStationName());
				contentValues.put(STATIONBOARD_DESTINATION_NAME, stationBoard.getDestinationStationName());
				
				contentValues.put(STATIONBOARD_REALTIME_CALLSUCCESSFUL, String.valueOf(stationBoard.isCallSuccessFul()));
				contentValues.put(STATIONBOARD_REALTIME_DELAY, stationBoard.getDelay());
				contentValues.put(STATIONBOARD_REALTIME_ISCANCELLED, String.valueOf(stationBoard.isCancelled()));
				contentValues.put(STATIONBOARD_PARENT_ID, stationBoard.getParentId());
				contentValues.put(STATIONBOARD_TS_ID, stationBoard.getTravelSegmentID());
				sqLiteDatabase.insert(DB_TABLE_STATIONBOARD , ID, contentValues);							
							
				//Log.d(tag, "Insert data to TABLE= "+DB_TABLE_STATION);		
				sqLiteDatabase.setTransactionSuccessful();				
			}finally{
				sqLiteDatabase.endTransaction();
			}
			return true;
		}else {
			//Log.d(tag, "There is no data was inserted.");
			return false;
		}
	}	
    
    public Map<String, List<StationBoard>> insertStationBoards(List<StationBoard> stationBoards) {
    	List<StationBoard> sameStationBoards = null;
    	Map<String, List<StationBoard>> sameStationBoardsMap = null;
		if (stationBoards != null ) {
			ContentValues contentValues = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			try{
				for (StationBoard stationBoard : stationBoards) {
					StationBoard sameStationBoard = selectSameStationBoard(stationBoard);
					if (sameStationBoard != null) {
						Log.d(TAG, "Has same stationboard= ");	
						sameStationBoards = new ArrayList<StationBoard>();
						sameStationBoards.add(stationBoard);
						sameStationBoardsMap = new HashMap<String, List<StationBoard>>();
						sameStationBoardsMap.put(sameStationBoard.getId(), sameStationBoards);
					}else {
						contentValues.put(STATIONBOARD_ID, stationBoard.getId());
						contentValues.put(STATIONBOARD_CODE, stationBoard.getStationRCode());
						contentValues.put(STATIONBOARD_DATETIME, DateUtils.dateTimeToString(stationBoard.getDateTime()));
						contentValues.put(STATIONBOARD_TIMEPREFERENCE, stationBoard.getTimePreference().ordinal());
						contentValues.put(STATIONBOARD_TRAINCATEGORY, stationBoard.getTrainCategory());
						contentValues.put(STATIONBOARD_TRAINNUMBER, stationBoard.getTrainNumber());
						contentValues.put(STATIONBOARD_TYPE, stationBoard.getType());
						contentValues.put(STATIONBOARD_SORTDATE, DateUtils.dateTimeToString(stationBoard.getSortDate()));
						contentValues.put(STATIONBOARD_DNR, stationBoard.getDnrStr());
						contentValues.put(STATIONBOARD_ORIGIN_NAME, stationBoard.getOriginStationName());
						contentValues.put(STATIONBOARD_DESTINATION_NAME, stationBoard.getDestinationStationName());
						
						contentValues.put(STATIONBOARD_REALTIME_CALLSUCCESSFUL, String.valueOf(stationBoard.isCallSuccessFul()));
						contentValues.put(STATIONBOARD_REALTIME_DELAY, stationBoard.getDelay());
						contentValues.put(STATIONBOARD_REALTIME_ISCANCELLED, String.valueOf(stationBoard.isCancelled()));
						contentValues.put(STATIONBOARD_PARENT_ID, stationBoard.getParentId());
						contentValues.put(STATIONBOARD_TS_ID, stationBoard.getTravelSegmentID());
						sqLiteDatabase.insert(DB_TABLE_STATIONBOARD , ID, contentValues);			
					}
					
				}															
				//Log.d(tag, "Insert data to TABLE= "+DB_TABLE_STATION);		
				sqLiteDatabase.setTransactionSuccessful();				
			}finally{
				sqLiteDatabase.endTransaction();
			}
			
		}
		return sameStationBoardsMap;
	}	
    
    
    public List<StationBoard> selectStationBoardsCollection(boolean isTypeA, String dnrId) throws SQLException {

		String sql = selectSqlSentence(isTypeA, dnrId);
		// Log.d(tag, "sql is : " + sql);
		// Log.d(tag, "Select all data.");

		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());
		List<StationBoard> stationBoards = new ArrayList<StationBoard>();

		for (int i = 0; i < cursorNum; i++) {

			cursor.moveToPosition(i);

			String id = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			//Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			StationBoard stationBoard = new StationBoard(id, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnr, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentId, tsId);
			
			
			stationBoards.add(stationBoard);
		}

		cursor.close();

		return stationBoards;
	}
    
    public List<StationBoard> selectRealTimeOfChildTravelSegmentsByParentIdWithTypeA(String parentId, String dnr, boolean isAll) throws SQLException {
    	
    	Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);
		Date fewLaterDayOfToday = DateUtils.getFewLaterDay(nowTime, 3);
		String fewLaterDayOfTodayStr = DateUtils.dateToString(fewLaterDayOfToday);
		Cursor cursor = null;
		if (isAll) {
	    	cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
	    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
	    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
	    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
	    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
	    			, STATIONBOARD_PARENT_ID + " = '" + parentId + "' and " + STATIONBOARD_DATETIME + " >= '" + nowTimeStr + "' and " 
	    					+ STATIONBOARD_TYPE + " = 'A' and "
	    					+ STATIONBOARD_DNR + " = '" + dnr + "'"
	    			, null, null, null, null);
		}else {
	    	cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
	    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
	    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
	    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
	    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
	    			, STATIONBOARD_PARENT_ID + " = '" + parentId + "' and " + STATIONBOARD_DATETIME + " >= '" + nowTimeStr + "' and " 
	    					+ STATIONBOARD_DATETIME + " <= '" + fewLaterDayOfTodayStr + "' and " + STATIONBOARD_TYPE + " = 'A' and "
	    					+ STATIONBOARD_DNR + " = '" + dnr + "'"
	    			, null, null, null, null);
		}

//		Log.d(TAG, "parentId:::" + parentId);
//		Log.d(TAG, "nowTimeStr:::" + nowTimeStr);
//		Log.d(TAG, "fewLaterDayOfTodayStr:::" + fewLaterDayOfTodayStr);
//		Log.d(TAG, "fewLaterDayOfTodayStr:::" + fewLaterDayOfTodayStr);
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());
		List<StationBoard> stationBoards = new ArrayList<StationBoard>();

		for (int i = 0; i < cursorNum; i++) {

			cursor.moveToPosition(i);

			String id = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnrInDatabase = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			//Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			//Log.d(TAG, "boolean isCancelled=======" + cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentIdStr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			StationBoard stationBoard = new StationBoard(id, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnrInDatabase, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentIdStr, tsId);
			
			
			stationBoards.add(stationBoard);
		}

		cursor.close();

		return stationBoards;
	}
    
    public List<StationBoard> selectRealTimeOfChildTravelSegmentsByParentIdWithNotTypeA(String parentId, String dnr, boolean isAll) throws SQLException {
    	
    	Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);
		Date fewLaterDayOfToday = DateUtils.getFewLaterDay(nowTime, 3);
		String fewLaterDayOfTodayStr = DateUtils.dateToString(fewLaterDayOfToday);
		Cursor cursor = null;
		if (isAll) {
	    	cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
	    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
	    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
	    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
	    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
	    			, STATIONBOARD_PARENT_ID + " = '" + parentId + "' and " + STATIONBOARD_DATETIME + " >= '" + nowTimeStr + "' and " 
	    					+ STATIONBOARD_TYPE + " != 'A' and " 
	    					+ STATIONBOARD_TYPE + " != 'C' and "
	    					+ STATIONBOARD_DNR + " = '" + dnr + "'"
	    			, null, null, null, null);
		}else {
	    	cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
	    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
	    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
	    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
	    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
	    			, STATIONBOARD_PARENT_ID + " = '" + parentId + "' and " + STATIONBOARD_DATETIME + " >= '" + nowTimeStr + "' and " 
	    					+ STATIONBOARD_DATETIME + " <= '" + fewLaterDayOfTodayStr + "' and " + STATIONBOARD_TYPE + " != 'A' and " 
	    					+ STATIONBOARD_TYPE + " != 'C' and "
	    					+ STATIONBOARD_DNR + " = '" + dnr + "'"
	    			, null, null, null, null);
		}

//		Log.d(TAG, "parentId:::" + parentId);
//		Log.d(TAG, "nowTimeStr:::" + nowTimeStr);
//		Log.d(TAG, "fewLaterDayOfTodayStr:::" + fewLaterDayOfTodayStr);
//		Log.d(TAG, "fewLaterDayOfTodayStr:::" + fewLaterDayOfTodayStr);
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());
		List<StationBoard> stationBoards = new ArrayList<StationBoard>();

		for (int i = 0; i < cursorNum; i++) {

			cursor.moveToPosition(i);

			String id = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnrInDatabase = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			//Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			//Log.d(TAG, "boolean isCancelled=======" + cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentIdStr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			StationBoard stationBoard = new StationBoard(id, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnrInDatabase, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentIdStr, tsId);
			
			
			stationBoards.add(stationBoard);
		}

		cursor.close();

		return stationBoards;
	}
    
    public StationBoard selectRealTimeOfSelfTravelSegmentsById(String id) throws SQLException {
    	
    	StationBoard stationBoard = null;
    	Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);
		Date fewLaterDayOfToday = DateUtils.getFewLaterDay(nowTime, 3);
		String fewLaterDayOfTodayStr = DateUtils.dateToString(fewLaterDayOfToday);
    	Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
    			, STATIONBOARD_TS_ID + " = '" + id + "' and " + STATIONBOARD_DATETIME + " >= '" + nowTimeStr + "' and " 
    					+ STATIONBOARD_DATETIME + " <= '" + fewLaterDayOfTodayStr + "' and " + STATIONBOARD_TYPE + " = 'A'"
    			, null, null, null, null);
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());

		

		if (cursorNum > 0) {
			cursor.moveToFirst();

			String stationBoardid = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			//Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentIdStr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			stationBoard = new StationBoard(stationBoardid, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnr, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentIdStr, tsId);
			
			
			
		
		}
		cursor.close();

		return stationBoard;
	}
    
    
    private StationBoard selectSameStationBoard(StationBoard stationBoard) throws SQLException {
    	
    	StationBoard sameStationBoard = null;

    	Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
    			, STATIONBOARD_CODE + " = '" + stationBoard.getStationRCode() + "' and " + STATIONBOARD_DATETIME + " = '" + DateUtils.dateTimeToString(stationBoard.getDateTime()) + "' and " 
    					+ STATIONBOARD_TRAINCATEGORY + " = '" + stationBoard.getTrainCategory() + "' and " + STATIONBOARD_TRAINNUMBER + " = '"+stationBoard.getTrainNumber() +"'"
    			, null, null, null, null);
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());

		

		if (cursorNum > 0) {
			cursor.moveToFirst();

			String stationBoardid = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			//Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentIdStr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			sameStationBoard = new StationBoard(stationBoardid, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnr, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentIdStr, tsId);
			
			
			
		
		}
		cursor.close();

		return sameStationBoard;
	}
    
    public StationBoard selectRealTimeTravelSegmentsById(String id) throws SQLException {
    	
    	StationBoard stationBoard = null;
    	

    	Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
    			, STATIONBOARD_TS_ID + " = '" + id +"'"
    			, null, null, null, null);
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());

		

		if (cursorNum > 0) {
			cursor.moveToFirst();

			String stationBoardid = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentIdStr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			stationBoard = new StationBoard(stationBoardid, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnr, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentIdStr, tsId);
			
			
			
		
		}
		cursor.close();

		return stationBoard;
	}
    
    public List<StationBoard> selectDuplicatedStationBoardById(String id) throws SQLException {
    	
    	
    	

    	Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD, new String[] {
    			STATIONBOARD_ID, STATIONBOARD_CODE, STATIONBOARD_DATETIME, STATIONBOARD_TIMEPREFERENCE, STATIONBOARD_TRAINCATEGORY
    			, STATIONBOARD_TRAINNUMBER, STATIONBOARD_TYPE, STATIONBOARD_SORTDATE, STATIONBOARD_DNR
    			, STATIONBOARD_ORIGIN_NAME, STATIONBOARD_DESTINATION_NAME, STATIONBOARD_REALTIME_CALLSUCCESSFUL
    			, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED, STATIONBOARD_PARENT_ID, STATIONBOARD_TS_ID}
    			, STATIONBOARD_ID + " = '" + id +"'"
    			, null, null, null, null);
		// Log.d(tag, "order cursor is:");
    	int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());
		List<StationBoard> stationBoards = new ArrayList<StationBoard>();

		for (int i = 0; i < cursorNum; i++) {

			cursor.moveToPosition(i);

			String stationId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ID));
			String stationRCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DATETIME));
			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(STATIONBOARD_TIMEPREFERENCE));
			String trainCategory = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINCATEGORY));
			String trainNumber = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TRAINNUMBER));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TYPE));
			String sortDate = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_SORTDATE));
			String dnrInDatabase = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DNR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_ORIGIN_NAME));
			String destinationName = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_DESTINATION_NAME));
			
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			//Log.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			String delay = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			//Log.d(TAG, "boolean isCancelled=======" + cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
			String parentIdStr = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_PARENT_ID));
			String tsId = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_TS_ID));
			TimePreference timePreference = null;
			if (ordinal == 0) {
				timePreference = TimePreference.DEPARTURE;
			}else {
				timePreference = TimePreference.ARRIVAL;
			}
			
			StationBoard stationBoard = new StationBoard(stationId, stationRCode, DateUtils.stringToDateTime(dateTime), 
					timePreference, trainCategory, trainNumber, type, DateUtils.stringToDateTime(sortDate), dnrInDatabase, originName, 
					destinationName, callSuccessFul, delay, isCancelled, parentIdStr, tsId);
			
			
			stationBoards.add(stationBoard);
		}

		cursor.close();

		return stationBoards;
	}
    
    public boolean updateStationboardRealtime(StationBoardBulkResponse stationBoardBulkResponse){
    	if (stationBoardBulkResponse != null ) {
			ContentValues contentValues = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			try{
				for (StationBoardBulk stationBoardBulk : stationBoardBulkResponse.getStationBoardBulks()) {
					//if (stationBoardBulk.isCallSuccessFul() == true) {
						
						contentValues.put(STATIONBOARD_REALTIME_CALLSUCCESSFUL, String.valueOf(stationBoardBulk.isCallSuccessFul()));
						contentValues.put(STATIONBOARD_REALTIME_DELAY, stationBoardBulk.getDelay());
						contentValues.put(STATIONBOARD_REALTIME_ISCANCELLED, String.valueOf(stationBoardBulk.isIsCancelled()));						
						//Log.d(TAG, "CallSuccessFul=======" + stationBoardBulk.isCallSuccessFul());
						//Log.e(TAG, "CallSuccessFul=======" + stationBoardBulk.isIsCancelled());
						sqLiteDatabase.update(DB_TABLE_STATIONBOARD, contentValues, STATIONBOARD_ID +" = '" + stationBoardBulk.getId() + "'", null);
					/*}else {
						break;
					}	*/									
				}																
				//Log.d(tag, "Insert data to TABLE= "+DB_TABLE_STATION);		
				sqLiteDatabase.setTransactionSuccessful();				
			}finally{
				sqLiteDatabase.endTransaction();
			}
			return true;
		}else {
			//Log.d(tag, "There is no data was inserted.");
			return false;
		}
    }
    

    
    private String selectSqlSentence(boolean isTypeA, String dnr) {

		String sql = "";
		Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);
		// Log.d(tag, "nowTime is : " + nowTimeStr);

		if (isTypeA) {
			Date fewLaterDayOfToday = DateUtils.getFewLaterDay(nowTime, 3);
			String fewLaterDayOfTodayStr = DateUtils.dateToString(fewLaterDayOfToday);
			Log.d(TAG, "nowTime is : " + nowTimeStr);
			if (dnr != null) {
				sql = "select * from StationBoard where StationBoard.DateTime >= '"
						+ nowTimeStr
						+ "' and StationBoard.DateTime <= '" + fewLaterDayOfTodayStr
						+ "' and StationBoard.type = 'A' " 
						+ "and StationBoard.Dnr = '" + dnr + "'";
			}else {
				sql = "select * from StationBoard where StationBoard.DateTime >= '"
						+ nowTimeStr
						+ "' and StationBoard.DateTime <= '" + fewLaterDayOfTodayStr
						+ "' and StationBoard.type = 'A'";
			}
			
			Log.d(TAG, "sql is : " + sql);
		}else {
				
			sql = "select * from StationBoard where StationBoard.DateTime >= '"
					+ nowTimeStr
					+ "' and StationBoard.type != 'Unknown'"
					+ " order by StationBoard.SortDate asc, StationBoard.Dnr asc";
		}
		
		return sql;

	}
	public void deleteStationBoardBulkById(String dnr) {
		sqLiteDatabase.delete(DB_TABLE_STATIONBOARD, STATIONBOARD_DNR + "='"
				+ dnr + "'", null);
	}
}
