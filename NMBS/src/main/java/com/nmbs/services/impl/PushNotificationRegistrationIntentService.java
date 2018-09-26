package com.nmbs.services.impl;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.GetSubScriptionListAsyncTask;
import com.nmbs.model.HafasUser;

import com.nmbs.services.IPushService;
import com.nmbs.services.ISettingService;

public class PushNotificationRegistrationIntentService extends IntentService {

    private static final String TAG = "Push";
    private static final String[] TOPICS = {"global"};
    private IPushService pushService;
    private String token = "";
    public PushNotificationRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.pushService = ((NMBSApplication) getApplication()).getPushService();
       /* try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            //token = "adgqewasvs1234123";
            Log.i(TAG, "GCM Registration Token: " + token);
            this.pushService.saveRegistrationId(token);
            sendRegistrationToServer(token);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        IPushService pushService = ((NMBSApplication) getApplication()).getPushService();
        ISettingService settingService = ((NMBSApplication) getApplication()).getSettingService();
        try{
            /*String notificationTime = SettingsPref.getStartNotifiTime(getApplicationContext());
            if(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
                notificationTime = notificationTime.replace("min.","").trim();
            }else{
                notificationTime = "5";
            }

            String minDelay = SettingsPref.getDelayNotifiTime(getApplicationContext());
            if(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
                minDelay = notificationTime.replace("min.","").trim();
            }else{
                minDelay = "5";
            }*/
            int start = NMBSApplication.getInstance().getSettingService().getStartNotifiTimeIntger();
            int delay = NMBSApplication.getInstance().getSettingService().getDelayNotifiTimeIntger();
            String userId = pushService.createAccount(new HafasUser("", pushService.getRegistrationId(),
                    NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(), delay,
                    delay, start));
            if("".equals(userId)){
                userId = pushService.createAccount(new HafasUser("",pushService.getRegistrationId(),settingService.getCurrentLanguagesKey(),
                        delay, delay, start));
            }
            GetSubScriptionListAsyncTask asyncTask = new GetSubScriptionListAsyncTask(pushService,settingService.getCurrentLanguagesKey(),getApplicationContext());
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }catch (Exception e){

        }

    }
}
