package com.cfl.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cfl.R;
import com.cfl.application.NMBSApplication;
import com.cfl.log.LogUtils;
import com.cfl.model.ForgotPwdInfoResponse;
import com.cfl.services.impl.LoginService;


public class ProfileInfoAsyncTask extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private Handler handler;
    private String email;
    public static final String Intent_Key_Error = "Error";
    public ProfileInfoAsyncTask(Context context, Handler handler){
        this.mContext = context;
        this.handler = handler;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LogUtils.d("ProfileInfoAsyncTask", "doInBackground------->");
        NMBSApplication.getInstance().getLoginService().syncProfileInfo();
        sendMessageByWhat();
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

    private void sendMessageByWhat(){
        Message message = new Message();
        if (handler != null) {
            LogUtils.d("ProfileInfoAsyncTask", "sendMessageByWhat------->");
            handler.sendMessage(message);
        }
    }
}
