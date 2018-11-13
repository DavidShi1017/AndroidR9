package com.cflint.async;

import java.util.List;

import com.cflint.model.Order;
import com.cflint.services.IAssistantService;

import com.cflint.services.ISettingService;
import com.cflint.services.impl.AsyncDossierAfterSaleResponse;
import com.cflint.services.impl.ServiceConstant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class ExecuteDossierAftersalesTask extends AsyncTask<Void, Void, Void>{    	
	
	public static boolean isDossierCallFinished = true;
	private IAssistantService assistantService;
	private ISettingService settingService;
	private Context mContext;
	private List<Order> orderList;
	public AsyncDossierAfterSaleResponse asyncDossierAfterSaleResponse;
	public static final String SERVICE_STATE_KEY = "ExecuteDossierFinished"; 
	public ExecuteDossierAftersalesTask(IAssistantService assistantService, ISettingService settingService, Context mContext, List<Order> orderList){
		this.assistantService = assistantService;
		this.settingService = settingService;
		this.mContext = mContext;
		this.orderList = orderList;
	}
	
	@Override
	protected void onPreExecute() {
	    
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		
		//ExecuteDossierAftersalesTask.isDossierCallFinished = false;
		AsyncDossierAfterSaleResponse aresponse = new AsyncDossierAfterSaleResponse();
		aresponse.registerHandler(mHandler);
		aresponse.registerReceiver(mContext);
		assistantService.refreshDossierAftersales(orderList, settingService.getCurrentLanguagesKey());
		
		/*try {
			assistantService.refreshRealTimeFirstTime(settingService.getCurrentLanguage());
		} catch (Exception e) {
			// TODO Noting...
			e.printStackTrace();
		}*/
		return null;
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			isDossierCallFinished = true;
			if (mContext != null) {
				Intent broadcastIntent = new Intent(ServiceConstant.DOSSIER_SERVICE_ACTION);
				broadcastIntent.putExtra(SERVICE_STATE_KEY, isDossierCallFinished);
				mContext.sendBroadcast(broadcastIntent);
			}
			
		};
	};
	@Override
	protected void onPostExecute(Void result) {
		//System.out.println("finished.....");
		//executeRefreshRealTimeFirstTime();
		super.onPostExecute(result);
	}

	
}
