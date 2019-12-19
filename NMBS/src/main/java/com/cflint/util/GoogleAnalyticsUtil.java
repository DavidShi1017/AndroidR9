package com.cflint.util;

import android.app.Activity;
import android.os.Bundle;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.cflint.application.NMBSApplication;

/**
 * Created by Richard on 6/3/16.
 */
public class GoogleAnalyticsUtil {
    private static GoogleAnalyticsUtil instance;
    private GoogleAnalyticsUtil (){}
    public static GoogleAnalyticsUtil getInstance() {
        if (instance == null) {
            instance = new GoogleAnalyticsUtil();
            }
        return instance;
    }
    public void sendScreen(Activity activity, String screenName){
        FirebaseAnalytics mFirebaseAnalytics = NMBSApplication.getInstance().getGaTracker();
        mFirebaseAnalytics.setCurrentScreen(activity, screenName, null);
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    public void sendEvent(String category, String action,String label){
        FirebaseAnalytics mFirebaseAnalytics = NMBSApplication.getInstance().getGaTracker();
        Bundle params = new Bundle();
        params.putString("Action", action);
        params.putString("Label", label);
        mFirebaseAnalytics.logEvent(category, params);

        /*if(label.equals("")){
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build());
        }else{
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        }*/
    }
}
