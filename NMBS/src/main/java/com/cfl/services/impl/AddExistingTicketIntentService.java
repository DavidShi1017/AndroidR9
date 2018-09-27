 package com.cfl.services.impl;


import java.io.Serializable;


import com.cfl.application.NMBSApplication;
import com.cfl.async.RealTimeAsyncTask;
import com.cfl.dataaccess.restservice.IAssistantDataService;
import com.cfl.dataaccess.restservice.IStationBoardDataService;
import com.cfl.dataaccess.restservice.impl.AssistantDataService;
import com.cfl.dataaccess.restservice.impl.StationBoardDataService;

import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DonotContainTicket;

import com.cfl.exceptions.JourneyPast;
import com.cfl.exceptions.NetworkError;

import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.GeneralSetting;
import com.cfl.model.StationBoardCollection;


import com.cfl.model.Order;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;



/**
 * OfferIntentService runs in a new thread the executions of the OfferService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class AddExistingTicketIntentService extends IntentService {
	
	private static final String TAG = AddExistingTicketIntentService.class.getSimpleName();
    private static Context mContext;
    private Intent broadcastIntent = new Intent();
    
	public AddExistingTicketIntentService() {
		super(".intentservices.AddExistingTicketIntentService");
	}
		
	private DossierAftersalesResponse dossierAftersalesResponse;;

	private Order order;
	private String DNR;
	private String languageBeforSetting;
	private IAssistantDataService assistantDataService;
	private boolean hasError;
	private boolean isCorrupted;
	private IStationBoardDataService stationBoardDataService;
	private int stationBoardBulkCallErrorCount = -1;
	@Override
	protected void onHandleIntent(Intent intent) {
		
		order = (Order)intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		if (order != null) {
			DNR = order.getDNR();
			Log.d(TAG, "dossierAftersalesResponse...DNR..." + DNR);
		}
		languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		assistantDataService = new AssistantDataService();
		stationBoardDataService = new StationBoardDataService();
		handleOrder();
	}
	
	private void handleOrder(){
		Log.d(TAG, "handleOrdersIsNull...DNR...");
		String dossierAftersalesLifetime = "";
		GeneralSetting generalSetting = null;
		try {
			generalSetting = NMBSApplication.getInstance().getAssistantService().getGeneralSetting();
		} catch (Exception e1) {
			dossierAftersalesLifetime = "";
		}
		if(generalSetting != null){
			dossierAftersalesLifetime = generalSetting.getDossierAftersalesLifetime() + "";
		}
		try {
				
			dossierAftersalesResponse = assistantDataService.executeDossierAfterSale(mContext.getApplicationContext(), 
					order, languageBeforSetting, true, true);
			if (dossierAftersalesResponse != null) {
				isCorrupted = assistantDataService.reBuildOrder(mContext, dossierAftersalesResponse, this.order, dossierAftersalesLifetime);
				stationBoardDataService.createStationBoard(mContext, dossierAftersalesResponse, languageBeforSetting);
				createStationBoard(dossierAftersalesResponse.getDnrId());
			}
				
			sendBroadcast(dossierAftersalesResponse);
			
		}catch (ConnectionError e) {
			Log.d(TAG, "ConnectionError...: ");			
			catchError(NetworkError.wrongCombination, null);
			
		}catch (DonotContainTicket e) {	
			Log.d(TAG, "DonotContainTicket...: ");			
			catchError(NetworkError.donotContainTicke, null);
			
		}catch (JourneyPast e) {		
			Log.d(TAG, "JourneyPast...: ");			
			catchError(NetworkError.journeyPast, null);		
			
		}catch (CustomError e) {
			catchError(NetworkError.CustomError, e);
		}catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Exception...: ");		
			catchError(NetworkError.TIMEOUT, null);
		}
		
	}
	
	private void createStationBoard(String dnr){
		
		RealTimeAsyncTask realTimeAsyncTask = new RealTimeAsyncTask(languageBeforSetting, mContext, null);
		realTimeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		/*new Thread() {
			public void run() {
				
				try {
					stationBoardDataService.createStationBoard(mContext, dossierAftersalesResponse, languageBeforSetting);
					StationBoardCollection stationBoardCollection = stationBoardDataService.getStationBoardWithTypeIsA(mContext, dnr);
					if (stationBoardCollection != null && stationBoardCollection.getStationBoards() != null 
							&& stationBoardCollection.getStationBoards().size() > 0) {
					stationBoardBulkCallErrorCount = stationBoardDataService.executeStationBoardBulkQuery(stationBoardCollection, 
							languageBeforSetting, mContext);
					}
				}catch (Exception e) {
					stationBoardBulkCallErrorCount ++;
					e.printStackTrace();
				}
			}
		}.start();*/
	}
	
	private void sendBroadcast(DossierAftersalesResponse dossierAftersalesResponse){
		Log.e(TAG, "dossierAftersalesResponse...." + dossierAftersalesResponse);
		if(dossierAftersalesResponse != null){
			if (isCorrupted) {
				hasError = true;
			}
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_AFTERSALE_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierAftersalesResponse);
			broadcastIntent.putExtra("stationBoardBulkCallErrorCount", stationBoardBulkCallErrorCount);
			broadcastIntent.putExtra("newOrders", (Serializable)null);
			broadcastIntent.putExtra("hasError", hasError);
			sendBroadcast(broadcastIntent);					
		}		
	}


	/**
	 * Start this IntentService.
	 * @param context
	 * @param account
	 */
	public static void startService(Context context, Order order, String languageBeforSetting){
		//Log.d(TAG, "DossierAfterSaleBackgroudIntentService start......");
		mContext = context;

		Intent msgIntent = new Intent(context, AddExistingTicketIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, order);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		

		context.startService(msgIntent);
	}

	/**
	 * Set error broadcast action,and sent broadcast.

	 */
	public void catchError(NetworkError error, Exception e){
		Log.e(TAG, "catchError error" + error);
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_AFTERSALE_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		if(e != null)
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
		sendBroadcast(broadcastIntent);
		if(e != null){
			e.printStackTrace();
		}		
	}
}