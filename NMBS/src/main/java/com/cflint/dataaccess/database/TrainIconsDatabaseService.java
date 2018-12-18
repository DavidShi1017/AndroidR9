package com.cflint.dataaccess.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.cflint.application.NMBSApplication;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Station;
import com.cflint.model.Train;
import com.cflint.model.TrainIcon;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage City.
 */
public class TrainIconsDatabaseService {

	//private static final String TAG = GeneralSettingDatabaseService.class.getSimpleName();
	// Database fields
	public static final String DB_TRAIN_ICONS = "TrainIcons";
	public static final String Column_TrainIconsId = "_id";

	public static final String Column_BrandName = "BrandName";
	public static final String Column_LinkedHafasCodes = "LinkedHafasCodes";
	public static final String Column_LinkedTariffGroups = "LinkedTariffGroups";
	public static final String Column_LinkedTrainBrands = "LinkedTrainBrands";
	public static final String Column_HighResImage = "HighResImage";
	public static final String Column_LowResImage = "LowResImage";

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;

	public TrainIconsDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}

	public boolean insertTrainIcons(List<TrainIcon> trainIcons) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		if (trainIcons != null ) {
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try{
				for (TrainIcon trainIcon : trainIcons) {
					contentValues.put(Column_BrandName, trainIcon.getBrandName());
					contentValues.put(Column_LinkedHafasCodes, trainIcon.getHafasCodes());
					contentValues.put(Column_LinkedTariffGroups, trainIcon.getTariffGroups());
					contentValues.put(Column_LinkedTrainBrands, trainIcon.getTrainBrands());
					contentValues.put(Column_HighResImage, trainIcon.getImageHighResolution());
					contentValues.put(Column_LowResImage, trainIcon.getImageLowResolution());
					sqLiteDatabase.insert(DB_TRAIN_ICONS , Column_TrainIconsId, contentValues);
				}
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
	 * Select all data from SQLite
	 * @return StationResponse
	 * @throws SQLException
	 */

	public List<TrainIcon> selectTrainIcons() throws SQLException {

		List<TrainIcon> trainIcons = new ArrayList<>();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return trainIcons;
		}
		//Log.d(TAG, "Select GeneralSetting.");
		Cursor cursor = sqLiteDatabase.query(DB_TRAIN_ICONS, new String[] {
				Column_BrandName,
				Column_LinkedHafasCodes, Column_LinkedTariffGroups,
				Column_LinkedTrainBrands, Column_HighResImage, Column_LowResImage,
		}, null, null, null, null, null);

		TrainIcon trainIcon = null;
		int cursorNum = cursor.getCount();
		if(cursorNum > 0){
			trainIcons = new ArrayList<>();
		}
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String brandName = cursor.getString(cursor.getColumnIndexOrThrow(Column_BrandName));
			String linkedHafasCodes = cursor.getString(cursor.getColumnIndexOrThrow(Column_LinkedHafasCodes));

			String linkedTariffGroups = cursor.getString(cursor.getColumnIndexOrThrow(Column_LinkedTariffGroups));
			String linkedTrainBrands = cursor.getString(cursor.getColumnIndexOrThrow(Column_LinkedTrainBrands));
			String highResImage = cursor.getString(cursor.getColumnIndexOrThrow(Column_HighResImage));
			String lowResImage = cursor.getString(cursor.getColumnIndexOrThrow(Column_LowResImage));
			/*generalSetting = new GeneralSetting(maxRealTimeInfoHorizon, dossierAftersalesLifetime, bookingUrl, lffUrl,
					belgiumPhoneNumber, internationalPhoneNumber, allowContextRegistration, insuranceConditionsPdf);*/
			trainIcon = new TrainIcon(brandName, linkedHafasCodes, linkedTariffGroups, linkedTrainBrands, highResImage, lowResImage);
			trainIcons.add(trainIcon);
		}


		cursor.close();

		return trainIcons;
	}


	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteData(String tableName) {
		int isDelete;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		isDelete = sqLiteDatabase.delete(tableName, null, null) ;
		//Log.d(TAG, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}
}
