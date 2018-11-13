package com.cflint.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;


import com.cflint.application.NMBSApplication;
import com.cflint.dataaccess.restservice.IMasterDataService;
import com.cflint.dataaccess.restservice.impl.MasterDataService;
import com.cflint.handler.RestMasterDataHandler;
import com.cflint.services.IMessageService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MasterDataAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = MasterDataAsyncTask.class.getSimpleName();
    private String languageBeforSetting;
    public static boolean isMessageFinished;
    private Context mContext;
    private String dnr;
    public static boolean isRealTimeFinished = true;
    private boolean isSucceed;
    public static final String ERROR_COUNT = "ErrorCount";
    public static final String SERVICE_STATE_KEY = "RealTimeFinished";
    public static String REFRESHING_DNR;
    public static boolean isRefreshingOneDnr;
    private Timer timer;
    private TimerTask timerTask;
    private IMessageService messageService;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private RestMasterDataHandler handler;



    public MasterDataAsyncTask(String languageBeforSetting, Context mContext, RestMasterDataHandler handler, IMessageService messageService) {

        this.languageBeforSetting = languageBeforSetting;
        this.mContext = mContext;
        this.handler = handler;
        this.messageService = messageService;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        downloadHomeBanner();
        downloadGengeralSetting();
        downloadMessages();
        downloadTrainInfo();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                finishedExecute();
            }
        };
        timer.schedule(timerTask, 15000);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }


    private void downloadHomeBanner() {
        executorService.submit(new Runnable() {
            public void run() {
                HomeBannerAsyncTask asyncTask = new HomeBannerAsyncTask(languageBeforSetting, mContext);
                isSucceed = asyncTask.downloadHomeBanner();


            }
        });
    }
    private void downloadGengeralSetting(){
        executorService.submit(new Runnable() {
            public void run() {

                GengeralSettingAsyncTask asyncTask1 = new GengeralSettingAsyncTask(languageBeforSetting, mContext);
                asyncTask1.downloadGeneralSetting();
                //changeLanguageRestCount ++;

            }
        });
    }
    private void downloadMessages(){
        executorService.submit(new Runnable() {
            public void run() {
                MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(messageService, languageBeforSetting, mContext);
                isSucceed = mobileMessageAsyncTask.downloadMessages();
                //changeLanguageRestCount ++;

            }
        });
    }
    private void downloadTrainInfo(){
        executorService.submit(new Runnable() {
            public void run() {
                StationInfoAsyncTask stationInfoAsyncTask = new StationInfoAsyncTask(NMBSApplication.getInstance().getStationInfoService(),
                        NMBSApplication.getInstance().getSettingService(), mContext, true);
                stationInfoAsyncTask.execute((Void)null);


            }
        });
        //finishedExecute();
    }
    private void finishedExecute() {
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        Message message = new Message();
        message.what = RestMasterDataHandler.SERVICE_SUCCEED;
        //Log.e(TAG, "service status is..." + message.what);
        handler.handleMessage(message);
       /* if (changeLanguageRestCount == changeLanguageRestMaxCount) {
            if(isSucceed){
                message.what = RestMasterDataHandler.SERVICE_SUCCEED;
            }else {
                message.what = RestMasterDataHandler.SERVICE_SUCCEED;
            }

        }*/
    }
}
