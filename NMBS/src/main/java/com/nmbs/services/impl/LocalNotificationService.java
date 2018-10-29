package com.nmbs.services.impl;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.nmbs.R;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.util.AppUtil;
import com.nmbs.util.LocalNotificationReceiver;

import java.util.Date;

public class LocalNotificationService extends Service {

    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //Intent broadcastIntent = new Intent(this.getApplicationContext(), LocalNotificationReceiver.class);
        String name = AppUtil.getRunningActivity(getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.
                getActivities(this.getApplicationContext(), 0, makeIntentStack(getApplicationContext()), PendingIntent.FLAG_UPDATE_CURRENT);
        if(name.contains("com.nmbs")){
            Intent i = MyTicketsActivity.createIntent(getApplicationContext());
            pendingIntent = PendingIntent.
                    getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap bitmap = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.ic_notification);
        Date now = new Date();
        //LogUtils.e("RunningActivity", "i------->" + new Date().getHours());
        //LogUtils.e("RunningActivity", "i------->" + new Date().getMinutes());
        if(now.getHours() == 16 && now.getMinutes() < 1){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.getApplicationContext())
                    .setSmallIcon(R.drawable.ic_notification)
                    /*.setLargeIcon(bitmap)*/
                    .setContentTitle(this.getApplicationContext().getResources().getString(R.string.push_notification_new_message))
                    .setContentText(this.getApplicationContext().getResources().getString(R.string.local_notification_text))
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
        return START_NOT_STICKY;
    }

    Intent[] makeIntentStack(Context context) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
        intents[1] = MyTicketsActivity.createIntent(context);
        return intents;
    }
}