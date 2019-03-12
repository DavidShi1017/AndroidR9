package com.nmbs.dataaccess.database;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;


import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;
import com.nmbs.model.GeneralSetting;



/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage City.
 */
public class GeneralSettingDatabaseService {
	
	//private static final String TAG = GeneralSettingDatabaseService.class.getSimpleName();
	// Database fields   
	public static final String DB_GENERAL_SETTINGS = "GeneralSettings";
	public static final String GENERAL_SETTING_ID = "_id";
	/*public static final String GENERAL_SETTING_BOOKING_COM_ID = "BookingComAid";
	public static final String GENERAL_SETTING_DEFAULT_ORIGIN_STATION = "DefaultOriginStation";
	public static final String GENERAL_SETTING_DOSSIER_AFTERSALES_LIFE_TIME = "DossierAftersalesLifetime";*/

	public static final String Column_RestSalt = "RestSalt";
	public static final String Column_AutoLogonSalt = "AutoLogonSalt";
	public static final String Column_FacebookAppId = "FacebookAppId";
	public static final String Column_GoogleAppId = "GoogleAppId";
	public static final String Column_CreateProfileUrl = "CreateProfileUrl";
	public static final String Column_ProfileOverviewUrl = "ProfileOverviewUrl";
	public static final String Column_CommercialTtlListUrl = "CommercialTtlListUrl";
	public static final String Column_PrivacyPolicyUrl = "PrivacyPolicyUrl";
	public static final String Column_Domain = "Domain";
	public static final String Column_CheckLastUpdateTimestampPassword = "CheckLastUpdateTimestampPassword";

	public static final String Column_MaxRealTimeInfoHorizon = "MaxRealTimeInfoHorizon";
	public static final String Column_DossierAftersalesLifetime = "DossierAftersalesLifetime";
	public static final String Column_BookingUrl = "BookingUrl";
	public static final String Column_LffUrl = "LffUrl";
	public static final String Column_BelgiumPhoneNumber = "BelgiumPhoneNumber";
	public static final String Column_InternationalPhoneNumber = "InternationalPhoneNumber";
	public static final String Column_AllowContextRegistration = "AllowContextRegistration";
	public static final String Column_InsuranceConditionsPdf = "InsuranceConditionsPdf";


	
    private SQLiteDatabase sqLiteDatabase;  
    private DatabaseHelper dbHelper;  
  
