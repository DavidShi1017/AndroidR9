package com.cfl.dataaccess.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cfl.model.FavoriteStation;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteStationsDatabaseService {
	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;
	
	// Database fields
	public static final String DB_TABLE_STATION_FAVORITE = "StationFavorite";
	public static final String Column_Id = "_id";
	public static final String STATION_CODE = "station_code";
	
	public FavoriteStationsDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Insert data to table.
	 *
	 *            order
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertStationCode(String code) {
		sqLiteDatabase.beginTransaction();
		ContentValues contentValues = new ContentValues();
		contentValues.put(STATION_CODE, code);

		try {
			sqLiteDatabase.insert(DB_TABLE_STATION_FAVORITE, code, contentValues);
			sqLiteDatabase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		}finally{
			sqLiteDatabase.endTransaction();
		}
	}
	
	public boolean deleteStationCode(String stationCode) {
		sqLiteDatabase.beginTransaction();
		try {
			sqLiteDatabase.delete(DB_TABLE_STATION_FAVORITE, STATION_CODE +"=?", new String[]{stationCode});
			sqLiteDatabase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		}finally{
			sqLiteDatabase.endTransaction();
		}

	}
	
	public void startTransacstion(){
		sqLiteDatabase.beginTransaction();
	}
	
	public void endTransaction(){
		sqLiteDatabase.setTransactionSuccessful();
		sqLiteDatabase.endTransaction();
	}
	
	public boolean isExistStationFavorite(String code) {

		Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATION_FAVORITE, null, STATION_CODE + "='" + code + "'", null, null, null, null);
		int cursorNum = cursor.getCount();
		if(cursorNum > 0){
			return true;
		}
		cursor.close();
		return false;
	}

	public List<String> readStationCodeList() {
		List<String> favoriteStations = new ArrayList<String>();
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_STATION_FAVORITE, null, null, null, null, null, null);
		int cursorNum = cursor.getCount();
		try{
			for (int i = 0; i < cursorNum; i++) {
				cursor.moveToPosition(i);
				String code = cursor.getString(cursor.getColumnIndexOrThrow(STATION_CODE));
				favoriteStations.add(code);
			}
		}catch (Exception e){

		}finally {
			cursor.close();
		}


		return favoriteStations;
	}
	
}
