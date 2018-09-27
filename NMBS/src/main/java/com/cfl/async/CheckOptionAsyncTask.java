package com.cfl.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.cfl.application.NMBSApplication;
import com.cfl.log.LogUtils;


public class CheckOptionAsyncTask extends AsyncTask<Void, Void, Void> {

    public static final String CheckOption_Broadcast = "Refresh_Multiple_Broadcast";

    private Context mContext;
    private Handler handler;
    private String email;
    public static boolean isChecking;
    public static final String Intent_Key_Error = "Error";

    public CheckOptionAsyncTask(Context context){
        this.mContext = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LogUtils.d("CheckOptionAsyncTask", "doInBackground------->");
        isChecking = true;
       /* try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        NMBSApplication.getInstance().getLoginService().syncCheckOption();

        sendMessageByWhat();
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

    private void sendMessageByWhat(){
        isChecking = false;
        Intent broadcastIntent = new Intent(CheckOption_Broadcast);
        mContext.sendBroadcast(broadcastIntent);
    }
}
