package com.cflint.dataaccess.database;

import java.util.ArrayList;
import java.util.List;

import com.cflint.model.DeliveryMethod;
import com.cflint.model.DeliveryOption;
import com.cflint.model.StationsForPickup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;


public class DeliveryMethodDatabaseService {
	//private static final String TAG = DeliveryMethodDatabaseService.class.getSimpleName();
	
	// Database fields   
	public static final String DB_TABLE_DELIVERY_METHODS = "DeliveryMethods";
	public static final String DELIVERY_METHODS_ID = "_id";
	public static final String DELIVERY_METHODS_METHOD = "Method";
	public static final String DELIVERY_METHODS_NAME = "Name";
	public static final String DELIVERY_METHODS_DISPLAYMETHOD = "DisplayMethod";
	
	public static final String DB_TABLE_ALLOWEDSTATIONSFORPICKUP = "AllowedStationsForPickUp";
	public static final String ALLOWEDSTATIONSFORPICKUP_ID = "_id";
	public static final String ALLOWEDSTATIONSFORPICKUP_DELIVERYMETHOD = "DeliveryMethod";
	public static final String ALLOWEDSTATIONSFORPICKUP_STATIONCODE = "StationCode";
	public static final String ALLOWEDSTATIONSFORPICKUP_STATIONNAME = "StationName";
	
	public static final String DB_TABLE_ALLOWEDCOUNTRIESFORMAIL = "AllowedCountriesForMail";
	public static final String ALLOWEDCOUNTRIESFORMAIL_ID = "_id";
	public static final String ALLOWEDCOUNTRIESFORMAIL_DELIVERYMETHOD = "DeliveryMethod";
	public static final String ALLOWEDCOUNTRIESFORMAIL_COUNTRYCODE = "CountryCode";
	
	private SQLiteDatabase sqLiteDatabase;  
    private DatabaseHelper dbHelper;  
    

    public DeliveryMethodDatabaseService(Context context) {   
    	dbHelper = DatabaseHelper.getInstance(context);  
    	sqLiteDatabase = dbHelper.getWritableDatabase();  
    }  
    
    
    /**
     * Insert data to table.
     * @param DeliveryMethod deliveryMethod
   	 * @return true means everything is OK, otherwise means failure
     */
    private boolean insertAllowedStationsForPickupCollection(DeliveryMethod deliveryMethod) {
    	List<StationsForPickup> allowedStationsForPickups = null;
    	if(deliveryMethod != null){
    		
    		allowedStationsForPickups = deliveryMethod.getAllowedStationsForPickup();
    	}  
    	
    	
		if (allowedStationsForPickups != null ) {
			if(allowedStationsForPickups.size() > 0){
				ContentValues contentValues = new ContentValues();	
				sqLiteDatabase.beginTransaction();
				   try {
					   for (StationsForPickup allowedStationsForPickup : allowedStationsForPickups) {
							contentValues.put(ALLOWEDCOUNTRIESFORMAIL_DELIVERYMETHOD, deliveryMethod.getMethod());
							contentValues.put(ALLOWEDSTATIONSFORPICKUP_STATIONCODE, allowedStationsForPickup.getCode());
							contentValues.put(ALLOWEDSTATIONSFORPICKUP_STATIONNAME, allowedStationsForPickup.getName());
							sqLiteDatabase.insert(DB_TABLE_ALLOWEDSTATIONSFORPICKUP, ALLOWEDSTATIONSFORPICKUP_ID, contentValues);	
						}						
						//Log.d(TAG, "Insert data to TABLE= "+DB_TABLE_ALLOWEDSTATIONSFORPICKUP);
						sqLiteDatabase.setTransactionSuccessful();
				   } finally {
					   sqLiteDatabase.endTransaction();
				   }			
				return true;
			}else{
				//Log.d(TAG, "There is no data was inserted.");
				return false;	
			}			
		}else {
			//Log.d(TAG, "There is no data was inserted.");
			return false;			
		}
	}	
    
    /**
     * Insert data to table.
     * @param DeliveryMethod deliveryMethod
   	 * @return true means everything is OK, otherwise means failure
     */
    private boolean insertAllowedCountriesForMailCollection(DeliveryMethod deliveryMethod) {
    	List<String> allowedCountriesForMails = null;
    	if(deliveryMethod != null){
    		allowedCountriesForMails = deliveryMethod.getAllowedCountriesForMail();
    	}
    	
		if (allowedCountriesForMails != null ) {
			if(allowedCountriesForMails.size() > 0){
				ContentValues contentValues = new ContentValues();	
				sqLiteDatabase.beginTransaction();
				   try {
					   for (String allowedCountriesForMail : allowedCountriesForMails) {
							contentValues.put(ALLOWEDCOUNTRIESFORMAIL_DELIVERYMETHOD, deliveryMethod.getMethod());
							contentValues.put(ALLOWEDCOUNTRIESFORMAIL_COUNTRYCODE, allowedCountriesForMail);
							sqLiteDatabase.insert(DB_TABLE_ALLOWEDCOUNTRIESFORMAIL, ALLOWEDCOUNTRIESFORMAIL_ID, contentValues);
						}						
						//Log.d(TAG, "Insert data to TABLE= "+DB_TABLE_ALLOWEDCOUNTRIESFORMAIL);
						sqLiteDatabase.setTransactionSuccessful();
				   } finally {
					   sqLiteDatabase.endTransaction();
				   }			
				return true;
			}else{
				//Log.d(TAG, "There is no data was inserted.");
				return false;		
			}
			
		}else {
			//Log.d(TAG, "There is no data was inserted.");
			return false;			
		}
	}	
    
