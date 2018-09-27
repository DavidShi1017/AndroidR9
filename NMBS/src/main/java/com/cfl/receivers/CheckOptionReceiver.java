package com.cfl.receivers;

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

import com.cfl.R;
import com.cfl.application.NMBSApplication;
import com.cfl.async.CheckOptionAsyncTask;
import com.cfl.log.LogUtils;
import com.cfl.model.CheckOption;
import com.cfl.util.DateUtils;
import com.cfl.util.LocalNotificationReceiver;

import java.util.Date;

public class CheckOptionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("CheckOptionReceiver", "CheckOptionReceiver...");
        Bundle bundle = intent.getExtras();
        int e_requestCode = bundle.getInt("RequestCode");
        LogUtils.e("CheckOptionReceiver", "CheckOptionReceiver..." + e_requestCode);

        if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
            CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(context);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

}
