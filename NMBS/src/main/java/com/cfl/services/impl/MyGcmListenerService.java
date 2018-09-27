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

package com.cfl.services.impl;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.cfl.R;
import com.cfl.activities.MainActivity;
import com.cfl.activities.PushNotificationErrorActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.util.ActivityConstant;
import com.cfl.util.PushNotificationReceiver;

import java.util.List;
import java.util.Map;

public class MyGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";


    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.e("Push", "onMessageReceived...");
        if(remoteMessage != null){
            if (remoteMessage.getData().size() > 0) {
                //Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            }
            if (remoteMessage.getNotification() != null) {
                //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                //sid = remoteMessage.getNotification().getBody();
            }
            Map data = remoteMessage.getData();
            String destination = (String)data.get("destination");
            String level = (String)data.get("level");
            String origin = (String)data.get("origin");
            String sid = (String)data.get("data.sid");
            String stype = (String)data.get("stype");
            //Log.e("Push", "onMessageReceived data is..." + sid);

            sendNotification(destination,level,origin,sid,stype);
        }

    }

/*    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        String from = remoteMessage.getFrom();
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        String sid = "";
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sid = remoteMessage.getNotification().getBody();
        }
*//*        Map data = remoteMessage.getData();
        String destination = (String)data.get("destination");
        String level = (String)data.get("level");
        String origin = (String)data.get("origin");
        String sid = (String)data.get("data.sid");
        String stype = (String)data.get("stype");
        Log.e("Push", "onMessageReceived data is..." + sid);
        sendNotification(destination,level,origin,sid,stype);*//*
        sendNotification("","","",sid,"");
    }*/

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     */
    private void sendNotification(String destination, String level, String origin, String sid, String stype) {
        System.out.println("sendNotification............");
        //Log.e("Push", "MyGcmListenerService subscriptionId is..." + sid);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(sid == null || sid.isEmpty()){
           sid = "123";
        }

        Intent broadcastIntent = new Intent(this, PushNotificationReceiver.class);

        broadcastIntent.putExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID,sid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                //.setTicker("New message")
                .setSmallIcon(R.drawable.ic_notification)
                /*.setLargeIcon(bitmap)*/

                .setContentTitle(getResources().getString(R.string.push_notification_new_message))
                .setContentText(getResources().getString(R.string.push_notification_read_new_message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setWhen(0)
                .setContentIntent(pendingIntent);

      //  notificationManager.setLatestEventInfo(context, title, message, intent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
