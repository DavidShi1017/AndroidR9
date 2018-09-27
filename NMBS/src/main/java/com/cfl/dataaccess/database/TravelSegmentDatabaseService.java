package com.cfl.dataaccess.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cfl.model.DossierTravelSegment;
import com.cfl.model.LocalNotification;
import com.cfl.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage City.
 */
public class TravelSegmentDatabaseService {

	private static final String TAG = TravelSegmentDatabaseService.class.getSimpleName();
	// Database fields
	public static final String DB_Dossier_TravelSegment = "DossierTravelSegment";
	public static final String DB_Dossier_TravelSegment_Date = "departureDate";
	public static final String Column_notification_id = "notificationId";
	public static final String Column_is_cancel = "isCancel";

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    public TravelSegmentDatabaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);  
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

	public boolean insertTravelSegment(DossierTravelSegment travelSegment,int id,boolean isCancel) {
		if (travelSegment != null ) {
			Log.d(TAG, "Insert a travelSegment for notification....." + travelSegment.getDepartureDateTime());
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try{
				String cancelFlag = "1";
				if(isCancel){
					cancelFlag = "0";
				}
				contentValues.put(DB_Dossier_TravelSegment_Date, DateUtils.dateTimeToString(travelSegment.getDepartureDateTime()));
				contentValues.put(Column_notification_id, id);
				contentValues.put(Column_is_cancel, cancelFlag);
				sqLiteDatabase.insert(DB_Dossier_TravelSegment , null, contentValues);
				sqLiteDatabase.setTransactionSuccessful();
			}finally{
				sqLiteDatabase.endTransaction();
			}
			return true;
		}else {
			Log.e(TAG, "DossierSummary is not inserted.");
			return false;
		}
	}

	public List<LocalNotification> getAllTravelSegment(Context context) {
		LocalNotification localNotification = null;
		List<LocalNotification> localNotifications = new ArrayList<LocalNotification>();
		Cursor cursor = sqLiteDatabase.query(DB_Dossier_TravelSegment, null, null, null, null, null, null);
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String date = cursor.getString(cursor.getColumnIndexOrThrow(DB_Dossier_TravelSegment_Date));
			String id = cursor.getString(cursor.getColumnIndexOrThrow(Column_notification_id));
			String isCancel = cursor.getString(cursor.getColumnIndexOrThrow(Column_is_cancel));
			localNotification = new LocalNotification(DateUtils.stringToDate(date),Integer.parseInt(id), isCancel == "0"? true : false);
			localNotifications.add(localNotification);
		}
		cursor.close();
		return localNotifications;
	}

	public boolean deleteTravelSegment(DossierTravelSegment dossierTravelSegment){
		if(dossierTravelSegment != null){
			Log.e(TAG, "Refresh dossier logic.........deleteTravelSegment.........Date is...." + DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime()));
			sqLiteDatabase.beginTransaction();
			try {
				int result = sqLiteDatabase.delete(DB_Dossier_TravelSegment, DB_Dossier_TravelSegment_Date +"=?", new String[]{DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime())});
				Log.e(TAG, "Refresh dossier logic.........deleteTravelSegment.........result...." + result);
				if(result == 0){
					int resultAgain = sqLiteDatabase.delete(DB_Dossier_TravelSegment, DB_Dossier_TravelSegment_Date +"=?", new String[]{DateUtils.dateToString(dossierTravelSegment.getDepartureDate())});
					Log.e(TAG, "Refresh dossier logic.........deleteTravelSegment.........resultAgain...." + resultAgain);
				}
				sqLiteDatabase.setTransactionSuccessful();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;

			}finally{
				sqLiteDatabase.endTransaction();
			}
		}
		return false;
	}

	public LocalNotification queryTravelSegmentByDate(Context context, String date){
		LocalNotification localNotification = null;
		Cursor cursor = sqLiteDatabase.query(DB_Dossier_TravelSegment, null, DB_Dossier_TravelSegment_Date+"='"+date+"'", null, null, null, null);
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String tempDate = cursor.getString(cursor.getColumnIndexOrThrow(DB_Dossier_TravelSegment_Date));
			String id = cursor.getString(cursor.getColumnIndexOrThrow(Column_notification_id));
			String isCancel = cursor.getString(cursor.getColumnIndexOrThrow(Column_is_cancel));
			localNotification = new LocalNotification(DateUtils.stringToDateTime(tempDate),Integer.parseInt(id),isCancel == "0"?true:false);
		}
		cursor.close();
		return localNotification;
	}

	public boolean updateTravelSegmentByDate(Context context, String date,boolean isCancel){
		sqLiteDatabase.beginTransaction();
		String cancelFlag = "1";
		if(isCancel){
			cancelFlag = "0";
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(Column_is_cancel, cancelFlag);
		try {
			sqLiteDatabase.update(DB_Dossier_TravelSegment, contentValues,
					DB_Dossier_TravelSegment_Date+"='"+date+"'",null);
			sqLiteDatabase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}finally{
			sqLiteDatabase.endTransaction();
		}
	}

}
