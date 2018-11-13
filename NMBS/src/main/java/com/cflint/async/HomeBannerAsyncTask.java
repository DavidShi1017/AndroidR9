package com.cflint.async;

import android.content.Context;
import android.os.AsyncTask;


import com.cflint.dataaccess.restservice.IMasterDataService;
import com.cflint.dataaccess.restservice.impl.MasterDataService;
import com.cflint.exceptions.ConnectionError;
import com.cflint.handler.RestMasterDataHandler;
import com.cflint.model.HomeBannerResponse;
import com.cflint.util.FileManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeBannerAsyncTask extends AsyncTask<Void, Void, Void> {

    private String languageBeforSetting;
    public static boolean isMessageFinished;
    private Context mContext;
    private String dnr;
    public static boolean isRealTimeFinished = true;
    private boolean isHasError;
    public static final String ERROR_COUNT = "ErrorCount";
    public static final String SERVICE_STATE_KEY = "RealTimeFinished";
    public static String REFRESHING_DNR;
    public static boolean isRefreshingOneDnr;
    private static final String TAG = HomeBannerAsyncTask.class.getSimpleName();
    private IMasterDataService masterDataService = new MasterDataService();


    private boolean serviceStatus;

    public HomeBannerAsyncTask(String languageBeforSetting, Context mContext) {

        this.languageBeforSetting = languageBeforSetting;
        this.mContext = mContext;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        downloadHomeBanner();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }


    public boolean downloadHomeBanner() {

        try {
            HomeBannerResponse homeBannerResponse = masterDataService.executeHomeBanner(mContext, languageBeforSetting);
            if(homeBannerResponse == null){
                //FileManager.getInstance().deleteExternalStoragePrivateFile(mContext, FileManager.FOLDER_HOMEBANNER, FileManager.FILE_HOMEBANNER);
                IMasterDataService masterDataService = new MasterDataService();
                masterDataService.cleanLastModifiedHomeBanner(mContext);
            }
            serviceStatus = true;
            //Log.d(TAG, "downloadHomeBanner is SUCCEED.....");
        } catch (ConnectionError e) {
            //Log.d("HomeBanner", "HomeBanner 404 the data will be deleted....");
            //FileManager.getInstance().deleteExternalStoragePrivateFile(mContext, FileManager.FOLDER_HOMEBANNER, FileManager.FILE_HOMEBANNER);
            masterDataService.cleanLastModifiedHomeBanner(mContext);
            serviceStatus = true;
            //Log.e(TAG, "downloadHomeBanner is FAILED.....");
        } catch (Exception e) {
            //FileManager.getInstance().deleteExternalStoragePrivateFile(mContext, FileManager.FOLDER_HOMEBANNER, FileManager.FILE_HOMEBANNER);
            masterDataService.cleanLastModifiedHomeBanner(mContext);
            serviceStatus = true;
            //Log.e(TAG, "downloadHomeBanner is FAILED.....");
            e.printStackTrace();
        }
        return serviceStatus;
    }
}
