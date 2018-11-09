package com.nmbs.async;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nmbs.R;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.activities.WebViewActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.DossierDatabaseService;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.exceptions.ConnectionError;
import com.nmbs.exceptions.CustomError;
import com.nmbs.exceptions.DonotContainTicket;
import com.nmbs.log.LogUtils;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.PushService;
import com.nmbs.util.AppUtil;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.LocalNotificationReceiver;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.List;

public class RefreshMultipleDossierAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = RefreshMultipleDossierAsyncTask.class.getSimpleName();
    private int currentCount;
    private int totalCount;

    private Context mContext;
    public static boolean isRefreshing = false;
    private List<String> dnrs;
    private String dnr;

    private DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
    public RefreshMultipleDossierAsyncTask(Context mContext, List<String> dnrs) {
        isRefreshing = true;
        this.mContext = mContext;
        this.dnrs = dnrs;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        refreshDossier();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public void refreshDossier() {
        final DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
        //Log.d("uploadDossier", "uploading Dossier::::" + dnrs.size());
        totalCount = dnrs.size();
        DossierDetailsResponse dossierResponse = null;
        boolean isSent = false;
        for(int i = 0; i < dnrs.size(); i ++){
            try {
                dnr = dnrs.get(i);
                //Log.d("uploadDossier", "uploading Dossier::::" + dnr);
                dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, NMBSApplication.getInstance().getLoginService().getLogonInfo().getEmail(),
                        dnr, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(), true, false, true);
                if(dossierResponse == null || dossierResponse.getDossier() == null){

                }else{
                    if(!isSent && NMBSApplication.getInstance().getSettingService().isAutoUpdate()){
                        if(NMBSApplication.getInstance().getSettingService().isDnr()){
                            if(!Utils.isAppForeground(mContext)){
                                sendNotification();
                            }
                        }
                        isSent = true;
                    }
                    dossierDetailsService.enableSubscription(dossierResponse.getDossier(), handlerEnable,
                            NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                currentCount++;
                //
            }
        }
        if(currentCount == totalCount){
            sendMessageByWhat();
        }

    }

    private void sendMessageByWhat(){

        //if(currentCount == totalCount){
        AutoRetrievalDossiersTask.isWorking = false;
        Intent broadcastIntent = new Intent(AutoRetrievalDossiersTask.RefreshDossier_Broadcast);
        mContext.sendBroadcast(broadcastIntent);
        LogUtils.e("AutoRetrievalDossiersTask", "handleMessage...isRefreshing....." + AutoRetrievalDossiersTask.isWorking);
        //}
    }

    private Handler handlerEnable = new Handler(Looper.getMainLooper()){
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
                    LogUtils.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
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
                    LogUtils.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
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
                    LogUtils.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
                    break;
            }
        };
    };

    private void sendNotification(){
        //Intent broadcastIntent = new Intent(mContext, LocalNotificationReceiver.class);
        String name = AppUtil.getRunningActivity(mContext);
        PendingIntent pendingIntent = PendingIntent.
                getActivities(mContext, 0, makeIntentStack(mContext), PendingIntent.FLAG_UPDATE_CURRENT);
        if(name.contains("com.nmbs")){
            Intent i = MyTicketsActivity.createIntent(mContext);
            pendingIntent = PendingIntent.
                    getActivity(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_notification);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notification)
                /*.setLargeIcon(bitmap)*/
                .setContentTitle(mContext.getResources().getString(R.string.push_notification_new_message))
                .setContentText(mContext.getResources().getString(R.string.local_notification_newdnr))
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    Intent[] makeIntentStack(Context context) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
        intents[1] = MyTicketsActivity.createIntent(context);
        return intents;
    }
}
