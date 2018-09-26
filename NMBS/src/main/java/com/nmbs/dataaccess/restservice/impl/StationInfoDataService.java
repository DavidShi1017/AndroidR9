package com.nmbs.dataaccess.restservice.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;


import com.nmbs.R;
import com.nmbs.activity.SettingsActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.converters.StationInfoResponseConverter;
import com.nmbs.dataaccess.database.MessageDatabaseService;
import com.nmbs.dataaccess.restservice.IStationInfoDataService;
import com.nmbs.log.LogUtils;
import com.nmbs.model.MobileMessage;
import com.nmbs.model.StationInfo;
import com.nmbs.model.StationInfoResponse;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.FileConstant;
import com.nmbs.util.FileManager;
import com.nmbs.util.HTTPRestServiceCaller;
import com.nmbs.util.HttpRetriever;
import com.nmbs.util.SharedPreferencesUtils;
import com.nmbs.util.Utils;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 1/25/16.
 */
public class StationInfoDataService implements IStationInfoDataService {
    public final static String LAST_MODIFIED_TIME = "last_modified_station_info_time";
    public final static String SERVER_LAST_MODIFIED_TIME = "server_last_modified_station_info_time";
    HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
    StationInfoResponseConverter stationInfoResponseConverter = new StationInfoResponseConverter();
    private static final String TAG = StationInfoDataService.class.getSimpleName();

