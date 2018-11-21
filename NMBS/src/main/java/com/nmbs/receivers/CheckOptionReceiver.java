package com.nmbs.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nmbs.R;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.CheckOptionAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CheckOption;
import com.nmbs.util.DateUtils;
import com.nmbs.util.LocalNotificationReceiver;

import java.util.Date;

public class CheckOptionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtils.e("CheckOptionReceiver", "CheckOptionReceiver...");
        Bundle bundle = intent.getExtras();
        int e_requestCode = bundle.getInt("RequestCode");
        LogUtils.e("CheckOptionReceiver", "CheckOptionReceiver..." + e_requestCode);
        new Thread() {
            public void run() {
                if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
                    CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(context);
                    asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }.start();


    }

}
