package com.nmbs.dataaccess.database;

import java.util.ArrayList;

import java.util.List;


import com.nmbs.log.LogUtils;
import com.nmbs.model.StationBoardBulk;
import com.nmbs.model.StationBoardBulkResponse;
import com.nmbs.model.StationBoardResponse;


import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;


public class StationBoardBulkRealTimeDatabaseService {
	
	private static final String TAG = StationBoardBulkRealTimeDatabaseService.class.getSimpleName();
	public static final String DB_TABLE_STATIONBOARD_REALTIME = "StationBoardBulkRealTime";
	public static final String ID = "_id";
	public static final String STATIONBOARD_REALTIME_ID = "StationBoardRealTimeId";
	public static final String STATIONBOARD_REALTIME_CALLSUCCESSFUL = "CallSuccessFul";	
	
	public static final String STATIONBOARD_REALTIME_DELAY = "Delay";
	public static final String STATIONBOARD_REALTIME_ISCANCELLED = "IsCancelled";

	
	
    private SQLiteDatabase sqLiteDatabase;  
    private DatabaseHelper dbHelper;  
  
    public StationBoardBulkRealTimeDatabaseService(Context context) { 
        dbHelper = DatabaseHelper.getInstance(context);  
        sqLiteDatabase = dbHelper.getWritableDatabase();
    } 
    
    /**
     * Insert data to table.
     * @param StationBoardBulk StationBoardBulk
   	 * @return true means everything is OK, otherwise means failure
     */
    public boolean insertStationBoardBulkRealTime(StationBoardBulkResponse stationBoardBulkResponse) {
		if (stationBoardBulkResponse != null ) {
			ContentValues contentValues = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			try{
				for (StationBoardBulk stationBoardBulk : stationBoardBulkResponse.getStationBoardBulks()) {
					if (stationBoardBulk.isCallSuccessFul() == true) {
						deleteStationBoardBulkById(stationBoardBulk.getId());
					}else {
						break;
					}
					contentValues.put(STATIONBOARD_REALTIME_ID, stationBoardBulk.getId());
					contentValues.put(STATIONBOARD_REALTIME_CALLSUCCESSFUL, String.valueOf(stationBoardBulk.isCallSuccessFul()));
					contentValues.put(STATIONBOARD_REALTIME_DELAY, stationBoardBulk.getDelay());
					contentValues.put(STATIONBOARD_REALTIME_ISCANCELLED, stationBoardBulk.isIsCancelled());

					LogUtils.d(TAG, "CallSuccessFul=======" + stationBoardBulk.isCallSuccessFul());
					
					sqLiteDatabase.insert(DB_TABLE_STATIONBOARD_REALTIME , ID, contentValues);	
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
    
    
    public List<StationBoardBulk> selectStationBoardBulksCollection() throws SQLException {

		
		// Log.d(tag, "sql is : " + sql);
		// Log.d(tag, "Select all data.");
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD_REALTIME, new String[] {
				STATIONBOARD_REALTIME_ID, STATIONBOARD_REALTIME_CALLSUCCESSFUL, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED},
				null, null, null, null, null);
		
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		// Log.d(tag, "order cursor count is:" + cursor.getCount());
		List<StationBoardBulk> stationBoards = new ArrayList<StationBoardBulk>();

		for (int i = 0; i < cursorNum; i++) {

			cursor.moveToPosition(i);

			String id = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ID));
			boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
			LogUtils.d(TAG, "boolean callSuccessFul=======" + callSuccessFul);
			double delay = cursor.getDouble(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
			boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
						
			StationBoardBulk stationBoard = new StationBoardBulk(id, callSuccessFul, delay, isCancelled);			
			
			stationBoards.add(stationBoard);
		}

		cursor.close();

		return stationBoards;
	}

    public StationBoardBulk selectStationBoardBulkById(String idString){
    	Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONBOARD_REALTIME, new String[] {
				STATIONBOARD_REALTIME_ID, STATIONBOARD_REALTIME_CALLSUCCESSFUL, STATIONBOARD_REALTIME_DELAY, STATIONBOARD_REALTIME_ISCANCELLED},
				STATIONBOARD_REALTIME_ID + " = '" + idString , null, null, null, null);
		
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		if (cursorNum > 0)
			cursor.moveToFirst();
		
		String id = cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ID));
		boolean callSuccessFul = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_CALLSUCCESSFUL)));
		double delay = cursor.getDouble(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_DELAY));
		boolean isCancelled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(STATIONBOARD_REALTIME_ISCANCELLED)));
		
		
		StationBoardBulk stationBoard = new StationBoardBulk(id, callSuccessFul, delay, isCancelled);

		cursor.close();

		return stationBoard;
    }

	public void deleteStationBoardBulkById(String id) {
		sqLiteDatabase.delete(DB_TABLE_STATIONBOARD_REALTIME, STATIONBOARD_REALTIME_ID + "='"
				+ id + "'", null);
	}
}
