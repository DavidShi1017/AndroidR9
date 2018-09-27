package com.cfl.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cfl.dataaccess.restservice.IMasterDataService;
import com.cfl.dataaccess.restservice.impl.MasterDataService;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.services.IMessageService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;

import org.json.JSONException;

public class TrainIconsAsyncTask extends AsyncTask<Void, Void, Void>{

	private String language;
	private Context mContext;
	public TrainIconsAsyncTask(String language, Context mContext){
		this.language = language;
		this.mContext = mContext;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		downloadTrainIcons();
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	public void downloadTrainIcons(){
		IMasterDataService masterDataService = new MasterDataService();
		try {
			//Thread.sleep(15000);

			masterDataService.executeTrainIcons(mContext, language);
		} catch (Exception e) {
			try {
				masterDataService.storeTrainIcon(mContext);
			} catch (InvalidJsonError invalidJsonError) {
				invalidJsonError.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
