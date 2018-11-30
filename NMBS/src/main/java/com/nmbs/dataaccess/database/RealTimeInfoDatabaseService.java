package com.nmbs.dataaccess.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nmbs.application.NMBSApplication;
import com.nmbs.model.RealTimeInfoResponse;
import com.nmbs.services.impl.DossierDetailsService;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage Station.
 */
public class RealTimeInfoDatabaseService {
	private static final String TAG = RealTimeInfoDatabaseService.class.getSimpleName();

	// Database fields
	public static final String DB_TABLE_REAL_TIME_INFO = "RealTimeInfo";
	public static final String REAL_TIME_INFO_ID = "_id";
	public static final String REAL_TIME_INFO_SUCCESS = "isSuccess";
	public static final String REAL_TIME_INFO_CONTENT = "realTimeContent";
	public static final String REAL_TIME_INFO_FLAG = "flag";




    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    public RealTimeInfoDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }    

    /**
	 * Insert data to table.
	 *
	 *            order
	 * @return true means everything is OK, otherwise means failure
	 */
	
	public void startTransacstion(){
		sqLiteDatabase.beginTransaction();
	}
	
	public void endTransaction(){
		sqLiteDatabase.setTransactionSuccessful();
		sqLiteDatabase.endTransaction();
	}


	public boolean insertRealTime(RealTimeInfoResponse realTimeInfoResponse) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		sqLiteDatabase.beginTransaction();
		ContentValues contentValues = new ContentValues();
		contentValues.put(REAL_TIME_INFO_ID, realTimeInfoResponse.getId());
		contentValues.put(REAL_TIME_INFO_SUCCESS, String.valueOf(realTimeInfoResponse.getIsSuccess()));
		contentValues.put(REAL_TIME_INFO_CONTENT, realTimeInfoResponse.getContent());
		contentValues.put(REAL_TIME_INFO_FLAG, realTimeInfoResponse.getRealTimeType());

		try {
			sqLiteDatabase.insert(DB_TABLE_REAL_TIME_INFO, realTimeInfoResponse.getId(),
					contentValues);
			sqLiteDatabase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}finally{
			sqLiteDatabase.endTransaction();
		}
	}

	public List<RealTimeInfoResponse> readAllRealTimeInfo() {
		List<RealTimeInfoResponse> realTimeInfoResponses = new ArrayList<RealTimeInfoResponse>();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return realTimeInfoResponses;
		}
		RealTimeInfoResponse realTimeInfoResponse = null;
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_REAL_TIME_INFO, null, null, null, null, null, null);
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String itemId = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_ID));
			String success = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_SUCCESS));
			String content = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_CONTENT));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_FLAG));
			if(DossierDetailsService.Dossier_Realtime_Connection.equals(type)){
				realTimeInfoResponse = new RealTimeInfoResponse(itemId, Boolean.valueOf(success), content, DossierDetailsService.Dossier_Realtime_Connection);
			}else{
				realTimeInfoResponse = new RealTimeInfoResponse(itemId, Boolean.valueOf(success), content, DossierDetailsService.Dossier_Realtime_Segment);
			}
			realTimeInfoResponses.add(realTimeInfoResponse);
		}
		cursor.close();
		return realTimeInfoResponses;
	}

	public RealTimeInfoResponse readRealTimeInfoById(String id) {
		RealTimeInfoResponse realTimeInfoResponse = null;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return realTimeInfoResponse;
		}
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_REAL_TIME_INFO, null, REAL_TIME_INFO_ID+"='"+id+"'", null, null, null, null);
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String itemId = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_ID));
			String success = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_SUCCESS));
			String content = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_CONTENT));
			String type = cursor.getString(cursor.getColumnIndexOrThrow(REAL_TIME_INFO_FLAG));
			if(DossierDetailsService.Dossier_Realtime_Connection.equals(type)){
				realTimeInfoResponse = new RealTimeInfoResponse(itemId, Boolean.valueOf(success), content, DossierDetailsService.Dossier_Realtime_Connection);
			}else{
				realTimeInfoResponse = new RealTimeInfoResponse(itemId, Boolean.valueOf(success), content, DossierDetailsService.Dossier_Realtime_Segment);
			}
		}
		cursor.close();
		return realTimeInfoResponse;
	}

	public boolean deleteRealTime(String id) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		sqLiteDatabase.beginTransaction();
		try {
			sqLiteDatabase.delete(DB_TABLE_REAL_TIME_INFO, REAL_TIME_INFO_ID+"=?", new String[]{id});
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
