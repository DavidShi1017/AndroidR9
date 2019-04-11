/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmbs.services.impl;


import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.GetSubScriptionListAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.model.HafasUser;
import com.nmbs.services.IPushService;
import com.nmbs.services.ISettingService;
import com.nmbs.util.FunctionConfig;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "Push";
    private IPushService pushService;
/*    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, PushNotificationRegistrationIntentService.class);
        startService(intent);
    }*/
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        LogUtils.d(TAG, "Refreshed token: " + refreshedToken);
        this.pushService = ((NMBSApplication) getApplication()).getPushService();
        this.pushService.saveRegistrationId(refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        if(FunctionConfig.kFunManagePush){
            sendRegistrationToServer(refreshedToken);
        }

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