    public StationInfoResponse getStationInfoResponse(Context context, String language, boolean isChangeLanguage) throws Exception {
        //Log.e("StationInfo", "start station info......");
        String stringHttpResponse = null;
        StationInfoResponse stationInfoResponse = null;
        String urlString = context.getString(R.string.server_url_get_stationinfo);

        String lastModifiedInPreferences = readLastModifiedTime(context);
        if (isChangeLanguage) {
            lastModifiedInPreferences = "";
        }
        //lastModifiedInPreferences = "";
        stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(context,
                null, urlString, language, HTTPRestServiceCaller.HTTP_GET_METHOD, 6000, true,
                lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_7);
        if (!"304".equalsIgnoreCase(stringHttpResponse)) {
            Map<String, String> lastModified = httpRestServiceCaller.getLastModified();

            stationInfoResponse = stationInfoResponseConverter.parseStationInfo(stringHttpResponse);
            if (stationInfoResponse != null && stationInfoResponse.getStations() != null && stationInfoResponse.getStations().size() > 0){
                if (lastModified != null) {
                    saveLastModifiedTime(context, lastModified.get(urlString));
                }
                FileManager.getInstance().createExternalStoragePrivateFileFromString(context, FileManager.FOLDER_FILE, FileConstant.FILE_STATION_INFO, stringHttpResponse);
                downloadPdf(context, stationInfoResponse, true, language);

            }else{
                stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, FileManager.FOLDER_FILE, FileConstant.FILE_STATION_INFO);
                stationInfoResponse = stationInfoResponseConverter.parseStationInfo(stringHttpResponse);
                if(stationInfoResponse == null){
                    stationInfoResponse = storeStationInfo(context, language);
                }
            }

        }else{
            stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, FileManager.FOLDER_FILE, FileConstant.FILE_STATION_INFO);
            stationInfoResponse = stationInfoResponseConverter.parseStationInfo(stringHttpResponse);
            if(stationInfoResponse == null){
                stationInfoResponse = storeStationInfo(context, language);
            }
            //downloadPdf(context, stationInfoResponse);
        }
        return stationInfoResponse;
    }

    public StationInfoResponse storeStationInfo( Context context, String language) throws Exception {

        //Log.e("StationInfo", "storeStationInfo....");
        int resourcesId = 0;
        if (SettingService.LANGUAGE_EN.contains(language)) {
            resourcesId = R.raw.stationinfo_en;
        }else if(SettingService.LANGUAGE_FR.contains(language)){
            resourcesId = R.raw.stationinfo_fr;
        }else if(SettingService.LANGUAGE_NL.contains(language)){
            resourcesId = R.raw.stationinfo_nl;
        }else if(SettingService.LANGUAGE_DE.contains(language)){
            resourcesId = R.raw.stationinfo_de;
        }else {
            resourcesId = R.raw.stationinfo_en;
        }

        InputStream is = context.getResources().openRawResource(resourcesId);
        String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
        //Log.e("StationInfo", "stringHttpResponse...." + stringHttpResponse);

        FileManager.getInstance().createExternalStoragePrivateFileFromString(context, FileManager.FOLDER_FILE, FileConstant.FILE_STATION_INFO, stringHttpResponse);
        StationInfoResponse stationInfoResponse = stationInfoResponseConverter.parseStationInfo(stringHttpResponse);
        downloadPdf(context, stationInfoResponse, true, language);
        return stationInfoResponse;
    }

    public StationInfoResponse getStationInfoResponseInLocal(Context context)throws Exception {
        String stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, FileManager.FOLDER_FILE, FileConstant.FILE_STATION_INFO);
        StationInfoResponse stationInfoResponse = stationInfoResponseConverter.parseStationInfo(stringHttpResponse);
        downloadPdf(context, stationInfoResponse, true, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
        return stationInfoResponse;
    }

    private boolean download(Context context, String url, String dossierId, String fileName){
        boolean isFinished = false;
        InputStream inputStream = null;
        try {
            inputStream = HttpRetriever.getInstance().retrieveStream(url);
            //InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierId, inputStream));
            //InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierId, inputStream));
							/*httpDownloader.downloadNetworkFile(urlString, FileManager.getInstance().getFilePath("/pdfpath/"),fileName + ".pdf");  */
            //Log.d("StationInfo", "The folder name is......" + dossierId);
            //Log.d("StationInfo", "The File name is......" + fileName);
            FileManager.getInstance().createExternalStoragePrivateFile(context, inputStream, "StationFloor", fileName);
            isFinished = true;
        } catch (Exception e) {
            isFinished = false;
            e.printStackTrace();
        }
        //Log.e("StationInfo", "isFinished......" + isFinished);
        return isFinished;
    }

    private void deleteNoLongerFiles(Context context, List<String> pdfs) throws Exception {
        File f = FileManager.getInstance().getFileDirectory(context, "StationFloor", "StationFloor");
        //Log.d("StationInfo", "deleteNoLongerFiles......" + f);
        if(f != null && f.isDirectory()){
            File [] files = f.listFiles();
            //Log.d("StationInfo", "deleteNoLongerFiles..files size is...." + files.length);
            if(files != null){
                for(File file : files){
                    boolean isNeedDelete = true;
                    //Log.d("StationInfo", "deleteNoLongerFiles..file.getName()...." + file.getName());
                    for(String pdf : pdfs){
                        if(pdf != null && !pdf.isEmpty()){
                            int index = pdf.lastIndexOf(".");
                            pdf = pdf.substring(0, index);
                            String fileName = Utils.sha1(pdf) + ".pdf";

                            //Log.d("StationInfo", "deleteNoLongerFiles..fileName...." + fileName);
                            if (StringUtils.equalsIgnoreCase(file.getName(), fileName)) {
                                isNeedDelete = false;
                            }
                        }
                    }
                    if (isNeedDelete){
                        //Log.d("StationInfo", "deleteNoLongerFiles...Delete file..." + file.getName());
                        FileManager.getInstance().deleteExternalStoragePrivateFile(context, "StationFloor", file.getName());
                    }
                }
            }
        }

    }

   private boolean downloadPdf(final Context context, final StationInfoResponse stationInfoResponse, final boolean isStore, String language) throws Exception {

       boolean isSuccessfully = false;
       List<String> pdfs = new ArrayList<>();
       final List<String> codes = new ArrayList<>();
       final String lang = language.substring(0,2);

       if(stationInfoResponse != null){
           for(StationInfo stationInfo : stationInfoResponse.getStations()){
               if(stationInfo != null){
                   pdfs.add(stationInfo.getFloorPlanDownloadURL());
                   codes.add(stationInfo.getCode());
               }
           }
       }
       deleteNoLongerFiles(context, pdfs);
        for (int i = 0; i < pdfs.size(); i++) {
            String pdf = pdfs.get(i);
            if(pdf != null && !pdf.isEmpty()){

                final String pdfUrl = pdf;

                int index = pdf.lastIndexOf(".");
                if(pdf.length() > 0){
                    pdf = pdf.substring(0, index);
                }
                final String fileName = Utils.sha1(pdf) + ".pdf";
                boolean hasThisFile = FileManager.getInstance().hasExternalStoragePrivateFile(context, "StationFloor", fileName);
                //Log.e("StationInfo", "hasThisFile......" + hasThisFile );
                //LogUtils.e("StationInfo", "Starting Pdf file name......" + fileName );
                //Log.e("StationInfo", "Starting Pdf file urlString......" + pdfUrl );
                //Log.e("StationInfo", "isStore......" + isStore );

                final String code = codes.get(i);
                if (!hasThisFile) {
                    new Thread() {
                        public void run() {
                            if(isStore){
                                InputStream inputStream = null;
                                try {
                                    inputStream = context.getResources().getAssets().open(code + "_" + lang + ".pdf");
                                    LogUtils.e("StationInfo", "Starting Pdf file name......" + lang );
                                    //Log.e("StationInfo", "inputStream......" + inputStream );
                                    //Log.e("StationInfo", "inputStream......" + (code + "_" + lang + ".pdf") );
                                } catch (IOException e) {
                                    //Log.e("StationInfo", "IOException......" + (code + "_" + lang + ".pdf") );
                                    e.printStackTrace();
                                }
                                FileManager.getInstance().createExternalStoragePrivateFile(context, inputStream, "StationFloor", fileName);
                            }else{
                                boolean isFinished = download(context, pdfUrl, "StationFloor", fileName);
                                if (!isFinished){
                                    FileManager.getInstance().deleteExternalStoragePrivateFile(context, "StationFloor", fileName);
                                }
                                //Log.d("StationInfo", "isFinished......" + isFinished );
                            }


                        }
                    }.start();
                }
            }
        }
        return isSuccessfully;
    }

    public File getStationPdf(Context context, StationInfo stationInfo){
        //Log.d("StationInfo", "getStationPdf...stationInfo..." + stationInfo.getName());
        File file = null;
        if(stationInfo != null){
            String pdf = stationInfo.getFloorPlanDownloadURL();
            //Log.d("StationInfo", "getStationPdf...pdf..." + pdf);
            int index = pdf.lastIndexOf(".");
            if(pdf.length() > 0){
                pdf = pdf.substring(0, index);
            }
            String fileName = null;
            try {
                fileName = Utils.sha1(pdf) + ".pdf";
            } catch (Exception e) {
                e.printStackTrace();
            }
            file = FileManager.getInstance().getExternalStoragePrivateFile(context, "StationFloor", fileName);
        }
       return file;
    }

    private String readLastModifiedTime(Context context){

        SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);

        String lastModified = settings.getString(SERVER_LAST_MODIFIED_TIME, "").toString();
        if (lastModified.isEmpty()) {
            lastModified = context.getResources().getString(R.string.masterdata_last_modify);
            lastModified = cutLastModified(lastModified);

        }
        return lastModified;
    }
    private String cutLastModified(String lastModified){
        if (lastModified != null && lastModified.indexOf(':') != -1) {
            lastModified = lastModified.substring(lastModified.indexOf(':') + 2);
        }
        return lastModified;
    }

    private void saveLastModifiedTime(Context context, String lastModified){
        if (lastModified != null && lastModified.indexOf(':') != -1) {
            lastModified = lastModified.substring(lastModified.indexOf(':') + 2);
        }

        SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
        if(!"".equals(lastModified)||!"".equals(lastModified.trim()))
            settings.edit().putString(SERVER_LAST_MODIFIED_TIME, lastModified.trim()).commit();

    }

    public void cleanLastModifiedTime(Context context){

        //Log.d("Station" ,"cleanLastModifiedTime......... "  );
        SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
        //settings.getAll().clear();
        settings.edit().clear().commit();

    }

    public void storeDefaultData(Context context){
        String sharedLocale = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_LANGUAGE, context);

        storeMessageData(context, sharedLocale);

    }

    private void storeMessageData(Context context, String language){
        int resourcesId = getMessageResource(language);
        InputStream is = context.getResources().openRawResource(resourcesId);
        String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
        //Log.e(TAG, "storeMessageData....");
        StationInfoResponse stationInfoResponse = null;
        try {
            stationInfoResponse = stationInfoResponseConverter.parseStationInfo(stringHttpResponse);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (stationInfoResponse != null) {
            MessageDatabaseService databaseService = new MessageDatabaseService(context);
            List<MobileMessage> messages = databaseService.readMessageList();
            if (messages == null || messages.size() == 0) {
                //Log.e(TAG, "insertMessageData....");
                //databaseService.insertMessageList(messageResponse.getMobileMessages());
            }

        }
    }

    private int getMessageResource(String language){
        int resourcesId = 0;
		/*if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.message_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.message_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.message_nl;
		}else {
			resourcesId = R.raw.clicktocall_de;
		}*/
        return resourcesId;
    }

    public File getStationFloorPlan(Context context, String stationCode, String language){
        //Log.d("StationInfo", "getStationFloorPlan......");
        File file = null;
        AssetManager am = context.getAssets();
        language = language.substring(0, 2).toLowerCase();
        String existStationCode = getExistStationCode(stationCode);
        InputStream is = null;
        String fileName = existStationCode + "_" + language;
        LogUtils.e("StationInfo", "getStationFloorPlan...fileName..." + fileName);
        try {
            is = am.open(fileName + ".pdf");
            FileManager.getInstance().createExternalStoragePrivateFile(context, is, stationCode, stationCode + FileManager.POSTFIX_PDF);
            file = FileManager.getInstance().getExternalStoragePrivateFile(context, stationCode, stationCode + FileManager.POSTFIX_PDF);
           // Log.d("StationInfo", "getStationFloorPlan...file..." + file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
    public String getExistStationCode(String stationCode){
        if ("BEBMI".equalsIgnoreCase(stationCode)) {
            return stationCode;
        }else if ("NLASC".equalsIgnoreCase(stationCode)) {
            return stationCode;
        }else if ("DEKOH".equalsIgnoreCase(stationCode)) {
            return stationCode;
        }else if ("FRPNO".equalsIgnoreCase(stationCode)) {
            return stationCode;
        }else if ("GBSPX".equalsIgnoreCase(stationCode)) {
            return stationCode;
        }
        return null;
    }

}
