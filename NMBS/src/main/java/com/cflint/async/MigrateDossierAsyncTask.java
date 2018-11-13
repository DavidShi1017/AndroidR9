package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;


import com.cflint.application.NMBSApplication;
import com.cflint.dataaccess.restservice.IMasterDataService;
import com.cflint.dataaccess.restservice.impl.DossierDetailDataService;
import com.cflint.dataaccess.restservice.impl.MasterDataService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.DBooking343Error;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.DossierDetailParameter;
import com.cflint.model.DossierDetailsResponse;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Order;
import com.cflint.services.IAssistantService;
import com.cflint.services.IMasterService;
import com.cflint.services.impl.ServiceConstant;

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
