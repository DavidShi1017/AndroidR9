package com.nmbs.dataaccess.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.nmbs.model.ExtensionScheduleQuery;
import com.nmbs.model.TravelRequest;
import com.nmbs.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ScheduleQueryDataBaseService {

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;

	public static final String DB_TABLE_SCHEDULE_LAST_QUERY = "ScheduleLastQuery";
	public static final String LAST_QUERY_ID = "Schedule_Last_Query_ID";
	public static final String LAST_QUERY_ORIGIN_CODE = "origin_code";
	public static final String LAST_QUERY_DESTINATION_CODE = "destination_code";
	public static final String LAST_QUERY_VIA_CODE = "via_code";
	public static final String LAST_QUERY_DATE_TIME = "date_time";
	public static final String LAST_QUERY_TIME_PREFERENCE = "time_preference";
	public static final String LAST_QUERY_TRAIN_NR = "train_nr";
	public static final String LAST_QUERY_ORIGIN_NAME = "origin_name";
	public static final String LAST_QUERY_DES_NAME = "des_name";
	public static final String LAST_QUERY_VIA_NAME = "via_name";


	public ScheduleQueryDataBaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}

	public boolean insertScheduleQuery(String scheduleQueryID, String originCode, String desCode,
									   String viaCode, String dateTime, String timePreference, String trainNr,String originName,  String desName, String viaName) {
		if (!scheduleQueryID.equals("")) {
			if(isExistScheduleQuery(scheduleQueryID))
				deleteLastScheduleById(scheduleQueryID);
			ContentValues contentValues = new ContentValues();
			Log.e("ScheduleQuery", "insertScheduleQuery...dateTime..." + dateTime);
			sqLiteDatabase.beginTransaction();
			try {
				contentValues.put(LAST_QUERY_ID, scheduleQueryID);
				contentValues.put(LAST_QUERY_ORIGIN_CODE, originCode);
				contentValues.put(LAST_QUERY_DESTINATION_CODE, desCode);
				contentValues.put(LAST_QUERY_VIA_CODE, viaCode);
				contentValues.put(LAST_QUERY_DATE_TIME, dateTime);
				contentValues.put(LAST_QUERY_TIME_PREFERENCE, timePreference);
				contentValues.put(LAST_QUERY_TRAIN_NR, trainNr);
				contentValues.put(LAST_QUERY_ORIGIN_NAME, originName);
				contentValues.put(LAST_QUERY_DES_NAME, desName);
				contentValues.put(LAST_QUERY_VIA_NAME, viaName);

				sqLiteDatabase.insert(DB_TABLE_SCHEDULE_LAST_QUERY, LAST_QUERY_ID, contentValues);


				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		} else {
			return false;
		}
		return true;
	}

	public ExtensionScheduleQuery readScheduleQuery(String scheduleQueryId) {

		ExtensionScheduleQuery extensionScheduleQuery = null;
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_SCHEDULE_LAST_QUERY, new String[] {
				LAST_QUERY_ID, LAST_QUERY_ORIGIN_CODE, LAST_QUERY_DESTINATION_CODE,LAST_QUERY_VIA_CODE,LAST_QUERY_DATE_TIME,
				LAST_QUERY_TIME_PREFERENCE,LAST_QUERY_TRAIN_NR,LAST_QUERY_DES_NAME,LAST_QUERY_ORIGIN_NAME,LAST_QUERY_VIA_NAME}, LAST_QUERY_ID + " = '"
				+ scheduleQueryId + "'", null, null, null, null);
		if (cursor.getCount() == 0) {
			cursor.close();
		}else {			
			cursor.moveToFirst();	
			String originCode = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_ORIGIN_CODE));
			String desCode = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_DESTINATION_CODE));
			String viaCode = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_VIA_CODE));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_DATE_TIME));
			String preference = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_TIME_PREFERENCE));
			String trainNr = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_TRAIN_NR));
			String originName = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_ORIGIN_NAME));
			String desName = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_DES_NAME));
			String viaName = cursor.getString(cursor.getColumnIndexOrThrow(LAST_QUERY_VIA_NAME));
			Log.e("ScheduleQuery", "dateTime..." + dateTime);
			Date tempDate = null;
			if(!"".equals(dateTime)){
				tempDate = DateUtils.stringToDateTime(dateTime, "dd MMM yyyy - HH:mm");
			}else{
				tempDate = new Date();
			}
			Log.e("ScheduleQuery", "tempDate..." + tempDate);

			extensionScheduleQuery = new ExtensionScheduleQuery(originCode,desCode,viaCode,trainNr,tempDate,Enum.valueOf(TravelRequest.TimePreference.class, preference),originName,desName,viaName);

			cursor.close();
		}
		
		return extensionScheduleQuery;
	}
	
	
	public boolean isExistScheduleQuery(String OfferQueryId) {
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_SCHEDULE_LAST_QUERY, new String[] {
				LAST_QUERY_ID, LAST_QUERY_ORIGIN_CODE, LAST_QUERY_DESTINATION_CODE,LAST_QUERY_VIA_CODE,LAST_QUERY_DATE_TIME,
				LAST_QUERY_TIME_PREFERENCE,LAST_QUERY_TRAIN_NR,LAST_QUERY_DES_NAME,LAST_QUERY_ORIGIN_NAME,LAST_QUERY_VIA_NAME}, LAST_QUERY_ID + " = '"
				+ OfferQueryId + "'", null, null, null, null);

		int cursorNum = cursor.getCount();
		cursor.close();
		if (cursorNum > 0)
			return true;
		else
			return false;
	}

	public void deleteLastScheduleById(String OfferQueryId) {
		sqLiteDatabase.delete(DB_TABLE_SCHEDULE_LAST_QUERY, LAST_QUERY_ID + "='"
				+ OfferQueryId + "'", null);
	}

}
