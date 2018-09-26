package com.nmbs.dataaccess.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossierTravelSegment;
import com.nmbs.model.LocalNotification;
import com.nmbs.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage City.
 */
public class DossiersUpToDateDatabaseService {

	private static final String TAG = DossiersUpToDateDatabaseService.class.getSimpleName();
	// Database fields
	public static final String DB_DossiersUpToDate = "DossiersUpToDate";
	public static final String Column_Id = "_id";
	public static final String Column_Dnr = "dnr";
	public static final String Column_LastUpdated = "LastUpdated";

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    public DossiersUpToDateDatabaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);  
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

	public boolean insertTravelSegment(String dnr, String lastUpdated) {
		Log.d(TAG, "Insert a dnr....." + dnr);
		ContentValues contentValues = new ContentValues();
		sqLiteDatabase.beginTransaction();
		try{
			contentValues.put(Column_Dnr, dnr);
			contentValues.put(Column_LastUpdated, Column_LastUpdated);
			sqLiteDatabase.insert(DB_DossiersUpToDate , Column_Id, contentValues);
			sqLiteDatabase.setTransactionSuccessful();
		}finally{
			sqLiteDatabase.endTransaction();
		}
		return true;
	}

	public String selectLastUpdatedByDnr(String id) throws SQLException {
		Log.d(TAG, "Select LastUpdated. Id is..." + id);
		Cursor cursor = sqLiteDatabase.query(DB_DossiersUpToDate, new String[] {Column_Dnr, Column_LastUpdated},
				Column_Dnr + " = '" + id +  "'", null, null, null, null);
		String lastUpdated = null;
		if(cursor.getCount() > 0){
			cursor.moveToPosition(0);
			lastUpdated = cursor.getString(cursor.getColumnIndexOrThrow(Column_LastUpdated));

		}
		cursor.close();
		Log.d(TAG, "Select LastUpdated is..." + lastUpdated);
		return lastUpdated;
	}

	public boolean deleteLastUpdated(String id){
		sqLiteDatabase.beginTransaction();
		try {
			sqLiteDatabase.delete(DB_DossiersUpToDate, Column_Dnr +"=?", new String[]{id});
			sqLiteDatabase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}finally{
			sqLiteDatabase.endTransaction();
		}
	}

	public boolean updateLastUpdatedByDnr(Context context, String dnr, String lastUpdated){
		sqLiteDatabase.beginTransaction();

		ContentValues contentValues = new ContentValues();
		contentValues.put(Column_LastUpdated, lastUpdated);
		try {
			sqLiteDatabase.update(DB_DossiersUpToDate, contentValues, Column_Dnr + "='" + dnr + "'", null);
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
