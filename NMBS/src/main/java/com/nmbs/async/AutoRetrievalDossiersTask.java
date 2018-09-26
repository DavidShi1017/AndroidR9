package com.nmbs.async;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.nmbs.R;
import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.log.LogUtils;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossiersList;
import com.nmbs.model.LoginResponse;
import com.nmbs.model.LogonInfo;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.LoginService;
import com.nmbs.util.LocalNotificationReceiver;

import java.util.ArrayList;
import java.util.List;


public class AutoRetrievalDossiersTask extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private Handler handler;
    private String customerId;
    private String hash;
    private List<DossierSummary> dossiers;
    public static final String Intent_Key_Error = "Error";
    public static final String Intent_Key_Logoninfo = "Logoninfo";
    public static final String RefreshDossier_Broadcast = "Refresh_Multiple_Broadcast";
    public static boolean isWorking;
    public AutoRetrievalDossiersTask(Context context, String customerId, String hash, List<DossierSummary> dossiers){
        this.mContext = context;
        this.customerId = customerId;
        this.hash = hash;
        this.dossiers = dossiers;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        LogUtils.d("AutoRetrievalDossiersTask", "doInBackground------->");
        LogUtils.d("AutoRetrievalDossiersTask", "dossiers--->" + dossiers.size());
        List<String> refreshDossierIds = new ArrayList<>();
        List<String> refreshDnrs = new ArrayList<>();
        for(DossierSummary dossierSummary : dossiers){
            if(dossierSummary != null){
                refreshDossierIds.add(dossierSummary.getDossierId());
            }
        }
        try {
            DossierDetailDataService service = new DossierDetailDataService();
            DossiersList dossiersList = service.executeDossierList(mContext, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
                    customerId, hash);
            if(dossiersList != null){
                List<String> list = dossiersList.getDnrs();
                for (String dnr : list){
                    if(!refreshDossierIds.contains(dnr)){
                        refreshDnrs.add(dnr);
                    }
                }
            }
            if(refreshDnrs.size() > 0){
                isWorking = true;
                sendMessageByWhat();
                RefreshMultipleDossierAsyncTask asyncTask = new RefreshMultipleDossierAsyncTask(mContext, refreshDnrs);
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        } catch (Exception e) {
            LogUtils.e("AutoRetrievalDossiersTask", "Exception--->" + e.getMessage());
            e.printStackTrace();
        }
        //isWorking = false;
        //sendMessageByWhat();
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
    private void sendMessageByWhat(){

        //if(currentCount == totalCount){

            Intent broadcastIntent = new Intent(RefreshDossier_Broadcast);
            mContext.sendBroadcast(broadcastIntent);
            LogUtils.e("AutoRetrievalDossiersTask", "handleMessage...isRefreshing....." + isWorking);
        //}
    }

}
