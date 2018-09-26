package com.nmbs.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.nmbs.R;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.exceptions.BookingTimeOutError;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DBooking343Error;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.DonotContainTicket;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.exceptions.RequestFail;
import com.nmbs.exceptions.TimeOutError;
import com.nmbs.model.DossierDetailParameter;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.Order;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.PushService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.TrackerConstant;

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
