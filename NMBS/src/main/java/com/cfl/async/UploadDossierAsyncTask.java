package com.cfl.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.cfl.R;
import com.cfl.activities.MyTicketsActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.dataaccess.restservice.impl.DossierDetailDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.DonotContainTicket;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NetworkError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.DossierDetailParameter;
import com.cfl.model.DossierDetailsResponse;
import com.cfl.model.DossierSummary;
import com.cfl.model.Order;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.PushService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.TrackerConstant;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UploadDossierAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = UploadDossierAsyncTask.class.getSimpleName();

    public static final String UploadDossier_Broadcast = "UploadDossier_Broadcast";
    private Context mContext;
    public static boolean isUploading = false;
    private String dnr;
    private String email;
    private Handler handler;
    public static boolean isUploadSuccessful = false;
    public static final String Intent_Key_ErrorMessage = "ErrorMessage";
    public static final String Intent_Key_Status = "Status";
    public static final String Intent_Key_Dnr = "Dnr";
    public static final String Intent_Key_Email = "Email";
    private DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
    public UploadDossierAsyncTask(Handler handler, Context mContext, String dnr, String email) {
        this.mContext = mContext;
        this.handler = handler;
        this.dnr = dnr;
        this.email = email;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        isUploading = true;
        uploadDossier();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public void uploadDossier() {
        final DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
        Log.d("uploadDossier", "uploading Dossier::::" );
        DossierDetailsResponse dossierResponse = null;
        String responseErrorMessage = null;
        try {
            dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, email,
                   dnr, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(), true, false, true);
            if(dossierResponse == null || dossierResponse.getDossier() == null){
                Log.d("uploadDossier", "is not UploadSuccessful::::" );
                isUploadSuccessful = false;
                sendMessageByWhat(mContext.getResources().getString(R.string.general_server_unavailable));
            }else{
                Log.d("uploadDossier", "isUploadSuccessful::::" );
                isUploadSuccessful = true;
                sendMessageByWhat(null);
                dossierDetailsService.enableSubscription(dossierResponse.getDossier(), handlerEnable, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());


            }
        }catch (ConnectionError e) {
            //Log.d(TAG, "ConnectionError...: ");
            responseErrorMessage = mContext.getResources().getString(R.string.upload_tickets_failure_parameter_mismatch);
            isUploadSuccessful = false;
            sendMessageByWhat(responseErrorMessage);
        }catch (DonotContainTicket e) {
            //Log.d(TAG, "DonotContainTicket...: ");
            responseErrorMessage = mContext.getResources().getString(R.string.upload_tickets_failure_notravelsegment);
            isUploadSuccessful = false;
            sendMessageByWhat(responseErrorMessage);

        }catch (CustomError e) {
            Log.d("uploadDossier", "is not UploadSuccessful::::" );
            isUploadSuccessful = false;
            sendMessageByWhat(e.getMessage());

        }catch (Exception e) {
            e.printStackTrace();
            responseErrorMessage = mContext.getString(R.string.general_server_unavailable);
            isUploadSuccessful = false;
            sendMessageByWhat(responseErrorMessage);

        } finally {
            isUploading = false;
        }
    }

    private void sendMessageByWhat(String errorMessage){
        Message message = new Message();
        int status = 0;
        if(isUploadSuccessful){
            message.what = 1;
            status = 1;
        }else{
            message.what = 0;
            GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.AUTO_UPLOAD_TICKET_CATEGORY, TrackerConstant.AUTO_UPLOAD_TICKET_ERROR, "");
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Intent_Key_ErrorMessage, errorMessage);
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
        Intent broadcastIntent = new Intent(UploadDossier_Broadcast);
        if(status == 0){
            broadcastIntent.putExtra(Intent_Key_ErrorMessage, errorMessage);
            broadcastIntent.putExtra(Intent_Key_Dnr, dnr);
            broadcastIntent.putExtra(Intent_Key_Email, email);
        }
        mContext.sendBroadcast(broadcastIntent);
    }

    private Handler handlerEnable = new Handler(){
        public void handleMessage(Message msg) {
            //Log.e(TAG, "handleMessage...");
            //Log.e(TAG, "msg.what..." + msg.what);
            DossierSummary dossierSummary = dossierDetailsService.getDossier(dnr);
            //Log.e(TAG, "msg.what..." + msg.what);
            switch (msg.what) {
                case 0:
                    if(dossierSummary != null){
                        dossierSummary.setDossierPushEnabled(false);
                        dossierDetailsService.updateDossier(dossierSummary);
                        dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                    }
                    GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.AUTO_UPLOAD_TICKET_CATEGORY, TrackerConstant.AUTO_UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION, "");
                    Log.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
                    break;
                case 1:
                    if (dossierSummary != null){
                        dossierSummary.setDossierPushEnabled(true);
                        dossierDetailsService.updateDossier(dossierSummary);
                        dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                        //Log.d("Connection", "status..isPushEnabled..." + dossierDetailsService.isPushEnabled(dossier));
                    }
                    //finish();
                    GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.AUTO_UPLOAD_TICKET_CATEGORY, TrackerConstant.AUTO_UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION, "");
                    Log.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
                    break;
                case 2:
                    if(dossierSummary != null){
                        dossierSummary.setDossierPushEnabled(true);
                        dossierDetailsService.updateDossier(dossierSummary);
                        dossierDetailsService.setCurrentDossierSummary(dossierSummary);
                        //Log.d("Connection", "status..isPushEnabled..." + dossierDetailsService.isPushEnabled(dossier));
                    }
                    break;
                case 3:
                    break;
                case PushService.USER_ERROR:
                    //createSubscriptionErrorView.setText(activity.getResources().getString(R.string.alert_subscription_missingID));
                    GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.AUTO_UPLOAD_TICKET_CATEGORY, TrackerConstant.AUTO_UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION, "");
                    Log.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
                    break;
            }
        };
    };
}
