package com.nmbs.receivers;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.nmbs.R;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.WebViewActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.CheckOptionAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CheckOption;
import com.nmbs.util.AppUtil;
import com.nmbs.util.DateUtils;
import com.nmbs.util.LocalNotificationReceiver;
import com.nmbs.util.Utils;

import java.util.Date;

public class CheckOptionNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("CheckOptionNotificationReceiver", "CheckOptionNotificationReceiver...");
        Bundle bundle = intent.getExtras();
        //int e_requestCode = intent.getIntExtra("RequestCode", 0);
        //LogUtils.e("CheckOptionReceiver", "CheckOptionReceiver..." + e_requestCode);
        if(NMBSApplication.getInstance().getSettingService().isOptions()){
            if(NMBSApplication.getInstance().getLoginService().isLogon()){
                CheckOption checkOption = NMBSApplication.getInstance().getLoginService().getCheckOption(context);
                if(checkOption!= null && checkOption.getExpiration() != null){
                    Date date = DateUtils.getFewLaterDay(new Date(), 1);
                    LogUtils.e("CheckOptionNotificationReceiver", "CheckOptionReceiver------Expiration------->" + checkOption.getExpiration().getDate());
                    LogUtils.e("CheckOptionNotificationReceiver", "CheckOptionReceiver------FewLaterDay------->" + date.getDate());
                    if(date.getDate() == checkOption.getExpiration().getDate()){
                        sendNotification(context);
                    }
                }
            }
        }

    }

    private void sendNotification(Context context){
        /*Intent broadcastIntent = new Intent(context, LocalNotificationReceiver.class);
        broadcastIntent.putExtra("RequestCode", 123);*/
        String name = AppUtil.getRunningActivity(context);
        PendingIntent pendingIntent = PendingIntent.
                getActivities(context, 0, makeIntentStack(context), PendingIntent.FLAG_UPDATE_CURRENT);
        if(name.contains("com.nmbs")){
            Intent intent = WebViewActivity.createIntent(context,
                    Utils.getUrl(NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, "");
            pendingIntent = PendingIntent.
                    getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getResources().getString(R.string.push_notification_new_message))
                .setContentText(context.getResources().getString(R.string.local_notification_expiration))
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());


/*        Intent broadcastIntent = new Intent(context, LocalNotificationReceiver.class);
        broadcastIntent.putExtra("RequestCode", 123);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getResources().getString(R.string.push_notification_new_message))
                .setContentText(context.getResources().getString(R.string.local_notification_expiration))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_logo);

        LogUtils.i("repeat", "showNotification");
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());*/
    }

    Intent[] makeIntentStack(Context context) {
        String name = AppUtil.getRunningActivity(context);
        LogUtils.e("CheckOptionNotificationReceiver", "name..." + name);
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
        intents[1] = WebViewActivity.createIntent(context,
                Utils.getUrl(NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, "");
        return intents;
    }


}
