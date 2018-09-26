package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;


public class CheckLastUpdatePwdAsyncTask extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private Handler handler;
    public CheckLastUpdatePwdAsyncTask(Context context, Handler handler){
        this.mContext = context;
        this.handler = handler;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LogUtils.d("CheckLastUpdatePwdAsyncTask", "doInBackground------->");

        NMBSApplication.getInstance().getLoginService().syncCheckPwd();

        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        sendMessageByWhat();
    }

    private void sendMessageByWhat(){
        Message message = new Message();
        if (handler != null) {
            LogUtils.d("CheckLastUpdatePwdAsyncTask", "sendMessageByWhat------->");
            handler.sendMessage(message);
        }
    }
}
