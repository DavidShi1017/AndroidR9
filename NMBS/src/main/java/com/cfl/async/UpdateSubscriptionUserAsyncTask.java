package com.cfl.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cfl.application.NMBSApplication;
import com.cfl.exceptions.NetworkError;
import com.cfl.model.HafasUser;
import com.cfl.services.ICheckUpdateService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.ServiceConstant;

public class UpdateSubscriptionUserAsyncTask extends AsyncTask<ISettingService, Void, Void>{
	private ICheckUpdateService checkUpdateService;
	private HafasUser hafasUser;
	private Context context;
	private Handler handler;
	public UpdateSubscriptionUserAsyncTask(HafasUser hafasUser, Context context, Handler handler){
		this.context = context;
		this.hafasUser = hafasUser;
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		//showProgressDialog();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(ISettingService... params) {
		try {
			NMBSApplication.getInstance().getPushService().updateAccount(hafasUser);
			sendMessageByWhat(ServiceConstant.MESSAGE_WHAT_OK);
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
		//Bundle bundle = new Bundle();
		//bundle.putSerializable(ServiceConstant.PARAM_OUT_ERROR, NetworkError.CONNECTION);
		//message.setData(bundle);
		if (handler != null) {
			handler.sendMessage(message);
		}
	}

}
