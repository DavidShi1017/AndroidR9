package com.nmbs.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nmbs.activities.MainActivity;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.activities.WebViewActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;
import com.nmbs.services.IPushService;

import java.util.List;

/**
 * Created by Richard on 5/16/16.
 */
public class LocalNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断app进程是否存活
        int e_requestCode = intent.getIntExtra("RequestCode", 0);
        /*LogUtils.d("LocalNotificationReceiver", "e_requestCode-------->" + e_requestCode);
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo task = tasks.get(tasks.size() - 1); // Should be my task
        ComponentName rootActivity = task.topActivity;
        LogUtils.d("LocalNotificationReceiver", "rootActivity-------->" + rootActivity);
        if(rootActivity != null){
            if(!rootActivity.getClassName().contains("Activity")){
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Intent detailIntent = MyTicketsActivity.createIntent(context);
                if(e_requestCode > 0){
                    detailIntent = WebViewActivity.createIntent(context,
                            Utils.getUrl(context, NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl(),
                                    NMBSApplication.getInstance().getMasterService().loadGeneralSetting()), WebViewActivity.NORMAL_FLOW, "");
                }
                Intent[] intents = {mainIntent, detailIntent};
                context.startActivities(intents);
            }else{
                Intent mainIntent = new Intent();
                mainIntent.setComponent(rootActivity);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Intent detailIntent = MyTicketsActivity.createIntent(context);
                if(e_requestCode > 0){
                    detailIntent = WebViewActivity.createIntent(context,
                            Utils.getUrl(context, NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl(),
                                    NMBSApplication.getInstance().getMasterService().loadGeneralSetting()), WebViewActivity.NORMAL_FLOW, "");
                }
                Intent[] intents = {mainIntent, detailIntent};
                context.startActivities(intents);
            }
        }*/


        if(AppUtil.isAppAlive(context, "com.nmbs")){
            LogUtils.i("NotificationReceiver", "the app process is alive");
            Intent mainIntent = new Intent(context, MainActivity.class);

            Intent detailIntent = MyTicketsActivity.createIntent(context);
            if(e_requestCode > 0){
                detailIntent = WebViewActivity.createIntent(context,
                        Utils.getUrl(NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, "");
            }

            Intent[] intents = {mainIntent, detailIntent};
            context.startActivities(intents);
        }else {
            LogUtils.i("NotificationReceiver", "the app process is alive");
            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入             //参数跳转到DetailActivity中去了
            LogUtils.i("NotificationReceiver", "the app process is dead");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("com.nmbs");
            /*launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);*/
            context.startActivity(launchIntent);
        }
    }
}