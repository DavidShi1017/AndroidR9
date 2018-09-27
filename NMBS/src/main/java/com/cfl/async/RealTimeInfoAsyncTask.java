package com.cfl.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;


import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.services.IDossierService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.util.ActivityConstant;

public class RealTimeInfoAsyncTask extends AsyncTask<Void, Void, Void>{

	private static final String TAG = RealTimeInfoAsyncTask.class.getSimpleName();
	public static final int REALTIMEMESSAGE = 1;
	public static final String RealTime_Broadcast = "RealTime_Broadcast_";
	private Context mContext;
	private Handler handler;
	private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
	private DossierDetailsService dossierService;
	private ISettingService settingService;
	public static boolean isRefreshing;
	public RealTimeInfoAsyncTask( Context mContext, ISettingService settingService,DossierDetailsService dossierService,
								  Handler handler,RealTimeInfoRequestParameter realTimeInfoRequestParameter){
		this.mContext = mContext;
		this.handler = handler;
		this.realTimeInfoRequestParameter = realTimeInfoRequestParameter;
		this.dossierService = dossierService;
		this.settingService = settingService;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		//Log.d("RealTime", "RealTimeInfoAsyncTask...doInBackground.... ");
		isRefreshing = true;
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		//Log.e("RealTime", "shouldRefresh...key..." + realTimeInfoRequestParameter);
		//Log.e("RealTime", "shouldRefresh...key..." + realTimeInfoRequestParameter.getMap());
		//Log.e("RealTime", "shouldRefresh...key..." + realTimeInfoRequestParameter.getMap().size());
		if(realTimeInfoRequestParameter != null && realTimeInfoRequestParameter.getRealTimeInfoRequests() != null
				&& realTimeInfoRequestParameter.getRealTimeInfoRequests().size() > 0){
			//Log.d("RealTime", "RealTimeInfoRequests..." + realTimeInfoRequestParameter.getRealTimeInfoRequests().size());
			this.dossierService.refreshRealTimeInfo(realTimeInfoRequestParameter, settingService, mContext);
		}
		//Log.d("RealTime", "RealTimeInfoAsyncTask...doInBackground..finished...handler send message...");

		Message message = new Message();
		message.what = REALTIMEMESSAGE;
		isRefreshing = false;
		if(handler != null){
			handler.sendMessage(message);
		}
		Intent broadcastIntent = new Intent(RealTime_Broadcast);
		mContext.sendBroadcast(broadcastIntent);

		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
	}	
}
