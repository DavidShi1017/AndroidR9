package com.cflint.dataaccess.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;


import com.cflint.model.Station;


/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage Station.
 */
public class StationDatabaseService {
	private static final String TAG = StationDatabaseService.class.getSimpleName();

	// Database fields   
	public static final String DB_TABLE_STATION = "station";
	public static final String STATION_ID = "_id";
	public static final String STATION_CODE = "code";
	public static final String STATION_NAME = "name";
	public static final String STATION_DETAIL_INFO_PATH = "detailInfoPath";
	public static final String STATION_DESTINATION = "Destination";
	public static final String STATION_SYNONIEM = "Synoniem";




	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;

	public StationDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		sqLiteDatabase = dbHelper.getWritableDatabase();

	}




	/**
	 * Insert data to table.
	 *
	 * @param
	 *
	 * @return true means everything is OK, otherwise means failure
	 */

	public void startTransacstion(){
		sqLiteDatabase.beginTransaction();
	}

	public void endTransaction(){
		sqLiteDatabase.setTransactionSuccessful();
		sqLiteDatabase.endTransaction();
	}






	/**
	 * Insert data to table.
	 * @param  stations
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertStationCollection(List<Station> stations) {
		if (stations != null ) {
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try{
				for (Station station : stations) {
					contentValues.put(STATION_CODE, station.getCode());
					contentValues.put(STATION_NAME, station.getName());
					contentValues.put(STATION_DETAIL_INFO_PATH, station.getDetailInfoPath());
					contentValues.put(STATION_DESTINATION, station.getDestination());
					contentValues.put(STATION_SYNONIEM, station.getSynoniem());
					sqLiteDatabase.insert(DB_TABLE_STATION , STATION_ID, contentValues);
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



	/**
	 * Select all data from SQLite, add them to ArrayList
	 * @return StationResponse
	 * @throws SQLException
	 */
	public List<Station> selectStationCollection(int fromOrTo, String stationFromCode) throws SQLException {



		String sql = selectSqlSentence(fromOrTo, stationFromCode);
		//Log.d(tag, "Select all data....." + sql);

		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		//Log.d(tag, "Select all data.");
		//Log.d(tag, "Select all data." + cursor.getCount());
		int cursorNum = cursor.getCount();
		//Log.d(tag, "Select all data...." + cursorNum);
		List<Station> listStations = new ArrayList<Station>();
		Station station = null;
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String code = cursor.getString(cursor.getColumnIndexOrThrow(STATION_CODE));
			String name = cursor.getString(cursor.getColumnIndexOrThrow(STATION_NAME));
			String detailInfoPath = cursor.getString(cursor.getColumnIndexOrThrow(STATION_DETAIL_INFO_PATH));
			String destination = cursor.getString(cursor.getColumnIndexOrThrow(STATION_DESTINATION));
			String synoniem = cursor.getString(cursor.getColumnIndexOrThrow(STATION_SYNONIEM));
			station = new Station(code, name, detailInfoPath, destination, synoniem, synoniem + " " + name);
			listStations.add(station);
		}

		cursor.close();
		return listStations;
	}
	/**
	 * Select all data from SQLite, add them to ArrayList
	 * @return StationResponse
	 * @throws SQLException
	 */
	public List<Station> selectStationCollectionByStationCode(List<String> stationCodes) throws SQLException {

		List<Station> listStations = new ArrayList<Station>();
		Station station = null;
		int stationCount = 0;
		if(stationCodes != null ){
			stationCount = stationCodes.size();
		}
		//Log.d(tag, "stationCodes size is." + stationCount );
		for(int j = 0; j < stationCount; j++){
			Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATION
					, new String[] { STATION_ID, STATION_CODE, STATION_NAME , STATION_DETAIL_INFO_PATH, STATION_DESTINATION}
					, STATION_CODE + " = '" + stationCodes.get(j) + "'" , null, null, null, null);
			//Log.d(tag, "Select all data.");
			sqLiteDatabase.beginTransaction();
			int cursorNum = cursor.getCount();

			for (int i = 0; i < cursorNum; i++) {
				cursor.moveToPosition(i);
				String code = cursor.getString(cursor.getColumnIndexOrThrow(STATION_CODE));
				String name = cursor.getString(cursor.getColumnIndexOrThrow(STATION_NAME));
				String detailInfoPath = cursor.getString(cursor.getColumnIndexOrThrow(STATION_DETAIL_INFO_PATH));
				String destination = cursor.getString(cursor.getColumnIndexOrThrow(STATION_DESTINATION));
				String synoniem = cursor.getString(cursor.getColumnIndexOrThrow(STATION_SYNONIEM));
				station = new Station(code, name, detailInfoPath, destination, synoniem, synoniem + " " + name);
				listStations.add(station);
			}
			sqLiteDatabase.endTransaction();
			cursor.close();
		}
		return listStations;
	}

	/**
	 * Select one Station StationCode by from SQLite
	 * @return StationResponse
	 * @throws SQLException
	 */
	public Station selectStationByStationCode(String stationCodes)
			throws SQLException {

		Station station = null;

		Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATION, new String[] {
				STATION_ID, STATION_CODE, STATION_NAME,
				STATION_DETAIL_INFO_PATH, STATION_DESTINATION }, STATION_CODE
				+ " = '" + stationCodes + "'", null, null, null, null);
		//Log.d(tag, "Select all data.");
		sqLiteDatabase.beginTransaction();
		int cursorNum = cursor.getCount();

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String code = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_CODE));
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_NAME));
			String detailInfoPath = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_DETAIL_INFO_PATH));
			String destination = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_DESTINATION));
			station = new Station(code, name, detailInfoPath, destination, "", "");

		}
		sqLiteDatabase.endTransaction();
		cursor.close();

		return station;
	}

	/**
	 * Select one Station StationCode by from SQLite
	 * @return StationResponse
	 * @throws SQLException
	 */
	public Station selectStationByStationName(String stationName)
			throws SQLException {

		Station station = null;

		Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATION, new String[] {
				STATION_ID, STATION_CODE, STATION_NAME,
				STATION_DETAIL_INFO_PATH, STATION_DESTINATION }, STATION_NAME
				+ " = '" + stationName + "'", null, null, null, null);
		//Log.d(tag, "Select all data.");
		sqLiteDatabase.beginTransaction();
		int cursorNum = cursor.getCount();

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String code = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_CODE));
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_NAME));
			String detailInfoPath = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_DETAIL_INFO_PATH));
			String destination = cursor.getString(cursor
					.getColumnIndexOrThrow(STATION_DESTINATION));
			String synoniem = cursor.getString(cursor.getColumnIndexOrThrow(STATION_SYNONIEM));
			station = new Station(code, name, detailInfoPath, destination, synoniem, synoniem + " " + name);

		}
		sqLiteDatabase.endTransaction();
		cursor.close();

		return station;
	}


	private String selectSqlSentence(int fromOrTo, String stationCode){
		//LogUtils.d(TAG, "stationCode...." + stationCode);
		//fromOrTo = 0;
		String sql = "";
		switch (fromOrTo) {
			case 0:
				sql = "select * from station where station.name != '' order by station.name";
				break;

			case 1:
				sql = "select distinct code, station.name, station.detailInfopath, station.Destination, station.Synoniem " +
						"from StationMatrix,Station on StationCodeFrom = station.Code order by station.name";

				break;
			case 2:
				sql = "select distinct station.code, station.name, station.detailInfopath, station.Destination, station.Synoniem " +
						"from StationMatrix inner join Station on StationMatrix.[SationCodeTo] = station.Code " +
						"where StationMatrix.stationcodefrom = '" + stationCode + "' and station.name != '' order by station.name";
				break;
		}
		return sql;
	}

	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteMasterData(String tableName) {
		int isDelete;
		isDelete = sqLiteDatabase.delete(tableName, null, null) ;
		//Log.d(tag, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}
}
