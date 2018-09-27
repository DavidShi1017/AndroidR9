package com.cfl.dataaccess.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.cfl.model.City;
import com.cfl.model.Dossier;
import com.cfl.model.DossierSummary;
import com.cfl.model.TrainIcon;
import com.cfl.util.ComparatorDossierDate;
import com.cfl.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage City.
 */
public class DossierDatabaseService {

	//private static final String TAG = DossierDatabaseService.class.getSimpleName();
	// Database fields
	public static final String DB_Dossier = "Dossier";
	public static final String Column_Id = "_id";
	public static final String Column_Dossier_Id = "DossierId";

	public static final String Column_Dossier_Details = "DossierDetails";
	public static final String Column_Dossier_Date = "DossierDate";
	public static final String Column_Dossier_PushEnabled = "DossierPushEnabled";
	public static final String Column_PDF_Successfully = "PDFSuccessfully";
	public static final String Column_Barcode_Successfully = "BarcodeSuccessfully";
	public static final String Column_TravelSegment_Available = "TravelSegmentAvailable";
	public static final String Column_Display_Overlay = "DisplayOverlay";
	public static final String Column_LatestTravelDate = "LatestTravelDate";
	public static final String Column_EarliestTravelDate = "EarliestTravelDate";

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    public DossierDatabaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);  
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

	public boolean insertDossier(DossierSummary dossierSummary) {
		if (dossierSummary != null ) {
			//Log.d(TAG, "Insert a Dossier....." + dossierSummary.getDossierId());
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try{
				contentValues.put(Column_Dossier_Id, dossierSummary.getDossierId());
				contentValues.put(Column_Dossier_Details, dossierSummary.getDossierDetails());
				contentValues.put(Column_Dossier_Date, dossierSummary.getDossierDate());
				contentValues.put(Column_Dossier_PushEnabled, String.valueOf(dossierSummary.isDossierPushEnabled()));
				contentValues.put(Column_PDF_Successfully, String.valueOf(dossierSummary.isPDFSuccessfully()));
				contentValues.put(Column_Barcode_Successfully, String.valueOf(dossierSummary.isBarcodeSuccessfully()));
				contentValues.put(Column_TravelSegment_Available, String.valueOf(dossierSummary.isTravelSegmentAvailable()));
				contentValues.put(Column_Display_Overlay, String.valueOf(dossierSummary.isDisplayOverlay()));
				contentValues.put(Column_LatestTravelDate, DateUtils.dateTimeToString(dossierSummary.getLatestTravelDate()));
				contentValues.put(Column_EarliestTravelDate, DateUtils.dateTimeToString(dossierSummary.getEarliestTravel()));

				sqLiteDatabase.insert(DB_Dossier , Column_Id, contentValues);
				sqLiteDatabase.setTransactionSuccessful();
			}finally{
				sqLiteDatabase.endTransaction();
			}
			return true;
		}else {
			//Log.e(TAG, "DossierSummary is not inserted.");
			return false;
		}
	}

	  /**
     * Select all data from SQLite
     * @return StationResponse
     * @throws SQLException
     */

	public DossierSummary selectDossier(String id) throws SQLException {

		Cursor cursor = sqLiteDatabase.query(DB_Dossier, new String[] {
				Column_Dossier_Id, Column_Dossier_Details, Column_Dossier_Date,
				Column_Dossier_PushEnabled, Column_PDF_Successfully, Column_Barcode_Successfully,
				Column_TravelSegment_Available, Column_Display_Overlay, Column_LatestTravelDate, Column_EarliestTravelDate},
				Column_Dossier_Id + " = '"+id+ "'", null, null, null, null);
		DossierSummary dossierSummary = null;
		Log.d("DossierSummary", "cursor------->" + cursor.getCount());
			if(cursor.getCount() > 0){
				cursor.moveToPosition(0);
				String dossierId = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Id));
				String DossierDetails = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Details));
				String DossierDate = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Date));
				boolean dossierPushEnabled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_PushEnabled)));
				boolean dossierPDFSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_PDF_Successfully)));
				boolean dossierBarcodeSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Barcode_Successfully)));
				boolean travelSegmentAvailable = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_TravelSegment_Available)));
				boolean displayOverlay = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Display_Overlay)));
				String latestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_LatestTravelDate));
				String earliestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_EarliestTravelDate));
				dossierSummary = new DossierSummary(dossierId, DossierDetails, DossierDate, dossierPushEnabled, dossierPDFSuccessfully, dossierBarcodeSuccessfully,
						travelSegmentAvailable, displayOverlay, DateUtils.stringToDateTime(latestTravelDate), DateUtils.stringToDateTime(earliestTravelDate));
			}
		cursor.close();
		return dossierSummary;
	}

	public boolean isPushEnable(String id) throws SQLException {
		//Log.d(TAG, "Select GeneralSetting.");
		Cursor cursor = sqLiteDatabase.query(DB_Dossier, new String[]{
						Column_Dossier_Id, Column_Dossier_Details, Column_Dossier_Date,
						Column_Dossier_PushEnabled, Column_PDF_Successfully, Column_Barcode_Successfully,
						Column_TravelSegment_Available, Column_Display_Overlay, Column_LatestTravelDate, Column_EarliestTravelDate},
				Column_Dossier_Id + " = '" + id + "'", null, null, null, null);
		DossierSummary dossierSummary = null;
		if(cursor.getCount() > 0){
			cursor.moveToPosition(0);
			String dossierId = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Id));
			String DossierDetails = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Details));
			String DossierDate = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Date));
			boolean dossierPushEnabled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_PushEnabled)));
			boolean dossierPDFSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_PDF_Successfully)));
			boolean dossierBarcodeSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Barcode_Successfully)));
			boolean travelSegmentAvailable = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_TravelSegment_Available)));
			boolean displayOverlay = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Display_Overlay)));
			String latestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_LatestTravelDate));
			String earliestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_EarliestTravelDate));
			dossierSummary = new DossierSummary(dossierId, DossierDetails, DossierDate, dossierPushEnabled, dossierPDFSuccessfully, dossierBarcodeSuccessfully,
					travelSegmentAvailable, displayOverlay, DateUtils.stringToDateTime(latestTravelDate), DateUtils.stringToDateTime(earliestTravelDate));
		}
		cursor.close();
		if (dossierSummary != null){
			return dossierSummary.isDossierPushEnabled();
		}
		return false;
	}

	public List<DossierSummary> selectDossierActive(boolean isActive) throws SQLException {
		List<DossierSummary> dossiersSummary = new ArrayList<>();
		//Log.d(TAG, "Select GeneralSetting.");
		Cursor cursor = null;
		Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);
		if(isActive){
			cursor = sqLiteDatabase.query(DB_Dossier, new String[]{
							Column_Dossier_Id, Column_Dossier_Details, Column_Dossier_Date,
							Column_Dossier_PushEnabled, Column_PDF_Successfully, Column_Barcode_Successfully,
							Column_TravelSegment_Available, Column_Display_Overlay, Column_LatestTravelDate, Column_EarliestTravelDate},
					Column_LatestTravelDate + " >= '" + nowTimeStr + "'", null, null, null, Column_EarliestTravelDate + " asc");
		}else{
			cursor = sqLiteDatabase.query(DB_Dossier, new String[]{
							Column_Dossier_Id, Column_Dossier_Details, Column_Dossier_Date,
							Column_Dossier_PushEnabled, Column_PDF_Successfully, Column_Barcode_Successfully,
							Column_TravelSegment_Available, Column_Display_Overlay, Column_LatestTravelDate, Column_EarliestTravelDate},
					Column_LatestTravelDate + " < '" + nowTimeStr + "'", null, null, null, Column_EarliestTravelDate + " desc");
		}
		//Log.e("latestTravelDate", "nowTimeStr..." + nowTimeStr);
		DossierSummary dossierSummary = null;
		int cursorNum = cursor.getCount();

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String dossierId = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Id));
			String DossierDetails = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Details));
			String DossierDate = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Date));
			boolean dossierPushEnabled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_PushEnabled)));
			boolean dossierPDFSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_PDF_Successfully)));
			boolean dossierBarcodeSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Barcode_Successfully)));
			boolean travelSegmentAvailable = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_TravelSegment_Available)));
			boolean displayOverlay = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Display_Overlay)));
			String latestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_LatestTravelDate));
			String earliestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_EarliestTravelDate));
			dossierSummary = new DossierSummary(dossierId, DossierDetails, DossierDate, dossierPushEnabled, dossierPDFSuccessfully,
					dossierBarcodeSuccessfully, travelSegmentAvailable, displayOverlay, DateUtils.stringToDateTime(latestTravelDate), DateUtils.stringToDateTime(earliestTravelDate));
			dossiersSummary.add(dossierSummary);
			///Log.e("latestTravelDate", "latestTravelDate..." + latestTravelDate + "...dossierId..." + dossierId);
		}

		//Log.e("latestTravelDate", "dossiersSummary..." + dossiersSummary.size());
		/*Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.ASC);
		Collections.sort(dossiersSummary, comp);*/
		cursor.close();
		return dossiersSummary;
	}

	public List<DossierSummary> selectDossierAll() throws SQLException {
		List<DossierSummary> dossiersSummary = new ArrayList<>();
		//Log.d(TAG, "Select GeneralSetting.");
		Cursor cursor = sqLiteDatabase.query(DB_Dossier, new String[]{
						Column_Dossier_Id, Column_Dossier_Details, Column_Dossier_Date,
						Column_Dossier_PushEnabled, Column_PDF_Successfully, Column_Barcode_Successfully,
						Column_TravelSegment_Available, Column_Display_Overlay, Column_LatestTravelDate, Column_EarliestTravelDate},
				null, null, null, null, "");
		DossierSummary dossierSummary = null;
		int cursorNum = cursor.getCount();

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String dossierId = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Id));
			String DossierDetails = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Details));
			String DossierDate = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Date));
			boolean dossierPushEnabled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_PushEnabled)));
			boolean dossierPDFSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_PDF_Successfully)));
			boolean dossierBarcodeSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Barcode_Successfully)));
			boolean travelSegmentAvailable = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_TravelSegment_Available)));
			boolean displayOverlay = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Display_Overlay)));
			String latestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_LatestTravelDate));
			String earliestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_EarliestTravelDate));
			dossierSummary = new DossierSummary(dossierId, DossierDetails, DossierDate, dossierPushEnabled, dossierPDFSuccessfully,
					dossierBarcodeSuccessfully, travelSegmentAvailable, displayOverlay, DateUtils.stringToDateTime(latestTravelDate), DateUtils.stringToDateTime(earliestTravelDate));
			dossiersSummary.add(dossierSummary);
		}
		Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.ASC);
		Collections.sort(dossiersSummary, comp);
		cursor.close();
		return dossiersSummary;
	}


	public List<DossierSummary> selectPasetDossier(int dossierAftersalesLifetime) throws SQLException {
		List<DossierSummary> dossiersSummary = new ArrayList<>();
		//Log.d(TAG, "Select GeneralSetting.");
		String pastTime = DateUtils.dateToString(DateUtils.getTheDayBeforeYesterday(dossierAftersalesLifetime));
		Cursor cursor = sqLiteDatabase.query(DB_Dossier, new String[]{
						Column_Dossier_Id, Column_Dossier_Details, Column_Dossier_Date,
						Column_Dossier_PushEnabled, Column_PDF_Successfully, Column_Barcode_Successfully,
						Column_TravelSegment_Available, Column_Display_Overlay, Column_LatestTravelDate, Column_EarliestTravelDate},
				Column_LatestTravelDate + " < '" + pastTime + "'", null, null, null, "");
		DossierSummary dossierSummary = null;
		int cursorNum = cursor.getCount();

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String dossierId = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Id));
			String DossierDetails = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Details));
			String DossierDate = cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_Date));
			boolean dossierPushEnabled = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Dossier_PushEnabled)));
			boolean dossierPDFSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_PDF_Successfully)));
			boolean dossierBarcodeSuccessfully = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Barcode_Successfully)));
			boolean travelSegmentAvailable = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_TravelSegment_Available)));
			boolean displayOverlay = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_Display_Overlay)));
			String latestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_LatestTravelDate));
			String earliestTravelDate =  cursor.getString(cursor.getColumnIndexOrThrow(Column_EarliestTravelDate));
			dossierSummary = new DossierSummary(dossierId, DossierDetails, DossierDate, dossierPushEnabled, dossierPDFSuccessfully,
					dossierBarcodeSuccessfully, travelSegmentAvailable, displayOverlay, DateUtils.stringToDateTime(latestTravelDate), DateUtils.stringToDateTime(earliestTravelDate));
			dossiersSummary.add(dossierSummary);
		}
		Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.ASC);
		Collections.sort(dossiersSummary, comp);
		cursor.close();
		return dossiersSummary;
	}

	public void deleteDossier(String id) {
		//Log.d(TAG, "delete Dossier, id= " + id);
		int isDelete = sqLiteDatabase.delete(DB_Dossier, Column_Dossier_Id + "='" + id + "'", null) ;
		//Log.d(TAG, "isDelete ..." + isDelete);
	}
	

	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteMasterData(String tableName) {
		int isDelete;
		isDelete = sqLiteDatabase.delete(tableName, null, null) ;
		//Log.d(TAG, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}  
}
