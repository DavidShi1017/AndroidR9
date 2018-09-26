package com.nmbs.services.impl;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shig on 2016/7/7.
 */

public class TestService {
    private Context context;
    private final static String TAG = TestService.class.getSimpleName();
    public static boolean isTestMode = true;

    private static final String PROPERTY_EMPTY_USER = "EmptyUser";
    private static final String PROPERTY_CHECK_APP_VERSION = "CheckAppVersion";
    private static final String PROPERTY_DOWLOAD_FILE = "DownloadFile";
    private static final String PROPERTY_CREATE_SUBSCRIPTION = "CreateSubscription";
    private static final String PROPERTY_DELETE_SUBSCRIPTION = "DeleteSubscription";
    private static final String PROPERTY_SUBSCRIPTION_URL = "SubscriptionUrl";

    private static final String PROPERTY_DELAY_SUBSCRIPTION = "DelaySubscription";
    private static final String PREFERENCES_TEST = "com.nmbs.test.app";

    public TestService(Context context){
        this.context = context;
    }

    private  SharedPreferences getTestPreferences() {
        return context.getSharedPreferences(PREFERENCES_TEST, Context.MODE_PRIVATE);
    }

    public void setHafasUrl(String url){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putString(PROPERTY_SUBSCRIPTION_URL, url);
        editor.commit();
    }
    public String getHafasUr() {
        String url = getTestPreferences().getString(PROPERTY_SUBSCRIPTION_URL, "Demo");
        return url;
    }

    public void setEmptyUser(boolean isEmptyUser){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putBoolean(PROPERTY_EMPTY_USER, isEmptyUser);
        editor.commit();
    }
    public  boolean isEmptyUser() {
        boolean isEmptyUser = getTestPreferences().getBoolean(PROPERTY_EMPTY_USER, false);
        return isEmptyUser;
    }

    public void setCheckAppVersion(boolean isCheckAppVersion){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putBoolean(PROPERTY_CHECK_APP_VERSION, isCheckAppVersion);
        editor.commit();
    }
    public  boolean isCheckAppVersion() {
        boolean isCheckAppVersion = getTestPreferences().getBoolean(PROPERTY_CHECK_APP_VERSION, false);
        return isCheckAppVersion;
    }

    public void setDownloadFile(boolean isDownloadFile){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putBoolean(PROPERTY_DOWLOAD_FILE, isDownloadFile);
        editor.commit();
    }
    public  boolean isDownloadFile() {
        boolean isDownloadFile = getTestPreferences().getBoolean(PROPERTY_DOWLOAD_FILE, false);
        return isDownloadFile;
    }

    public void setCreateSubscription(boolean isCreateSubscription){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putBoolean(PROPERTY_CREATE_SUBSCRIPTION, isCreateSubscription);
        editor.commit();
    }
    public  boolean isCreateSubscription() {
        boolean isCreateSubscription = getTestPreferences().getBoolean(PROPERTY_CREATE_SUBSCRIPTION, false);
        return isCreateSubscription;
    }

    public void setDeleteSubscription(boolean isDeleteSubscription){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putBoolean(PROPERTY_DELETE_SUBSCRIPTION, isDeleteSubscription);
        editor.commit();
    }
    public  boolean isDeleteSubscription() {
        boolean isDeleteSubscription = getTestPreferences().getBoolean(PROPERTY_DELETE_SUBSCRIPTION, false);
        return isDeleteSubscription;
    }

    public void setDelaySubscription(boolean isDelaySubscription){
        SharedPreferences.Editor editor = getTestPreferences().edit();
        editor.putBoolean(PROPERTY_DELAY_SUBSCRIPTION, isDelaySubscription);
        editor.commit();
    }
    public  boolean isDelaySubscription() {
        boolean isDelaySubscription = getTestPreferences().getBoolean(PROPERTY_DELAY_SUBSCRIPTION, false);
        return isDelaySubscription;
    }

}
