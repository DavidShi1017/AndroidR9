package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;


import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.restservice.IMasterDataService;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.dataaccess.restservice.impl.MasterDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.DossierDetailParameter;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Order;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.impl.ServiceConstant;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MigrateDossierAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = MigrateDossierAsyncTask.class.getSimpleName();
    private String languageBeforSetting;
    public static final String MigrateDossier_Broadcast = "RealTime_Broadcast_";
    private Context mContext;
    public static boolean isMigrateing = false;
    private int totalCount = 0;
    private int currentCount = 0;
    private List<Order> listOrders;
    private Handler handler;
    public static final int MigrateSuccessful = 1;
    public static final int MigrateUnSuccessful = -1;
    public static final String Intent_Key_Status = "Status";
    private boolean hasError;
    public MigrateDossierAsyncTask(Handler handler, String languageBeforSetting, Context mContext, List<Order> listOrders) {
        this.languageBeforSetting = languageBeforSetting;
        this.mContext = mContext;
        this.listOrders = listOrders;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        isMigrateing = true;
        migratingDossier();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public void migratingDossier() {

        List<DossierDetailParameter> dossiers = new ArrayList<>();
        final DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
        for(Order order : listOrders){
            if(order != null){
                DossierDetailParameter dossier = new DossierDetailParameter(order.getDNR(), null);
                dossiers.add(dossier);
            }
        }
        if(listOrders != null){
            totalCount = listOrders.size();
        }
        //Log.d("MigrateDossier", "The Total count is::::" + totalCount);

        for (final DossierDetailParameter dossierDetailParameter : dossiers){
            if(dossierDetailParameter != null){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DossierDetailsResponse dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, null,
                                    dossierDetailParameter.getDnr(), languageBeforSetting, true, false, true);
                            if (dossierResponse == null){
                                hasError = true;
                            }else {
                                NMBSApplication.getInstance().getAssistantService().deleteDataByDNR(dossierDetailParameter.getDnr());
                            }
                        }catch (Exception e) {
                            hasError = true;
                        }
                        currentCount++;
                        finishMigrateDossier();
                        //Log.e("MigrateDossier", "The Total currentCount is::::" + currentCount);
                    }
                }).start();

            }
        }



            /*isMigrateing = false;
            Message message = new Message();


            if(handler != null){
                handler.sendMessage(message);
            }*/
            //Log.e(TAG, "The totalCount ==  currentCount, Migrated Dossier finished::::");

    }

    private void finishMigrateDossier(){
        if(currentCount == totalCount){
            isMigrateing = false;
            int status = MigrateUnSuccessful;
            if (hasError){
                status = MigrateUnSuccessful;
            }else {
                status = MigrateSuccessful;
            }
            Intent broadcastIntent = new Intent(MigrateDossier_Broadcast);
            broadcastIntent.putExtra(Intent_Key_Status, status);
            mContext.sendBroadcast(broadcastIntent);
        }
    }
}
