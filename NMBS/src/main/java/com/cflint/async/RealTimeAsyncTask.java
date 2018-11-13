package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cflint.dataaccess.restservice.IStationBoardDataService;
import com.cflint.dataaccess.restservice.impl.StationBoardDataService;
import com.cflint.exceptions.CustomError;

import com.cflint.model.StationBoardCollection;

import com.cflint.services.impl.ServiceConstant;

public class RealTimeAsyncTask extends AsyncTask<Void, Void, Void>{    	
	
	private String languageBeforSetting;
	public static boolean isMessageFinished;
	private Context mContext;
	private String dnr;
	public static boolean isRealTimeFinished = true;
	private boolean isHasError;
	public static final String ERROR_COUNT = "ErrorCount";
	public static final String SERVICE_STATE_KEY = "RealTimeFinished";
	public static String REFRESHING_DNR;
	public static boolean isRefreshingOneDnr;
	public RealTimeAsyncTask(String languageBeforSetting, Context mContext, String dnr){
		
		this.languageBeforSetting = languageBeforSetting;
		this.mContext = mContext;
		this.dnr = dnr;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {

		IStationBoardDataService stationBoardDataService = new StationBoardDataService();
		
		try {
			
			StationBoardCollection stationBoardCollection = null;
			if (dnr != null) {
				stationBoardCollection = stationBoardDataService.getStationBoardWithTypeIsA(mContext, dnr);
				isRefreshingOneDnr = true;
				REFRESHING_DNR = dnr;
			}else {
				stationBoardCollection = stationBoardDataService.getStationBoardWithTypeIsA(mContext, null);
			}
			if (stationBoardCollection != null && stationBoardCollection.getStationBoards() != null 
					&& stationBoardCollection.getStationBoards().size() > 0) {
				isRealTimeFinished = false;
				if (mContext != null) {
					Intent broadcastIntent = new Intent(ServiceConstant.REALTIME_SERVICE_ACTION);
					broadcastIntent.putExtra(ERROR_COUNT, isHasError);
					broadcastIntent.putExtra(SERVICE_STATE_KEY, isRealTimeFinished);
					mContext.sendBroadcast(broadcastIntent);
				}
				//Thread.sleep(15000);
				stationBoardDataService.executeStationBoardBulkQuery(stationBoardCollection, languageBeforSetting, mContext);
				stationBoardDataService.saveCreateStationBoardStatus(mContext, true);
			}
			
		}catch (CustomError e) {
			//catchError(NetworkError.CustomError, e);
			isHasError = true; 
		}catch (Exception e) {
			//stationBoardBulkCallErrorCount ++;
			isHasError = true;
			e.printStackTrace();
		}finally{				
			isRealTimeFinished = true;
			isRefreshingOneDnr = false;
			REFRESHING_DNR = null;
			//isHasError = true;			
			if (mContext != null) {
				Intent broadcastIntent = new Intent(ServiceConstant.REALTIME_SERVICE_ACTION);
				broadcastIntent.putExtra(ERROR_COUNT, isHasError);
				broadcastIntent.putExtra(SERVICE_STATE_KEY, isRealTimeFinished);
				mContext.sendBroadcast(broadcastIntent);
			}
		}
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
	}	
}
