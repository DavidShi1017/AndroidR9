package com.nmbs.services.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.DossiersUpToDateDatabaseService;
import com.nmbs.dataaccess.restservice.DossiersUpToDateDateService;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.log.LogUtils;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossiersUpToDate;
import com.nmbs.model.DossiersUpToDateParmeter;
import com.nmbs.model.DossiersUpToDateParmeters;
import com.nmbs.model.DossiersUpToDateResponse;
import com.nmbs.receivers.AlarmsBroadcastReceiver;
import com.nmbs.util.DateUtils;
import com.nmbs.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shig on 2016/6/13.
 */

public class DossiersUpToDateService {
    private static final String TAG = DossiersUpToDateService.class.getSimpleName();
    private static final String PROPERTY_UPDATE_TIME = "UpdateTime";
    //private static final String PROPERTY_APP_VERSION_CODE = "appVersionCode";
    private static final String PREFERENCES = "com.nmbs.update";
    private Context context;
    public static boolean stop = false;

    public DossiersUpToDateService(Context context) {
        this.context = context;
    }

    public void getDossiersUpToDateResponse(){
        DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
        String language = NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey();
        LogUtils.d("UpToDate", "Language is..." + language);
        //language = "";
        if(language == null || language.isEmpty()){
            LogUtils.d("UpToDate", "language is empty...");
            language = "en";
        }
        List<DossierSummary> dossiers = dossierDetailsService.getDossiers();
        if (dossiers != null){
            //DossiersUpToDateDatabaseService dossiersUpToDateDatabaseService = new DossiersUpToDateDatabaseService(context);
            List<DossiersUpToDateParmeter> dossiersUpToDateParmeters = new ArrayList<>();
            for (DossierSummary dossierSummary : dossiers){
                if(dossierSummary != null){
                    String lastUpdated = "";
                    String dnr = dossierSummary.getDossierId();
                    //String lastUpdated = dossiersUpToDateDatabaseService.selectLastUpdatedByDnr(dnr);
                    //Log.d(TAG, "In database the LastUpdated is..." + lastUpdated);
                    //if(lastUpdated == null || lastUpdated.isEmpty()){
                        DossierDetailsResponse dossierDetailsResponse = dossierDetailsService.getDossierDetail(dossierSummary);
                        if(dossierDetailsResponse != null && dossierDetailsResponse.getDossier() != null){
                            lastUpdated = DateUtils.dateTimeToString(dossierDetailsResponse.getDossier().getLastUpdated());
                        }
                        //Log.d("UpToDate", "In dossier the LastUpdated is..." + lastUpdated);
                  //  }

                    DossiersUpToDateParmeter dossiersUpToDateParmeter = new DossiersUpToDateParmeter(dnr, lastUpdated);
                    dossiersUpToDateParmeters.add(dossiersUpToDateParmeter);
                }
            }
            if(dossiersUpToDateParmeters != null && dossiersUpToDateParmeters.size() > 0){
                DossiersUpToDateParmeters dossiersUpToDateParmeter = new DossiersUpToDateParmeters(dossiersUpToDateParmeters);
                DossiersUpToDateDateService dossiersUpToDateDateService = new DossiersUpToDateDateService();
                try {
                    DossiersUpToDateResponse dossiersUpToDateResponse = dossiersUpToDateDateService.executeDossiersUpToDate(dossiersUpToDateParmeter, context, language);
                    refreshDossier(dossiersUpToDateResponse, language);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshDossier(DossiersUpToDateResponse dossiersUpToDateResponse, final String language){
        //Log.e(TAG, "RefreshDossier in backgroud...");
        if(dossiersUpToDateResponse != null){
            List<DossiersUpToDate> dossiersUpToDates = dossiersUpToDateResponse.getElements();
            final DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
            if(dossiersUpToDates != null){
                for(DossiersUpToDate dossiersUpToDate : dossiersUpToDates){

                    if(dossiersUpToDate != null){
                        final String dnr = dossiersUpToDate.getDnr();
                        boolean callSuccessful = dossiersUpToDate.isCallSuccessful();
                        boolean upToDate = dossiersUpToDate.isUpToDate();
                        //Log.e(TAG, "RefreshDossier in backgroud stop..." + stop);
                        if(stop && !TestService.isTestMode){
                            return;
                        }
                        //Log.e(TAG, "RefreshDossier in backgroud callSuccessful..." + callSuccessful);
                        //Log.e(TAG, "RefreshDossier in backgroud upToDate..." + upToDate);
                        if(!upToDate && callSuccessful){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Log.e(TAG, "RefreshDossier in backgroud Thread...");
                                        DossierDetailsResponse dossierResponse = dossierDetailDataService.executeDossierDetail(context, null,
                                                dnr, language, true, false, false);
                                        if(dossierResponse != null){

                                        }
                                    }catch (Exception e) {
                                    }
                                }
                            }).start();
                        }
                    }
                }
            }
        }
    }

    public static void saveUpdateTime(Context context, Date updateTime){
        //Log.e(TAG, "Now datetime is..." + updateTime);
        updateTime = DateUtils.getAfterManyHour(updateTime, 1);
        //Log.e(TAG, "One hour after datetime is..." + updateTime);
        String updateTimeStr = DateUtils.dateTimeToString(updateTime);
        SharedPreferences.Editor editor = getVersionPreferences(context).edit();
        editor.putString(PROPERTY_UPDATE_TIME, updateTimeStr);
        editor.commit();
    }

    public static Date getUpdateTime(Context context){
        String lastUpdateTime = getVersionPreferences(context).getString(PROPERTY_UPDATE_TIME, "");
        //Log.e(TAG, "lastUpdateTime is..." + lastUpdateTime);
        if (lastUpdateTime == null || lastUpdateTime.isEmpty()){
            return null;
        }
        Date updateTime = DateUtils.stringToDateTime(lastUpdateTime);
        return updateTime;
    }

    private static SharedPreferences getVersionPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }


}
