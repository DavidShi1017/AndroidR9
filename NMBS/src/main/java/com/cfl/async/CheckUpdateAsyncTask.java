package com.cfl.async;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cfl.exceptions.NetworkError;
import com.cfl.services.ICheckUpdateService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.ServiceConstant;

public class CheckUpdateAsyncTask extends AsyncTask<ISettingService, Void, Void>{
	private ICheckUpdateService checkUpdateService;
	private Handler mHandler;
	public CheckUpdateAsyncTask(Handler handler, ICheckUpdateService checkUpdateService){
		this.checkUpdateService = checkUpdateService;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		//showProgressDialog();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(ISettingService... params) {
		try {
			if(checkUpdateService.isExecuteCheckAppUpdate()){
			checkUpdateService.setCheckAppManually(false);
			checkUpdateService.setIsReady(true);
			checkUpdateService.checkAppVersion(params[0].getCurrentLanguagesKey());
			sendMessageByWhat(ServiceConstant.MESSAGE_WHAT_OK);
			}else{
				checkUpdateService.setIsReady(false);
				sendMessageByWhat(ServiceConstant.MESSAGE_WHAT_ERROR);
				return null;
			}
		} catch (Exception e) {
			sendMessageByWhat(ServiceConstant.MESSAGE_WHAT_ERROR);
			e.printStackTrace();
		}
		return null;
	}
	

	//send handler message
	private void sendMessageByWhat(int messageWhat){
		Message message = new Message();
		message.what = messageWhat;
		Bundle bundle = new Bundle();
		bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, NetworkError.CONNECTION);
		message.setData(bundle);
		if (mHandler != null) {
			mHandler.sendMessage(message);		
		}
		
	}

}