    /**
     * Insert data to table.
     * @param List<DeliveryMethod> deliveryMethod
   	 * @return true means everything is OK, otherwise means failure
     */
    public boolean insertDeliveryMethodCollection(List<DeliveryMethod> deliveryMethods) {
		if (deliveryMethods != null ) {
			ContentValues contentValues = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			   try {
				   for (DeliveryMethod deliveryMethod : deliveryMethods) {
						contentValues.put(DELIVERY_METHODS_METHOD, deliveryMethod.getMethod());
						contentValues.put(DELIVERY_METHODS_NAME, deliveryMethod.getName());
						contentValues.put(DELIVERY_METHODS_DISPLAYMETHOD, deliveryMethod.getDisplaymethod());
						sqLiteDatabase.insert(DB_TABLE_DELIVERY_METHODS, DELIVERY_METHODS_ID, contentValues);
						insertAllowedStationsForPickupCollection(deliveryMethod);
						insertAllowedCountriesForMailCollection(deliveryMethod);
					}						
					//Log.d(TAG, "Insert data to TABLE= "+DB_TABLE_DELIVERY_METHODS);
					sqLiteDatabase.setTransactionSuccessful();
			   } finally {
				   sqLiteDatabase.endTransaction();
			   }			
			return true;
		}else {
			//Log.d(TAG, "There is no data was inserted.");
			return false;			
		}
	}	
    
    
    /**
     * Select all data from SQLite, add them to ArrayList
     * @return CurrencyResponse
     * @throws SQLException
     */
	public List<DeliveryMethod> selectDeliveryMethodCollection() throws SQLException {	
		
		sqLiteDatabase.beginTransaction();
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_DELIVERY_METHODS
				, new String[] { DELIVERY_METHODS_ID, DELIVERY_METHODS_METHOD, DELIVERY_METHODS_NAME , DELIVERY_METHODS_DISPLAYMETHOD}
				, null, null, null, null, null);	
		int cursorNum = cursor.getCount();
		List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
		DeliveryMethod deliveryMethod = null;
		for (int i = 0; i < cursorNum; i++) {			
			cursor.moveToPosition(i);		
			String method = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_METHOD));
			String name = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_NAME));
			String displaymethod = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_DISPLAYMETHOD));
			
			List<StationsForPickup> allowedStationsForPickups = selectAllowedStationsForPickupByDeliveryMethod(method);
			List<String> allowedCountriesForMails = selectAllowedCountriesForMailByDeliveryMethod(method);
			deliveryMethod = new DeliveryMethod(method, name, displaymethod, allowedStationsForPickups, allowedCountriesForMails);
			deliveryMethods.add(deliveryMethod);
		}
		sqLiteDatabase.endTransaction();
		cursor.close();
		
		return deliveryMethods;		
	}	
	  /**
     * Select all data from SQLite, add them to ArrayList
     * @return CurrencyResponse
     * @throws SQLException
     */
	public List<DeliveryMethod> selectDeliveryMethodCollectionByDeliveryMethod(List<DeliveryOption> deliveryOptions) throws SQLException {	
		
		//sqLiteDatabase.beginTransaction();
		
		List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
		
		DeliveryMethod deliveryMethod = null;
		int deliveryOptionsCount = 0;
		if(deliveryOptions != null ){
			
			deliveryOptionsCount = deliveryOptions.size();
		}
		for(int j = 0; j < deliveryOptionsCount; j++){
			Cursor cursor = sqLiteDatabase.query(DB_TABLE_DELIVERY_METHODS
					, new String[] { DELIVERY_METHODS_ID, DELIVERY_METHODS_METHOD, DELIVERY_METHODS_NAME , DELIVERY_METHODS_DISPLAYMETHOD}
					, DELIVERY_METHODS_METHOD + " = '" + deliveryOptions.get(j).getMethod() + "'" , null, null, null, null);	
			int cursorNum = cursor.getCount();
			//Log.d(TAG, "cursorNum is " + cursorNum);
			for (int i = 0; i < cursorNum; i++) {			
				cursor.moveToPosition(i);		
				String method = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_METHOD));
				String name = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_NAME));
				String displaymethod = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_DISPLAYMETHOD));
				
				List<StationsForPickup> allowedStationsForPickups = selectAllowedStationsForPickupByDeliveryMethod(method);
				List<String> allowedCountriesForMails = selectAllowedCountriesForMailByDeliveryMethod(method);
				deliveryMethod = new DeliveryMethod(method, name, displaymethod, allowedStationsForPickups, allowedCountriesForMails);
				deliveryMethods.add(deliveryMethod);
			}
			//sqLiteDatabase.endTransaction();
			cursor.close();
		}				
		return deliveryMethods;		
	}
	
	  /**
     * Select all data from SQLite, add them to ArrayList
     * @return CurrencyResponse
     * @throws SQLException
     */
	public List<DeliveryMethod> selectDeliveryMethodCollectionByDeliveryMethodName(String deliveryMethodName) throws SQLException {	
		
		//sqLiteDatabase.beginTransaction();
		
		List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
		
		DeliveryMethod deliveryMethod = null;
		
		
		
			Cursor cursor = sqLiteDatabase.query(DB_TABLE_DELIVERY_METHODS
					, new String[] { DELIVERY_METHODS_ID, DELIVERY_METHODS_METHOD, DELIVERY_METHODS_NAME , DELIVERY_METHODS_DISPLAYMETHOD}
					, DELIVERY_METHODS_METHOD + " = '" + deliveryMethodName + "'" , null, null, null, null);	
			int cursorNum = cursor.getCount();
			//Log.d(TAG, "cursorNum is " + cursorNum);
			for (int i = 0; i < cursorNum; i++) {			
				cursor.moveToPosition(i);		
				String method = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_METHOD));
				String name = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_NAME));
				String displaymethod = cursor.getString(cursor.getColumnIndexOrThrow(DELIVERY_METHODS_DISPLAYMETHOD));
				
				List<StationsForPickup> allowedStationsForPickups = selectAllowedStationsForPickupByDeliveryMethod(method);
				List<String> allowedCountriesForMails = selectAllowedCountriesForMailByDeliveryMethod(method);
				deliveryMethod = new DeliveryMethod(method, name, displaymethod, allowedStationsForPickups, allowedCountriesForMails);
				deliveryMethods.add(deliveryMethod);
			}
			//sqLiteDatabase.endTransaction();
			cursor.close();
						
		return deliveryMethods;		
	}
	
	
	
	  /**
     * Select one AllowedStationsForPickup by a DeliveryMethod
     * @return List<String> allowedStationsForPickups
     * @throws SQLException
     */
	
	public List<StationsForPickup> selectAllowedStationsForPickupByDeliveryMethod(String deliveryMethod) throws SQLException {	
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_ALLOWEDSTATIONSFORPICKUP
				, new String[] { ALLOWEDSTATIONSFORPICKUP_ID  , ALLOWEDSTATIONSFORPICKUP_DELIVERYMETHOD  , ALLOWEDSTATIONSFORPICKUP_STATIONCODE, ALLOWEDSTATIONSFORPICKUP_STATIONNAME}
				,ALLOWEDSTATIONSFORPICKUP_DELIVERYMETHOD+"='"+deliveryMethod+"'" , null, null, null, null);	
		int cursorNum = cursor.getCount();
		List<StationsForPickup> allowedStationsForPickups = new ArrayList<StationsForPickup>();
		StationsForPickup stationsForPickup = null;
		for (int i = 0; i < cursorNum; i++) {			
			cursor.moveToPosition(i);		
			String stationCodeString = cursor.getString(cursor.getColumnIndexOrThrow(ALLOWEDSTATIONSFORPICKUP_STATIONCODE));
			String stationNameString = cursor.getString(cursor.getColumnIndexOrThrow(ALLOWEDSTATIONSFORPICKUP_STATIONNAME));
			stationsForPickup = new StationsForPickup(stationCodeString, stationNameString);
			
			allowedStationsForPickups.add(stationsForPickup);
			
		}
		cursor.close();
		return allowedStationsForPickups;		
	}	
	
	  /**
     * Select one AllowedCountriesForMail by a DeliveryMethod
     * @return List<String> allowedCountriesForMails
     * @throws SQLException
     */
	
	public List<String> selectAllowedCountriesForMailByDeliveryMethod(String deliveryMethod) throws SQLException {	
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_ALLOWEDCOUNTRIESFORMAIL
				, new String[] { ALLOWEDCOUNTRIESFORMAIL_ID  , ALLOWEDCOUNTRIESFORMAIL_DELIVERYMETHOD  , ALLOWEDCOUNTRIESFORMAIL_COUNTRYCODE}
				,ALLOWEDCOUNTRIESFORMAIL_DELIVERYMETHOD+"='"+deliveryMethod+"'" , null, null, null, null);	
		int cursorNum = cursor.getCount();
		List<String> allowedCountriesForMails = new ArrayList<String>();
		
		for (int i = 0; i < cursorNum; i++) {			
			cursor.moveToPosition(i);		
			String allowedCountriesForMail = cursor.getString(cursor.getColumnIndexOrThrow(ALLOWEDCOUNTRIESFORMAIL_COUNTRYCODE));
		
			
			allowedCountriesForMails.add(allowedCountriesForMail);
			
		}
		cursor.close();
		return allowedCountriesForMails;		
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
			deleteMasterData(DB_TABLE_ALLOWEDSTATIONSFORPICKUP);
			deleteMasterData(DB_TABLE_ALLOWEDCOUNTRIESFORMAIL);
			return true;
		}else{
			return false;
		}
	}   
}
