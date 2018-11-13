package com.cflint.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cflint.application.NMBSApplication;
import com.cflint.exceptions.NetworkError;
import com.cflint.model.HafasUser;
import com.cflint.services.ICheckUpdateService;
import com.cflint.services.ISettingService;
import com.cflint.services.impl.ServiceConstant;

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
