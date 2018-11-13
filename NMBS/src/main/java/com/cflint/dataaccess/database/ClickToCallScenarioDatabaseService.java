package com.cflint.dataaccess.database;

import java.util.ArrayList;
import java.util.List;

import com.cflint.model.ClickToCallScenario;

import com.cflint.model.ProviderSetting;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;


public class ClickToCallScenarioDatabaseService {

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;
	//private static final String TAG = ClickToCallScenarioDatabaseService.class.getSimpleName();
	
	// Database fields
	public static final String DB_TABLE_CLICK_TO_CALL_SCENARIO = "ClickToCallScenario";
	public static final String _ID = "_Id";
	public static final String CLICK_TO_CALL_SCENARIO_ID = "Id";
	public static final String CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER = "DefaultPhoneNumber";
	
	
	public static final String DB_TABLE_PROVIDER_SETTING = "ProviderSetting";
	public static final String PROVIDER_SETTING_ID = "_Id";
	public static final String PROVIDER_SETTING_SCENARIO_ID = "ScenarioId";
	public static final String PROVIDER_SETTING_PROVIDER = "Provider";	
	public static final String PROVIDER_SETTING_PHONENUMBER = "PhoneNumber";
	
	public ClickToCallScenarioDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Insert data to table.
	 * 
	 * @param List<ClickToCallScenario> clickToCallScenarios
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertClickToCallScenarios(List<ClickToCallScenario> clickToCallScenarios) {
		
		if (clickToCallScenarios != null ) {
			
			ContentValues contentValuesClickToCallScenario = new ContentValues();	
			ContentValues contentValuesProviderSetting = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			try{
				for (ClickToCallScenario clickToCallScenario: clickToCallScenarios) {
					contentValuesClickToCallScenario.put(CLICK_TO_CALL_SCENARIO_ID, clickToCallScenario.getId());
					contentValuesClickToCallScenario.put(CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER, clickToCallScenario.getDefaultPhoneNumber());								
					sqLiteDatabase.insert(DB_TABLE_CLICK_TO_CALL_SCENARIO , _ID, contentValuesClickToCallScenario);	
					
					for(ProviderSetting providerSetting : clickToCallScenario.getProviderSettings()){
						contentValuesProviderSetting.put(PROVIDER_SETTING_SCENARIO_ID, clickToCallScenario.getId());
						contentValuesProviderSetting.put(PROVIDER_SETTING_PROVIDER, providerSetting.getProvider());		
						contentValuesProviderSetting.put(PROVIDER_SETTING_PHONENUMBER, providerSetting.getPhoneNumber());	
						sqLiteDatabase.insert(DB_TABLE_PROVIDER_SETTING , PROVIDER_SETTING_ID, contentValuesProviderSetting);	
						//Log.d(TAG, "Insert data to TABLE= "+DB_TABLE_PROVIDER_SETTING);
					}																					
					//Log.d(TAG, "Insert data to TABLE= "+DB_TABLE_CLICK_TO_CALL_SCENARIO);
				}						
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
     * Select all data from SQLite, add them to ArrayList
     * @return StationResponse
     * @throws SQLException
     */
	public ClickToCallScenario loadClickToCallScenarioById(int id) throws SQLException {	
		ClickToCallScenario clickToCallScenario = null;
		Cursor cursorClickToCallScenario = sqLiteDatabase.query(DB_TABLE_CLICK_TO_CALL_SCENARIO
				, new String[] { CLICK_TO_CALL_SCENARIO_ID, CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER}
				, CLICK_TO_CALL_SCENARIO_ID + " = '" + id + "'", null, null, null, null);
		ProviderSetting providerSetting = null;
		List<ProviderSetting> providerSettings = new ArrayList<ProviderSetting>();
		int cursorNum = cursorClickToCallScenario.getCount();
		int cursorProviderSettingNum = 0;
		//Log.d(TAG, "cursorClickToCallScenario  is." + cursorNum);		
		for (int i = 0; i < cursorNum; i++) {	
			//Log.d(TAG, "Select ClickToCallScenario by id.");
			cursorClickToCallScenario.moveToPosition(i);		
			String scenarioId = cursorClickToCallScenario.getString(cursorClickToCallScenario.getColumnIndexOrThrow(CLICK_TO_CALL_SCENARIO_ID));
			String defaultPhoneNumber = cursorClickToCallScenario.getString(cursorClickToCallScenario.getColumnIndexOrThrow(CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER));
			
			Cursor cursorProviderSetting = sqLiteDatabase.query(DB_TABLE_PROVIDER_SETTING
					, new String[] { PROVIDER_SETTING_SCENARIO_ID, PROVIDER_SETTING_PROVIDER, PROVIDER_SETTING_PHONENUMBER}
					, PROVIDER_SETTING_SCENARIO_ID + " = '" + id + "'", null, null, null, null);
			cursorProviderSettingNum = cursorProviderSetting.getCount();
			
			for (int j = 0; j < cursorProviderSettingNum; j++) {
				cursorProviderSetting.moveToPosition(j);
				String provider = cursorProviderSetting.getString(cursorProviderSetting.getColumnIndexOrThrow(PROVIDER_SETTING_PROVIDER));
				String phoneNumber = cursorProviderSetting.getString(cursorProviderSetting.getColumnIndexOrThrow(PROVIDER_SETTING_PHONENUMBER));
				providerSetting = new ProviderSetting(provider, phoneNumber);
				providerSettings.add(providerSetting);
			}
			clickToCallScenario = new ClickToCallScenario(scenarioId, defaultPhoneNumber, providerSettings);			
			cursorProviderSetting.close();
		}	
		cursorClickToCallScenario.close();
		return clickToCallScenario;		
	}
	
	  /**
     * Select all data from SQLite, add them to ArrayList
     * @return StationResponse
     * @throws SQLException
     */
	public List<ClickToCallScenario> loadClickToCallScenarios() throws SQLException {	
		ClickToCallScenario clickToCallScenario = null;
		Cursor cursorClickToCallScenario = sqLiteDatabase.query(DB_TABLE_CLICK_TO_CALL_SCENARIO
				, new String[] { CLICK_TO_CALL_SCENARIO_ID, CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER}
				, null, null, null, null, null);
		ProviderSetting providerSetting = null;
		List<ProviderSetting> providerSettings = new ArrayList<ProviderSetting>();
		List<ClickToCallScenario> clickToCallScenarios = new ArrayList<ClickToCallScenario>();
		int cursorNum = cursorClickToCallScenario.getCount();
		int cursorProviderSettingNum = 0;
		//Log.d(TAG, "cursorClickToCallScenario  is." + cursorNum);		
		for (int i = 0; i < cursorNum; i++) {	
			//Log.d(TAG, "Select ClickToCallScenario by id.");
			cursorClickToCallScenario.moveToPosition(i);		
			String scenarioId = cursorClickToCallScenario.getString(cursorClickToCallScenario.getColumnIndexOrThrow(CLICK_TO_CALL_SCENARIO_ID));
			String defaultPhoneNumber = cursorClickToCallScenario.getString(cursorClickToCallScenario.getColumnIndexOrThrow(CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER));
			
			Cursor cursorProviderSetting = sqLiteDatabase.query(DB_TABLE_PROVIDER_SETTING
					, new String[] { PROVIDER_SETTING_SCENARIO_ID, PROVIDER_SETTING_PROVIDER, PROVIDER_SETTING_PHONENUMBER}
					, PROVIDER_SETTING_SCENARIO_ID + " = '" + scenarioId + "'", null, null, null, null);
			cursorProviderSettingNum = cursorProviderSetting.getCount();
			
			for (int j = 0; j < cursorProviderSettingNum; j++) {
				cursorProviderSetting.moveToPosition(j);
				String provider = cursorProviderSetting.getString(cursorProviderSetting.getColumnIndexOrThrow(PROVIDER_SETTING_PROVIDER));
				String phoneNumber = cursorProviderSetting.getString(cursorProviderSetting.getColumnIndexOrThrow(PROVIDER_SETTING_PHONENUMBER));
				providerSetting = new ProviderSetting(provider, phoneNumber);
				providerSettings.add(providerSetting);
			}
			clickToCallScenario = new ClickToCallScenario(scenarioId, defaultPhoneNumber, providerSettings);	
			clickToCallScenarios.add(clickToCallScenario);
			cursorProviderSetting.close();
		}	
		cursorClickToCallScenario.close();
		return clickToCallScenarios;		
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
