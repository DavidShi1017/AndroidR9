package com.cfl.async;

import android.content.Context;
import android.os.AsyncTask;


import com.cfl.application.NMBSApplication;
import com.cfl.dataaccess.restservice.IMasterDataService;
import com.cfl.dataaccess.restservice.impl.MasterDataService;

public class GengeralSettingAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = GengeralSettingAsyncTask.class.getSimpleName();
    private String languageBeforSetting;

    private Context mContext;


    private IMasterDataService masterDataService = new MasterDataService();


    public GengeralSettingAsyncTask(String languageBeforSetting, Context mContext) {

        this.languageBeforSetting = languageBeforSetting;
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        downloadGeneralSetting();
        new Thread() {
            public void run() {
                NMBSApplication.getInstance().getDossierDetailsService().deletePastTicket(NMBSApplication.getInstance().getAssistantService().getGeneralSetting());
            }
        }.start();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }


    public void downloadGeneralSetting() {
        try {
            //Log.d(TAG,"downloadGeneralSetting is working.....GeneralSetting");
            masterDataService.executeGeneralSetting(mContext, languageBeforSetting);
            //Log.d(TAG,"downloadGeneralSetting is SUCCEED.....");
        } catch (Exception e) {
            //Log.e(TAG,"downloadGeneralSetting is FAILED.....");
            //To do nothing!!!!!!!!
            //catchError(NetworkError.TIMEOUT, e);
        }
    }
}
