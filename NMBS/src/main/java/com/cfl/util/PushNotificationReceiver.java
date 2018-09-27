package com.cfl.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.cfl.activities.MainActivity;
import com.cfl.activities.MyTicketsActivity;
import com.cfl.activities.PushNotificationErrorActivity;
import com.cfl.application.NMBSApplication;

import java.util.List;

/**
 * Created by Richard on 5/16/16.
 */
public class PushNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断app进程是否存活
        String sid = intent.getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID);

        Log.e("Push", "savedInstanceState...." + sid);
        //GoogleAnalyticsUtil.getInstance().sendScreen("PushNotification");
        Activity activity = NMBSApplication.getInstance().getActivity();
        Log.e("Push", "PushNotificationReceiver subscriptionId is..." + NMBSApplication.getInstance().getActivity());

        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo task = tasks.get(tasks.size() - 1); // Should be my task
        ComponentName rootActivity = task.topActivity;

        if(rootActivity != null){
            if(!rootActivity.getClassName().contains("Activity")){
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Intent detailIntent = PushNotificationErrorActivity.createIntent(context,sid, NMBSApplication.PAGE_PUSH);
                Intent[] intents = {mainIntent, detailIntent};
                context.startActivities(intents);
            }else{
                Intent mainIntent = new Intent();
                mainIntent.setComponent(rootActivity);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Intent detailIntent = PushNotificationErrorActivity.createIntent(context,sid, NMBSApplication.PAGE_PUSH);
                Intent[] intents = {mainIntent, detailIntent};
                context.startActivities(intents);
            }
        }





        /*if(Utils.isAppAlive(context, "com.nmbs")){
            *//*String a = null;
            a.equals("fff");*//*
            //Intent mainIntent = new Intent(context, NMBSApplication.getInstance().getActivity().getClass());

        }else {

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.nmbs");
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Bundle args = new Bundle();
            args.putString(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID, sid);
            launchIntent.putExtras(args);
            context.startActivity(launchIntent);

        }*/
    }
}