package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.dataaccess.database.DossierDatabaseService;
import com.cflint.dataaccess.restservice.impl.DossierDetailDataService;
import com.cflint.exceptions.CustomError;
import com.cflint.model.DossierDetailsResponse;
import com.cflint.model.DossierSummary;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.services.impl.PushService;

public class RefreshDossierAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = RefreshDossierAsyncTask.class.getSimpleName();

    public static final String RefreshDossier_Broadcast = "Refresh_Broadcast";
    private Context mContext;
    public static boolean isRefreshing = false;
    private String dnr;
    private Handler handler;
    public static boolean isRefreshSuccessful = false;
    public static final String Intent_Key_Status = "Status";
    private DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
    public RefreshDossierAsyncTask(Handler handler, Context mContext, String dnr) {
        this.mContext = mContext;
        this.handler = handler;
        this.dnr = dnr;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        isRefreshing = true;
        refreshDossier();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public void refreshDossier() {
        final DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
        Log.d("refreshDossier", "uploading Dossier::::" );
        DossierDetailsResponse dossierResponse = null;
        try {
            dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, null,
                   dnr, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(), true, false, true);
            isRefreshing = false;
            if(dossierResponse == null || dossierResponse.getDossier() == null){
                Log.d("refreshDossier", "is not UploadSuccessful::::" );
                isRefreshSuccessful = false;
                sendMessageByWhat(mContext.getResources().getString(R.string.general_server_unavailable));
            }else{
                Log.d("refreshDossier", "isUploadSuccessful::::" );
                DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(mContext);
                DossierSummary dossierSummary = dossierDatabaseService.selectDossier(dossierResponse.getDossier().getDossierId());
                dossierDetailsService.setCurrentDossier(dossierResponse.getDossier());
                dossierDetailsService.autoEnableSubscription(dossierSummary, dossierResponse.getDossier(), null, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                dossierDetailsService.deleteSubscription(dossierResponse.getDossier());
                isRefreshSuccessful = true;
                sendMessageByWhat(null);
            }
        } catch (CustomError customError) {
            Log.d("refreshDossier", "is not UploadSuccessful::::" );
            isRefreshSuccessful = false;
            isRefreshing = false;
            sendMessageByWhat(customError.getMessage());
        } catch (Exception e) {
            Log.d("refreshDossier", "is not UploadSuccessful::::" );
            isRefreshSuccessful = false;
            isRefreshing = false;
            sendMessageByWhat(mContext.getResources().getString(R.string.general_server_unavailable));
        }finally {
            isRefreshing = false;
        }
    }

    private void sendMessageByWhat(String errorMessage){
        Message message = new Message();
        if(isRefreshSuccessful){
            message.what = 1;
        }else{
            message.what = 0;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Intent_Key_Status, errorMessage);
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
        isRefreshing = false;
        Intent broadcastIntent = new Intent(RefreshDossier_Broadcast);
        mContext.sendBroadcast(broadcastIntent);
    }
}
