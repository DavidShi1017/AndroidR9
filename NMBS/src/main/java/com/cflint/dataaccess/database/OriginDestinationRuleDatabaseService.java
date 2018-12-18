package com.cflint.dataaccess.database;

import java.util.ArrayList;
import java.util.List;

import com.cflint.application.NMBSApplication;
import com.cflint.model.Destination;
import com.cflint.model.Origin;
import com.cflint.model.OriginDestinationRule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;


public class OriginDestinationRuleDatabaseService {

	//private static final String tag = OriginDestinationRuleDatabaseService.class.getSimpleName();

	// Database fields
	public static final String DB_TABLE_STATIONMATRIX = "StationMatrix";
	public static final String STATIONMATRIX_ID = "_id";
	public static final String STATIONMATRIX_STATION_FROM_CODE = "StationCodeFrom";
	public static final String STATIONMATRIX_STATION_TO_CODE = "SationCodeTo";

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;

	public OriginDestinationRuleDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}

	/**
	 * Insert data to table.
	 *
	 * @param originDestinationRules
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertStationMatrix(List<OriginDestinationRule> originDestinationRules) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		if (originDestinationRules != null) {

			sqLiteDatabase.beginTransaction();
			try {
				for (OriginDestinationRule originDestinationRule : originDestinationRules) {

					List<Origin> origins = originDestinationRule.getOrigins();
					List<Destination> destinations = originDestinationRule.getDestinations();
					if(originDestinationRule.isApplicableForReverse()){
						insertStationMatrixApplicableForReverseTrue(origins, destinations);
						insertStationMatrixApplicableForReverseFalse(origins, destinations);
					}else{
						insertStationMatrixApplicableForReverseFalse(origins, destinations);
					}
				}
				//Log.d(tag, "Insert data to TABLE= " + originDestinationRules);
				//Log.d(tag, "Insert data to TABLE= " + DB_TABLE_STATIONMATRIX);
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
			return true;
		} else {
			//Log.d(tag, "There is no data was inserted.");
			return false;
		}
	}
	public void insertStationMatrixApplicableForReverseTrue(List<Origin> origins, List<Destination> destinations){
		ContentValues contentValues = new ContentValues();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return;
		}
		for (Destination destination : destinations) {
			contentValues
					.put(STATIONMATRIX_STATION_FROM_CODE, destination.getCode());
			for (Origin origin : origins){
				contentValues.put(STATIONMATRIX_STATION_TO_CODE, origin.getCode());
				sqLiteDatabase.insert(DB_TABLE_STATIONMATRIX, STATIONMATRIX_ID,
						contentValues);
			}
		}
	}
	public void insertStationMatrixApplicableForReverseFalse(List<Origin> origins, List<Destination> destinations){

		ContentValues contentValues = new ContentValues();

		for (Origin origin : origins) {
			contentValues
					.put(STATIONMATRIX_STATION_FROM_CODE, origin.getCode());
			for (Destination destination : destinations){
				contentValues.put(STATIONMATRIX_STATION_TO_CODE, destination.getCode());
				sqLiteDatabase.insert(DB_TABLE_STATIONMATRIX, STATIONMATRIX_ID,
						contentValues);
			}
		}
	}
	/**
	 * Select all data from SQLite, add them to ArrayList
	 * @return CurrencyResponse
	 * @throws SQLException
	 */
	public List<String> selectFromStationCodes() throws SQLException {
		List<String> fromStationCodes = new ArrayList<String>();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return fromStationCodes;
		}
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATIONMATRIX
				, new String[] { STATIONMATRIX_ID, STATIONMATRIX_STATION_FROM_CODE, STATIONMATRIX_STATION_TO_CODE}
				, null, null, null, null, null);
		int cursorNum = cursor.getCount();

		String fromStationCode = null;
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);

			fromStationCode = cursor.getString(cursor.getColumnIndexOrThrow(STATIONMATRIX_STATION_FROM_CODE));




			fromStationCodes.add(fromStationCode);
		}
		cursor.close();
		return fromStationCodes;
	}

	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteMasterData(String tableName) {
		int isDelete;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		isDelete = sqLiteDatabase.delete(tableName, null, null) ;
		//Log.d(tag, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}
}
