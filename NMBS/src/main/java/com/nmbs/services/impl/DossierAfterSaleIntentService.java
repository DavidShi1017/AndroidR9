 package com.nmbs.services.impl;


import java.util.ArrayList;

import java.util.List;


import com.nmbs.dataaccess.database.AssistantDatabaseService;
import com.nmbs.dataaccess.restservice.IAssistantDataService;
import com.nmbs.dataaccess.restservice.IStationBoardDataService;
import com.nmbs.dataaccess.restservice.impl.AssistantDataService;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.dataaccess.restservice.impl.StationBoardDataService;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.Order;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;




/**
 * DossierAfterSaleIntentService runs in a new thread the executions of the OfferService
 * 
 * An Intent is received with the in parameter as key PARAM_IN_MSG
 * 
 * An Intent is broadcasted with the result
 */
public class DossierAfterSaleIntentService extends IntentService {
	
	private static final String TAG = DossierAfterSaleIntentService.class.getSimpleName();
	private static Context mContext;
    private static List<Order> orders;
	public DossierAfterSaleIntentService() {
		super(".intentservices.DossierAfterSaleIntentService");
	}
	private Intent broadcastIntent = new Intent();
	private List<Order> newOrders;
	

	private DossierDetailsResponse dossierResponse;
	private boolean hasError;
	private  boolean isCorrupted;
	private Order order;
	private String DNR, email;
	private String languageBeforSetting;
	private IAssistantDataService assistantDataService;
	private IStationBoardDataService stationBoardDataService = new StationBoardDataService();;
	private DossierDetailDataService dossierDetailDataService;
	public static boolean isDossierCallFinished = true;
	private boolean isRefreshAllRealTime;
	@Override
	protected void onHandleIntent(Intent intent) {
		
		order = (Order)intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG);
		if (order != null) {
			DNR = order.getDNR();
		}
		//Log.d(TAG, "dossierAftersalesResponse...DNR..." + order.getDNR());
		boolean hasConnection = intent.getBooleanExtra(ServiceConstant.HAS_CONNECTION, false);
		isRefreshAllRealTime = intent.getBooleanExtra("isRefreshAllRealTime", false);
		dossierDetailDataService = new DossierDetailDataService();
		
		languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
		
		assistantDataService = new AssistantDataService();
		
		//Log.d(TAG, "dossierAftersalesResponse...orders..." + orders);
		
		if(orders != null && orders.size() > 1){			
			handleMultiOrder();			
		}else{
			handleSingleOrder(hasConnection);
		}		
			
	}
	
	private void handleSingleOrder(boolean hasConnection){
		//Log.d(TAG, "migrate handleOrdersIsNull...DNR...");
		try {
			if(hasConnection){
				//Log.d(TAG, "dossierAftersalesResponse...handleOrdersIsNull....DNR..." + DNR);
				dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, email, DNR, languageBeforSetting, true, false, true);

				if (dossierResponse == null) {
					//Log.e(TAG, "migrate...DNR...ERROR");
					catchError(NetworkError.TIMEOUT);
				}else{
					//Log.e(TAG, "migrate...DNR...DONE..." + DNR);
					AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(mContext);
					assistantDatabaseService.deleteOrder(DNR);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			//Log.d(TAG, "Exception...: ");
			catchError(NetworkError.TIMEOUT);
		}
		sendBroadcast();
	}
	private void handleMultiOrder(){
		//Log.d(TAG, "migrate handleOrdersIsNotNull...DNR...");
		newOrders = new ArrayList<Order>();
		for(int i = 0; i < orders.size(); i++){		
			//Log.d(TAG, "migrate orders size is..." + orders.size());
			//Log.d(TAG, "migrate DNR one by one...");
			try {
				String dnr = orders.get(i).getDNR();
				String email = orders.get(i).getEmail();
				dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, email, dnr, languageBeforSetting, true, false, true);
				if (dossierResponse == null) {
					//Log.e(TAG, "migrate...DNR...ERROR");
					hasError = true;
				}else{
					//Log.e(TAG, "migrate...DNR...DONE..." + DNR);
					AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(mContext);
					assistantDatabaseService.deleteOrder(dnr);
				}
								
			} catch (Exception e) {
				//Log.e(TAG, "Exception...");
				hasError = true;
				continue;
			} 					
			
		}
		if (hasError){
			catchError(NetworkError.TIMEOUT);
		}else{
			sendBroadcast();
		}
	}


	private void sendBroadcast(){
			broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_AFTERSALE_RESPONSE);
			broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
			//broadcastIntent.putExtra("stationBoardBulkCallErrorCount", stationBoardBulkCallErrorCount);
			sendBroadcast(broadcastIntent);
	}

	/**
	 * Start this IntentService.
	 * @param context
	 * @param /account
	 */
	public static void startService(Context context, Order order, String languageBeforSetting, boolean hasConnection, 
			List<Order> orderList, boolean isRefreshAllRealTime){
		//Log.d(TAG, "DossierAfterSaleIntentService start......");
		mContext = context;
		orders = orderList;

		Intent msgIntent = new Intent(context, DossierAfterSaleIntentService.class);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG, order);
		msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
		msgIntent.putExtra(ServiceConstant.HAS_CONNECTION, hasConnection);
		msgIntent.putExtra("isRefreshAllRealTime", isRefreshAllRealTime);
		context.startService(msgIntent);
	}
	
	/**
	 * Set error broadcast action,and sent broadcast.
	 * @param //NetworkError
	 * @param /Exception
	 */
	public void catchError(NetworkError error){
		//Log.e(TAG, "catchError error" + error);
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_AFTERSALE_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
	}
}