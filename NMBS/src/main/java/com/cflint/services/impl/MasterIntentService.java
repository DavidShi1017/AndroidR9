package com.cflint.services.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import com.cflint.activity.SettingsActivity;

import com.cflint.dataaccess.database.CollectionItemDatabaseService;
import com.cflint.dataaccess.database.CurrencyDatabaseService;

import com.cflint.dataaccess.database.GeneralSettingDatabaseService;

import com.cflint.dataaccess.restservice.IMasterDataService;

import com.cflint.dataaccess.restservice.impl.MasterDataService;

import com.cflint.exceptions.ConnectionError;

import com.cflint.exceptions.NetworkError;

import com.cflint.model.CollectionItem;

import com.cflint.model.MasterDataResponse;

import com.cflint.model.GeneralSetting;


import com.cflint.model.Station;


import com.cflint.model.Currency;
import com.cflint.util.FileManager;
import com.cflint.util.SharedPreferencesUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;




/**
 * MasterIntentService runs in a new thread the executions of the MasterService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class MasterIntentService extends IntentService{
	
	  public MasterIntentService() {
		super(".intentservices.MasterIntentService");		
	}
	private Intent broadcastIntent = new Intent();
	//private static final String TAG = MasterIntentService.class.getSimpleName();
	private IMasterDataService masterDataService = new MasterDataService();
	
	private static Context mContext;
	
	public static boolean isClickToCallFinished;
	public static boolean isMasterDataFinished;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		masterDataService.encryptDatabase(mContext);
		
		String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);

		
		downloadMasterDataForLanguage(languageBeforSetting);

		SharedPreferencesUtils.storeSharedPreferences(SettingsActivity.PREFS_IS_LANGUAGE_FIRST_UPDATE, "flase", mContext);
		//sendBroadcast();
		
	}
	
	
	private void downloadHomeBanner(final String languageBeforSetting){
		executorService.submit(new Runnable() {
			
			public void run() {
				try {
					masterDataService.executeHomeBanner(mContext, languageBeforSetting);
					
				}catch (ConnectionError e) {
					
					//Log.d("HomeBanner", "HomeBanner 404 the data will be deleted....");
					FileManager.getInstance().deleteExternalStoragePrivateFile(mContext, null, "HomeBanner.json");
				} catch (Exception e) {
					
					e.printStackTrace();
				}finally{
					
				}
				
			}
		});
	}
	
	private void downloadTrainIcons(final String languageBeforSetting){
		executorService.submit(new Runnable() {
			
			public void run() {
				try {
					masterDataService.executeTrainIcons(mContext, languageBeforSetting);
					
				}catch (ConnectionError e) {
					
					Log.e("TrainIcons", "TrainIcons error...");
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}finally{
					
				}
				
			}
		});
	}

	private void downloadMasterDataForLanguage(String languageBeforSetting){
		
		//Log.i(TAG, "MasterIntentService is working.....downloadMasterDataForLanguage");
		//getLanguageCollection(languageBeforSetting);
		//getMasterData(languageBeforSetting);
		getClickToCallScenarios(languageBeforSetting);	
		downloadHomeBanner(languageBeforSetting);		
		downloadTrainIcons(languageBeforSetting);
		//getTitleCollection(languageBeforSetting);
				
		//getCurrencies(languageBeforSetting);
		
		getGeneralSetting(languageBeforSetting);

	}
	
	private void getMasterData(final String languageBeforSetting){
		
		executorService.submit(new Runnable() {
			
			public void run() {
				try {
					isMasterDataFinished = false;
					//Thread.sleep(15000);
					MasterDataResponse masterDataResponse = masterDataService.executeMasterData(mContext, languageBeforSetting);
					if (masterDataResponse != null) {

						
						if (masterDataResponse.getOriginDestinationRules() != null) {
							List<Station> stations = masterDataService.executeStationCollection(mContext, languageBeforSetting, masterDataResponse.getOriginDestinationRules());
							masterDataService.insertStations(mContext, stations);
							masterDataService.insertStationMatrix(mContext, masterDataResponse.getOriginDestinationRules());
						}
						broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_MASTER_RESPONSE);
						broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
						sendBroadcast(broadcastIntent);
					}
				} catch (Exception e) {
					catchError(NetworkError.TIMEOUT,e);
					e.printStackTrace();
				}  finally{				
					isMasterDataFinished = true;
					if (mContext != null) {
						Intent broadcastIntent = new Intent(ServiceConstant.MASTERDATA_SERVICE_ACTION);
						mContext.sendBroadcast(broadcastIntent);
					}
				}	
			}
		});
		
	}



	
	private void getGeneralSetting(String languageBeforSetting) {
		try {
			//Log.i(TAG,"MasterIntentService is working.....GeneralSetting");
			
			masterDataService.executeGeneralSetting(mContext, languageBeforSetting);


		} catch (Exception e) {
			//To do nothing!!!!!!!!
			//catchError(NetworkError.TIMEOUT, e);
		} 
	}
	

	private void getClickToCallScenarios(final String languageBeforSetting) {
		
		executorService.submit(new Runnable() {
			
			public void run() {
				try {
					//Log.i(TAG,"MasterIntentService is working.....ClickToCallScenarios");				
					//Thread.sleep(10000);
					isClickToCallFinished = false;
					masterDataService.executeClickToCallScenario(mContext, languageBeforSetting);
				} catch (Exception e) {
					//To do nothing!!!!!!!!
					//catchError(NetworkError.TIMEOUT, e);
				} finally{				
					isClickToCallFinished = true;
					if (mContext != null) {
						Intent broadcastIntent = new Intent(ServiceConstant.CLICK_TO_CALL_SERVICE_ACTION);
						mContext.sendBroadcast(broadcastIntent);
					}
				}				
			}
		});
	}

	
	/**
	 * Call insertCurrencies method in DatabaseService. 
	 * @param currencies
	 */
	private void insertCurrencies(List<Currency> currencies){
		//Log.d(TAG, "Converter currency succeed....");
		CurrencyDatabaseService currencyDatabaseService = new CurrencyDatabaseService(mContext);
		List<Currency> currenciesInDataBase = currencyDatabaseService.selectCurrencyCollection();
		if(currenciesInDataBase != null){
			if(currenciesInDataBase.size() != 0){
				//Log.d(TAG, "Start delete data for CURRENCY table....");
				boolean isDeleted = currencyDatabaseService.deleteMasterData(CurrencyDatabaseService.DB_TABLE_CURRENCY);
				if(isDeleted){
					//Log.d(TAG, "Delete currency succeed....");
					//Log.d(TAG, "Start insert data to CURRENCY table....");
					currencyDatabaseService.insertCurrencyCollection(currencies);
				}
			}else{
				currencyDatabaseService.insertCurrencyCollection(currencies);
			}	
		}

	}
	


	/**
	 * Call insertCities method in DatabaseService.
	 * 
	 * @param GeneralSetting
	 */


	


	
	/**
	 * Start this IntentService.
	 * @param context
	 * @param account
	 */
	public static void startService(Context context, String languageBeforSetting, boolean isMastWorking){

		mContext = context;
		isClickToCallFinished = false;
		Intent msgIntent = new Intent(context, MasterIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		msgIntent.putExtra(ServiceConstant.IS_MASTDATA_WORKING, isMastWorking);
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param NetworkError
	 * @param Exception
	 */
	public void catchError(NetworkError error,Exception e){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_MASTER_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
		e.printStackTrace();
	}
}
