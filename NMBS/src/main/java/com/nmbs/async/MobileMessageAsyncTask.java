package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.nmbs.dataaccess.database.MessageDatabaseService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.ServiceConstant;

public class MobileMessageAsyncTask extends AsyncTask<Void, Void, Void>{    	
	private IMessageService messageService;
	private ISettingService settingService;
	public static boolean isMessageFinished;
	private Context mContext;
	private String language;
	private boolean serviceStatus;
	private static final String TAG = MobileMessageAsyncTask.class.getSimpleName();
	public MobileMessageAsyncTask(IMessageService messageService,String language, Context mContext){
		this.messageService = messageService;
		this.settingService = settingService;
		this.mContext = mContext;
		this.language = language;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {

		downloadMessages();
		return null;
	}		
	@Override
	protected void onPostExecute(Void result) {	
		
		super.onPostExecute(result);
	}

	public boolean downloadMessages(){
		//Log.e("Messages",  "Messages: " + i);
		try {
			isMessageFinished = false;
			//Thread.sleep(15000);
			//Log.d(TAG, "MessageAsyncTask has finished....");
			messageService.getMessageData(language, false);
			serviceStatus = true;
		} catch (Exception e) {
			isMessageFinished = true;
			serviceStatus = false;
			//Log.d(TAG, "MessageAsyncTask has Exception....");
			e.printStackTrace();
		}finally{
			isMessageFinished = true;
			if (mContext != null) {
				//Log.d(TAG, "MessageAsyncTask send broadcast....");
				Intent broadcastIntent = new Intent(ServiceConstant.MESSAGE_SERVICE_ACTION);
				mContext.sendBroadcast(broadcastIntent);
			}
		}
		return serviceStatus;
	}
}