    public GeneralSettingDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }    
    
	   /**
     * Insert data to table.
     * @param generalSetting generalSetting
   	 * @return true means everything is OK, otherwise means failure
     */
    public boolean insertStationCollection(GeneralSetting generalSetting) {

		if (generalSetting != null ) {
			ContentValues contentValues = new ContentValues();
			if(sqLiteDatabase == null){
				sqLiteDatabase = dbHelper.getWritableDatabase();
			}
			if(sqLiteDatabase == null){
				return false;
			}
			sqLiteDatabase.beginTransaction();
			try{
				LogUtils.e("insert GeneralSetting", "insert bookTicktes URL is------>" + generalSetting.getBookingUrl());
				contentValues.put(Column_RestSalt, generalSetting.getRestSalt());
				contentValues.put(Column_AutoLogonSalt, generalSetting.getAutoLogonSalt());
				contentValues.put(Column_FacebookAppId, generalSetting.getFacebookAppId());
				contentValues.put(Column_GoogleAppId, generalSetting.getGoogleAppId());
				contentValues.put(Column_CreateProfileUrl, generalSetting.getCreateProfileUrl());
				contentValues.put(Column_ProfileOverviewUrl, generalSetting.getProfileOverviewUrl());
				contentValues.put(Column_CommercialTtlListUrl, generalSetting.getCommercialTtlListUrl());
				contentValues.put(Column_PrivacyPolicyUrl, generalSetting.getPrivacyPolicyUrl());
				contentValues.put(Column_Domain, generalSetting.getDomain());
				contentValues.put(Column_CheckLastUpdateTimestampPassword, String.valueOf(generalSetting.getDomain()));
									
				contentValues.put(Column_MaxRealTimeInfoHorizon, Integer.valueOf(generalSetting.getMaxRealTimeInfoHorizon()));
				contentValues.put(Column_DossierAftersalesLifetime, Integer.valueOf(generalSetting.getDossierAftersalesLifetime()));
				contentValues.put(Column_BookingUrl, generalSetting.getBookingUrl());
				contentValues.put(Column_LffUrl, generalSetting.getLffUrl());
				contentValues.put(Column_BelgiumPhoneNumber, generalSetting.getBelgiumPhoneNumber());
				contentValues.put(Column_InternationalPhoneNumber, generalSetting.getInternationalPhoneNumber());
				contentValues.put(Column_AllowContextRegistration, String.valueOf(generalSetting.isAllowContextRegistration()));
				contentValues.put(Column_InsuranceConditionsPdf, generalSetting.getInsuranceConditionsPdf());

				sqLiteDatabase.insert(DB_GENERAL_SETTINGS , GENERAL_SETTING_ID, contentValues);
						
				//Log.d(TAG, "Insert data to TABLE= "+DB_GENERAL_SETTING);		
				sqLiteDatabase.setTransactionSuccessful();				
			}finally{
				sqLiteDatabase.endTransaction();
			}
			return true;
		}else {
			//Log.d(TAG, "There is no data was inserted.");
			return false;
		}
	}	
   
		
	  /**
     * Select all data from SQLite
     * @return StationResponse
     * @throws SQLException
     */

	public GeneralSetting selectGeneralSetting() throws SQLException {
		GeneralSetting generalSetting = null;
		//Log.d(TAG, "Select GeneralSetting.");
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return generalSetting;
		}
		Cursor cursor = sqLiteDatabase.query(DB_GENERAL_SETTINGS, null, null, null, null,
				null, null);


		int cursorNum = cursor.getCount();
		
		for (int i = 0; i < cursorNum; i++) {	
			cursor.moveToPosition(i);

			String restSalt = cursor.getString(cursor.getColumnIndexOrThrow(Column_RestSalt));
			String autoLogonSalt = cursor.getString(cursor.getColumnIndexOrThrow(Column_AutoLogonSalt));
			String facebookAppId = cursor.getString(cursor.getColumnIndexOrThrow(Column_FacebookAppId));
			String googleAppId = cursor.getString(cursor.getColumnIndexOrThrow(Column_GoogleAppId));
			String createProfileUrl = cursor.getString(cursor.getColumnIndexOrThrow(Column_CreateProfileUrl));
			String profileOverviewUrl = cursor.getString(cursor.getColumnIndexOrThrow(Column_ProfileOverviewUrl));
			String commercialTtlListUrl = cursor.getString(cursor.getColumnIndexOrThrow(Column_CommercialTtlListUrl));
			String privacyPolicyUrl = cursor.getString(cursor.getColumnIndexOrThrow(Column_PrivacyPolicyUrl));
			String domain = cursor.getString(cursor.getColumnIndexOrThrow(Column_Domain));
			boolean checkLastUpdateTimestampPassword = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_CheckLastUpdateTimestampPassword)));

			int maxRealTimeInfoHorizon = cursor.getInt(cursor.getColumnIndexOrThrow(Column_MaxRealTimeInfoHorizon));
			int dossierAftersalesLifetime = cursor.getInt(cursor.getColumnIndexOrThrow(Column_DossierAftersalesLifetime));
			String bookingUrl = cursor.getString(cursor.getColumnIndexOrThrow(Column_BookingUrl));
			String lffUrl = cursor.getString(cursor.getColumnIndexOrThrow(Column_LffUrl));
			String belgiumPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Column_BelgiumPhoneNumber));
			String internationalPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Column_InternationalPhoneNumber));
			boolean allowContextRegistration = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(Column_AllowContextRegistration)));
			String insuranceConditionsPdf = cursor.getString(cursor.getColumnIndexOrThrow(Column_InsuranceConditionsPdf));

			generalSetting = new GeneralSetting(maxRealTimeInfoHorizon, dossierAftersalesLifetime, bookingUrl, lffUrl,
					belgiumPhoneNumber, internationalPhoneNumber, allowContextRegistration, insuranceConditionsPdf,
					restSalt, autoLogonSalt, facebookAppId, googleAppId, createProfileUrl, profileOverviewUrl, commercialTtlListUrl,
					privacyPolicyUrl, domain, checkLastUpdateTimestampPassword);
		}


		cursor.close();

		return generalSetting;
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
		//Log.d(TAG, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}  
}
