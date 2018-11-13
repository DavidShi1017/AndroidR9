package com.cflint.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;


import com.cflint.application.NMBSApplication;

import com.cflint.services.impl.DossiersUpToDateService;
import com.cflint.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class DossierUpToDateAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = DossierUpToDateAsyncTask.class.getSimpleName();


    private Context mContext;
    public DossierUpToDateAsyncTask(Context mContext) {

        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Log.d("UpToDate", "DossierUpToDateAsyncTask...doInBackground");
        if(NetworkUtil.isWifiConnected(mContext)){
            DossiersUpToDateService dossiersUpToDateService = NMBSApplication.getInstance().getDossiersUpToDateService();
            dossiersUpToDateService.getDossiersUpToDateResponse();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}
