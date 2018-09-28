package com.nmbs.services.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.nmbs.R;
import com.nmbs.activities.PDFViewActivity;
import com.nmbs.adapter.PDFsAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AutoRetrievalDossiersTask;
import com.nmbs.async.CreateAllSubScriptionAsyncTask;
import com.nmbs.async.DeleteAllSubScriptionAsyncTask;
import com.nmbs.dataaccess.converters.DossierDetailConverter;
import com.nmbs.dataaccess.converters.RealTimeConnectionConverter;
import com.nmbs.dataaccess.converters.RealTimeTravelSegmentConverter;
import com.nmbs.dataaccess.database.AssistantDatabaseService;
import com.nmbs.dataaccess.database.DossierDatabaseService;
import com.nmbs.dataaccess.database.RealTimeInfoDatabaseService;
import com.nmbs.dataaccess.database.TrainIconsDatabaseService;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.model.Connection;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossierTravelSegment;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.LogonInfo;
import com.nmbs.model.Order;
import com.nmbs.model.PDF;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.model.RealTimeInfoResponse;
import com.nmbs.model.RealTimeInfoTravelSegment;
import com.nmbs.model.StationInfo;
import com.nmbs.model.StationInfoResponse;
import com.nmbs.model.StationIsVirtualText;
import com.nmbs.model.Subscription;
import com.nmbs.model.TrainIcon;
import com.nmbs.services.IPushService;
import com.nmbs.services.ISettingService;
import com.nmbs.util.AESUtils;
import com.nmbs.util.ComparatorDossierDate;
import com.nmbs.util.ComparatorTravelSegmentDate;
import com.nmbs.util.DateUtils;
import com.nmbs.util.FileManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * Created by shig on 2016/4/12.
 */
public class DossierDetailsService {

    private Context context;
    private final static String TAG = DossierDetailsService.class.getSimpleName();
    public final static String DossierStateError = "ERROR";
    public final static String DossierStateInProgress = "PROVISIONAL";
    public final static String DossierStateConfirmed = "CONFIRMED";
    public static final String class1 = "1st Class";
    public static final String class2 = "2nd Class";
    public static final String SegmentType_MARKETPRICE = "MARKETPRICE";
    public static final String SegmentType_Reservation = "Reservation";
    public static final String SegmentType_Admission = "Admission";
    public static final String SegmentType_OtherAdmissionTicket = "OtherAdmissionTicket";
    public static final String Dossier_Realtime_Connection = "CONNECTION";
    public static final String Dossier_Realtime_Segment = "TRAVELSEGMENT";

    public static final int Dossier_Active = 0;
    public static final int Dossier_NoActive = 1;
    public static final int Dossier_Failed = 2;
    public static final int Dossier_Nothing = 3;

    private Dossier currentDossier;
    private DossierSummary currentDossierSummary;
    private DossierTravelSegment currentDossierTravelSegment;


    private File decryptFile;
    public DossierDetailsService(Context context) {
        this.context = context;
    }

    public List<DossierSummary> getDossiers(){
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        List<DossierSummary> dossiers = dossierDatabaseService.selectDossierAll();
        /*if(dossiers != null && dossiers.size() > 0){
            Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.ASC);
            Collections.sort(dossiers, comp);
        }*/
        return dossiers;
    }

    public DossierSummary getDossier(String id){
        //Log.d(TAG, "Dossier id is===" + id);
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        DossierSummary dossierSummary = dossierDatabaseService.selectDossier(id.toUpperCase());
        return dossierSummary;
    }

    public List<DossierSummary> getDossiers(boolean isActive){
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        //List<DossierSummary> dossiersAll = dossierDatabaseService.selectDossierAll();
        List<DossierSummary> dossiersActive = dossierDatabaseService.selectDossierActive(true);
        List<DossierSummary> dossiersInActive = dossierDatabaseService.selectDossierActive(false);
        /*if(dossiersAll != null && dossiersAll.size() > 0){
            for(DossierSummary dossierSummary : dossiersAll){
                if(dossierSummary != null){

                    if(DateUtils.getDate(context, dossierSummary.getLatestTravelDate()).after(DateUtils.getDate(context, new Date()))){
                        dossiersActive.add(dossierSummary);
                        Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.ASC);
                        Collections.sort(dossiersActive, comp);
                    }else {
                        dossiersInActive.add(dossierSummary);
                        Comparator<DossierSummary> comp = new ComparatorDossierDate(ComparatorDossierDate.DESC);
                        Collections.sort(dossiersInActive, comp);
                    }
                    Log.d(TAG, "dossiersActive LatestTravelDate is..." + dossierSummary.getLatestTravelDate());
                }
            }
        }*/
        if(isActive){
            if(dossiersActive != null){
                //Log.d(TAG, "dossiersActive count is..." + dossiersActive.size());
            }else{
                //Log.d(TAG, "dossiersActive is null...");
            }

            return dossiersActive;
        }else{
            if(dossiersInActive != null){
                //Log.d(TAG, "dossiersInActive count is..." + dossiersInActive.size());
            }else{
                //Log.d(TAG, "dossiersInActive is null...");
            }
            return dossiersInActive;
        }
    }

    public List<DossierTravelSegment> getActiveTravelSegment(boolean isHome, List<DossierTravelSegment> dossierTravelSegments){
        Comparator<DossierTravelSegment> comp = new ComparatorTravelSegmentDate(dossierTravelSegments);
        Collections.sort(dossierTravelSegments, comp);
        List<DossierTravelSegment> newDossierTravelSegments = new ArrayList<>();
        if(isHome){
            int i = 0;
            Date earliest = null;
            Date now = new Date();
            String nowString = DateUtils.dateTimeToString(now);
            now = DateUtils.stringToDate(nowString);
            for(DossierTravelSegment dossierTravelSegment : dossierTravelSegments){
                if(dossierTravelSegment != null){
                    if(i == 0 && (dossierTravelSegment.getDepartureDate().after(now) || dossierTravelSegment.getDepartureDate().equals(now))){
                        earliest = dossierTravelSegment.getDepartureDate();
                        i++;
                        //Log.e(TAG, "earliest date is..." + earliest);
                    }
                    //Log.e(TAG, "getDepartureDate date is..." + dossierTravelSegment.getDepartureDate());
                    if(dossierTravelSegment.getDepartureDate().equals(earliest)){
                        //Log.e(TAG, "Added DepartureDate date is..." + dossierTravelSegment.getDepartureDate());
                        newDossierTravelSegments.add(dossierTravelSegment);
                    }
                }
            }
        }
        Comparator<DossierTravelSegment> compDossierTravelSegment = new ComparatorTravelSegmentDate(newDossierTravelSegments);
        Collections.sort(newDossierTravelSegments, compDossierTravelSegment);
        return newDossierTravelSegments;
    }


    public List<DossierTravelSegment> getDossierTravelSegment(){
        List<DossierSummary> dossiersActive = getDossiers(true);
        //Log.e(TAG, "dossiersActive count..." + dossiersActive.size());
        List<DossierTravelSegment> dossierTravelSegments = new ArrayList<>();
        if(dossiersActive != null && dossiersActive.size() > 0){
            for(int i = 0; i < dossiersActive.size(); i++){
                DossierSummary dossierSummary = dossiersActive.get(i);
                if(dossierSummary != null){
                    //Log.e(TAG, "dossiersActive ..." + dossierSummary.getDossierId());
                    DossierDetailsResponse dossierResponse = getDossierDetail(dossierSummary);
                    if(dossierResponse != null && dossierResponse.getDossier() != null){
                        for(DossierTravelSegment dossierTravelSegment : dossierResponse.getDossier().getTravelSegments()){
                            dossierTravelSegments.add(dossierTravelSegment);
                        }
                    }
                }
            }
        }
        //Log.e(TAG, "dossiersActive ..." + dossierTravelSegments.size());
        return dossierTravelSegments;
    }

    public void addDossier(DossierSummary dossierSummary){
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        dossierDatabaseService.insertDossier(dossierSummary);
    }
    public void removeDossier(DossierSummary dossierSummary){
        //Log.e("removeDossier", "removeDossier...");
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        dossierDatabaseService.deleteDossier(dossierSummary.getDossierId());
    }

    public void updateDossier(DossierSummary dossierSummary){
        removeDossier(dossierSummary);
        addDossier(dossierSummary);
    }

    public DossierDetailsResponse getDossierDetail(DossierSummary dossierSummary){
        DossierDetailsResponse dossierResponse = null;
        DossierDetailConverter dossierDetailConverter = new DossierDetailConverter();
        try {
            dossierResponse = dossierDetailConverter.parsesDossierDetailResponse(dossierSummary.getDossierDetails());
            //Log.e(TAG, "dossierResponse...." + dossierResponse);
        } catch (Exception e) {
            //Log.e(TAG, "dossierResponse exception return null....");
            e.printStackTrace();
            return null;
        }
        return  dossierResponse;
    }

    public String comfortClass(int comfortClass){
        if(comfortClass == 1){
            return context.getString(R.string.general_class_1st);
        }else{
            return context.getString(R.string.general_class_2nd);
        }
    }
    public static String getPassengeText(Context context, String passengeName, String coachNumber, String seatNumberInfo){
        String text = "";
        if(coachNumber != null && !coachNumber.isEmpty()){
            text = passengeName + ": "
                    + context.getResources().getString(R.string.general_carriage) + " " + coachNumber + " - "
                    + context.getResources().getString(R.string.general_place) + " " + seatNumberInfo;
        }else{
            text = passengeName;
        }
        return text;
    }

    public String getPassengerTypeText(Context context, String type){
        if ("KID0".equals(type) || "0".equals(type)){
            return context.getResources().getString(R.string.general_child_0_3);
        }else if("KID4".equals(type) || "4".equals(type)){
            return context.getResources().getString(R.string.general_child_4_5);
        }else if ("KID6".equals(type) || "6".equals(type)) {
            return context.getResources().getString(R.string.general_child_6_11);
        }else if ("KID12".equals(type) || "12".equals(type)){
            return context.getResources().getString(R.string.general_youth_12_14);
        }else if("KID15".equals(type)  || "16".equals(type)){
            return context.getResources().getString(R.string.general_youth_15_17);
        }else if ("YOUTH".equals(type) || "Y".equals(type)) {
            return context.getResources().getString(R.string.general_youth_15_25);
        }else if ("ADULT".equals(type) || "A".equals(type)){
            return context.getResources().getString(R.string.general_adult_26_59);
        }else if("SENIOR".equals(type) || "S".equals(type)){
            return context.getResources().getString(R.string.general_senior_60);
        }else {
            return context.getResources().getString(R.string.general_adult_26_59);
        }
    }

    public void openPDF(final String dnr, final String pdfId) {
        //String[] params = { dnr, pdfId };
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                //String dnr = params[0];
                //String pdfId = params[1];
                //String fileName = "";
                String decryptFileName = "";
                String fileName = dnr + "-" + pdfId + ".pdf";
                decryptFileName = dnr + "-" + pdfId + "-" +"decrypt"+ ".pdf";
                File file = FileManager.getInstance().getExternalStoragePrivateFile(context, dnr, "." + fileName);
                //Log.e(TAG, "openPDF------->");
                if(file != null && file.exists()){
                    //Log.e(TAG, "openPDF---exists---->" + fileName);
                    openPdf(file);
                }
                /*//InputStream encrypttIs = null;
                InputStream decryptInputStream = null;
               // InputStream encryptInputStream = null;
                try {
                    InputStream encrypttIs = new FileInputStream(file);
                    try{
                        decryptInputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(dnr, encrypttIs));
                    } catch(Exception e){
                        if(file.exists()){
                            encrypttIs = new FileInputStream(file);
                        }
                        try{
                            InputStream encryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dnr,encrypttIs));
                            file.delete();
                            FileManager.getInstance().createExternalStoragePrivateFile(context, encryptInputStream, dnr, fileName);
                            openPDF(dnr, pdfId);
                        }catch (Exception ee){

                        }
                    }
                    startAdobleToReadPdf(dnr, pdfId, decryptFileName, decryptInputStream);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }*/
                return null;
            }
            protected void onPostExecute(Void result){}
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void startAdobleToReadPdf(String dnr, String pdfId, String decryptFileName,
                                     InputStream afterEncryptInputStream){
        /*InputStream inputStream = null ;
        if(afterEncryptInputStream == null){
            String fileName = dnr + "-" + pdfId + ".pdf";
            File file = FileManager.getInstance().getExternalStoragePrivateFile(context, dnr, fileName);
            try {
                inputStream = new ByteArrayInputStream(AESUtils.decryptPdfOrBarcode(dnr,new FileInputStream(file)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            inputStream = afterEncryptInputStream;
        }

        FileManager.getInstance().createExternalStoragePrivateFile(context, inputStream, dnr, decryptFileName);
        decryptFile = FileManager.getInstance().getExternalStoragePrivateFile(context, dnr, decryptFileName);
        openPdf(decryptFile);*/
    }

    private void openPdf(File file){
        if (file.exists()) {
            /*Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
            context.startActivity(PDFViewActivity.createIntent(context, file, "", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
            try {
                //context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "ActivityNotFoundException, Open PDF Failed", e);
            }
        } else {
            //Toast.makeText(applicationContext, applicationContext.getString(R.string.alert_status_service_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteUndecryptPdfFile(String dossierId, String pdfId){
        String decryptFileName = dossierId + "-" + pdfId + "-" + "decrypt" + ".pdf";
        FileManager.getInstance().deleteExternalStoragePrivateFile(context, dossierId, decryptFileName);
    }

    public List<PDF> getPdfs(Activity activity, Dossier dossier, List<PDF> pdfsInDossier){
        List<PDF> pdfs = new ArrayList<>();
        for(int i = 0;i < pdfsInDossier.size(); i++){
            String fileName = dossier.getDossierId() + "-" + pdfsInDossier.get(i).getPdfId() + ".pdf";
            boolean has = FileManager.getInstance().hasExternalStoragePrivateFile(activity.getApplicationContext(), dossier.getDossierId(), "." + fileName);
            //Log.d("PDF", "has pdf...." + has);
            if(has){
                PDF pdf = dossier.getPdf(pdfsInDossier.get(i).getPdfId());
                if (pdfs.size() > 0){
                    for (PDF pdfInList : pdfs){
                        if(pdfInList != null && pdf != null && !pdfInList.getCreationTimeStamp().equals(pdf.getCreationTimeStamp())){
                            pdfs.add(pdf);
                        }
                    }
                }else {
                    pdfs.add(pdf);
                }
            }
            //Log.d("PDF", "PDF count is...." + pdfs.size());
        }
        return pdfs;
    }

    public void showPDFDialog(Activity activity, final List<PDF> pdfs, final Dossier dossier){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dossier_detail_PDF_tickets));
        PDFsAdapter pdfAdapter = new PDFsAdapter(activity.getApplicationContext(), R.layout.li_pdf, pdfs);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int which) {
                if(pdfs != null && pdfs.size() > which){
                    String id = pdfs.get(which).getPdfId();
                    openPDF(dossier.getDossierId(), id);
                }
            }
        };
        builder.setAdapter(pdfAdapter, listener);
        dialog = builder.create();
        dialog.show();
    }

    public List<StationIsVirtualText> getStationsIsVirtualTextFromSegment(DossierTravelSegment dossierTravelSegment, List<StationIsVirtualText> stationIsVirtualTexts){

        if(dossierTravelSegment.getOriginStationIsVirtualText() != null && !dossierTravelSegment.getOriginStationIsVirtualText().isEmpty()){
            StationIsVirtualText originStationIsVirtualText = new StationIsVirtualText(dossierTravelSegment.getOriginStationRcode(),
                    dossierTravelSegment.getOriginStationName(), dossierTravelSegment.getOriginStationIsVirtualText());
            stationIsVirtualTexts.add(originStationIsVirtualText);
        }
        if(dossierTravelSegment.getDestinationStationIsVirtualText() != null && !dossierTravelSegment.getDestinationStationIsVirtualText().isEmpty()){
            StationIsVirtualText destinationStationIsVirtualText = new StationIsVirtualText(dossierTravelSegment.getDestinationStationRcode(),
                    dossierTravelSegment.getDestinationStationName(), dossierTravelSegment.getDestinationStationIsVirtualText());
            stationIsVirtualTexts.add(destinationStationIsVirtualText);
        }
        return stationIsVirtualTexts;
    }


    public List<StationInfo> getStationInfoFromDossier(StationInfoResponse stationInfoResponse, List<String> codes){
        List<StationInfo> stationInfos = new ArrayList<>();
        if(stationInfoResponse != null) {
            List<StationInfo> stationInfosInLocal = stationInfoResponse.getStations();
            for (StationInfo stationInfoInLocal : stationInfosInLocal) {
                for (String code : codes) {
                    if (stationInfoInLocal != null && stationInfoInLocal.getCode().equalsIgnoreCase(code)) {
                        stationInfos.add(stationInfoInLocal);
                    }
                }
            }
        }
        return stationInfos;
    }

    private void createRealTimeInfos(){
        List<DossierSummary> dossiersActive = getDossiers(true);
        Map<String, Object> objectMap = null;
        List<Object> objectList = null;
        Map<String, Object> realTimeMap = new HashMap<>();
        List<Object> realTimeList = new ArrayList<>();
        if(dossiersActive != null && dossiersActive.size() > 0){
            for(DossierSummary dossierSummary : dossiersActive){
                DossierDetailsResponse dossierResponse = getDossierDetail(dossierSummary);
                if(dossierResponse != null){
                    objectMap = getRealTimeInfoRequesMap(dossierResponse.getDossier(), NMBSApplication.getInstance().getMasterService().loadGeneralSetting());
                    objectList = getRealTimeInfoRequestList(objectMap);
                    realTimeMap.putAll(objectMap);
                    realTimeList.addAll(objectList);
                }
            }
        }

        realTimeInfoRequestParameter = new RealTimeInfoRequestParameter(realTimeList);
        realTimeInfoRequestParameter.setMap(realTimeMap);

    }

    private RealTimeInfoRequestParameter createRealTimeInfosForDossier(Dossier dossier){
        //Log.e("RealTime", "createRealTimeInfosForDossier......" + dossier.getDossierId());
        Map<String, Object> objectMap = null;
        List<Object> objectList = null;
        Map<String, Object> realTimeMap = new HashMap<>();
        List<Object> realTimeList = new ArrayList<>();
        if(dossier != null){
            objectMap = getRealTimeInfoRequesMap(dossier, NMBSApplication.getInstance().getMasterService().loadGeneralSetting());
            //Log.e("RealTime", "objectMap...key..." + objectMap);
            objectList = getRealTimeInfoRequestList(objectMap);
            if(objectMap != null && objectMap.size() > 0){
                realTimeMap.putAll(objectMap);
            }
            if(objectList != null && objectList.size() > 0){
                realTimeList.addAll(objectList);
            }

        }
        //Log.e("RealTime", "realTimeList...key..." + realTimeList);
        RealTimeInfoRequestParameter realTimeInfoRequestParameter = new RealTimeInfoRequestParameter(realTimeList);
        realTimeInfoRequestParameter.setMap(realTimeMap);
        return realTimeInfoRequestParameter;
    }

    public RealTimeInfoRequestParameter getRealTimeInfoRequestParameterForDossier(Dossier dossier) {
        //Log.e("RealTime", "getRealTimeInfoRequestParameterForDossier......" + dossier.getDossierId());
        RealTimeInfoRequestParameter realTimeInfoRequestParameter = null;

        realTimeInfoRequestParameter = createRealTimeInfosForDossier(dossier);
        return realTimeInfoRequestParameter;
    }

    public RealTimeInfoRequestParameter getRealTimeInfoRequestParameter() {
        if(realTimeInfoRequestParameter == null || realTimeInfoRequestParameter.getMap() == null || realTimeInfoRequestParameter.getMap().size() == 0){
            createRealTimeInfos();
        }

        return realTimeInfoRequestParameter;
    }

    public void cleanRealtimeInfoRequest(){
        realTimeInfoRequestParameter = null;
    }

    private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
    public Map<String, Object> getRealTimeInfoRequesMap(Dossier dossier, GeneralSetting generalSetting){
        DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
        Map<String, Object> realTimeInfoRequestMap = dossierDetailDataService.getRealTimeInfoRequestList(dossier, generalSetting);
        return realTimeInfoRequestMap;
    }

    public List<Object> getRealTimeInfoRequestList(Map<String, Object> map){
        DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
        List<Object> objects = new ArrayList<>();
        objects = dossierDetailDataService.getRealTimeInfoRequestList(map);
        return objects;
    }

    public boolean refreshRealTimeInfo(RealTimeInfoRequestParameter realTimeInfoRequestParameter, ISettingService settingService, Context context){
        //Log.d("RealTime", "refreshRealTimeInfo ...");
        DossierDetailDataService dossierDataService = new DossierDetailDataService();
        RealTimeInfoDatabaseService realTimeInfoDatabaseService = new RealTimeInfoDatabaseService(context);
        boolean isSuccess = false;
        //try{
            List<RealTimeInfoResponse> realTimeInfoResponses = dossierDataService.callRefreshTimeInfo(context, settingService.getCurrentLanguagesKey(),
                    realTimeInfoRequestParameter);
            //Log.d("RealTime", "realTimeInfoResponses count is..." + realTimeInfoResponses);
            if(realTimeInfoResponses != null && realTimeInfoResponses.size() > 0){
                //Log.d("RealTime", "realTimeInfoResponses count is..." + realTimeInfoResponses.size());
                for(RealTimeInfoResponse realTimeInfoResponse : realTimeInfoResponses){
                    if(realTimeInfoResponse != null){
                        //Log.d("RealTime", "realTimeInfoResponses id is..." + realTimeInfoResponse.getId() + "...is Success???" + realTimeInfoResponse.getIsSuccess());
                        if(realTimeInfoResponse.getIsSuccess()){
                            realTimeInfoDatabaseService.deleteRealTime(realTimeInfoResponse.getId());
                            realTimeInfoDatabaseService.insertRealTime(realTimeInfoResponse);
                        }else{
                            RealTimeInfoResponse realTimeInfoResponseInLocal  = realTimeInfoDatabaseService.readRealTimeInfoById(realTimeInfoResponse.getId());
                            if(realTimeInfoResponseInLocal != null){
                                realTimeInfoDatabaseService.deleteRealTime(realTimeInfoResponse.getId());
                                realTimeInfoDatabaseService.insertRealTime(realTimeInfoResponseInLocal);
                            }else{
                                realTimeInfoDatabaseService.insertRealTime(realTimeInfoResponse);
                            }
                        }
                    }
                }
            }

            isSuccess = true;
        //}catch (Exception e){
        ////    e.printStackTrace();
       //     return false;
        //}

        return isSuccess;
    }

    public List<RealTimeInfoResponse> readAllRealTimeInfo(Context context){
        RealTimeInfoDatabaseService realTimeInfoDatabaseService = new RealTimeInfoDatabaseService(context);
        return realTimeInfoDatabaseService.readAllRealTimeInfo();
    }

    public RealTimeInfoResponse readRealTimeInfoById(String id, Context context){
        RealTimeInfoDatabaseService realTimeInfoDatabaseService = new RealTimeInfoDatabaseService(context);
        return realTimeInfoDatabaseService.readRealTimeInfoById(id);
    }
    public Object getRealTime(RealTimeInfoResponse realTimeInfoResponse){
        if(realTimeInfoResponse != null){
            if(Dossier_Realtime_Connection.equalsIgnoreCase(realTimeInfoResponse.getRealTimeType())){
                RealTimeConnectionConverter converter = new RealTimeConnectionConverter();
                RealTimeConnection connection = null;
                try {
                    //Log.d(TAG, "real time content::::" + realTimeInfoResponse.getContent());
                    connection = converter.parseRealTimeConnection(realTimeInfoResponse.getContent());
                } catch (Exception e) {
                    return null;
                }
                return connection;
            }else{
                RealTimeTravelSegmentConverter converter = new RealTimeTravelSegmentConverter();
                RealTimeInfoTravelSegment realTimeInfoTravelSegment = null;
                try {
                    realTimeInfoTravelSegment =  converter.parseTravelSegment(realTimeInfoResponse.getContent());
                } catch (Exception e) {
                    return null;
                }
                return realTimeInfoTravelSegment;
            }
        }
        return null;
    }

    public boolean shouldRefresh(RealTimeInfoRequestParameter realTimeInfoRequestParameter, DossierTravelSegment dossierTravelSegment, Connection connection){
        /*Log.e("RealTime", "shouldRefresh...key..." + realTimeInfoRequestParameter);
        Log.e("RealTime", "shouldRefresh...key..." + realTimeInfoRequestParameter.getMap());
        Log.e("RealTime", "shouldRefresh...key..." + realTimeInfoRequestParameter.getMap().size());*/
        if(realTimeInfoRequestParameter != null && realTimeInfoRequestParameter.getMap() != null){
            Iterator it = realTimeInfoRequestParameter.getMap().keySet().iterator();
            while(it.hasNext()) {
                String key = it.next().toString();
                //Log.e("LinedAdapter", "shouldRefresh...key..." + key);
                if(dossierTravelSegment != null){
                    if(key.equalsIgnoreCase(dossierTravelSegment.getTravelSegmentId())){
                        return true;
                    }

                }
                if(connection != null){
                    if(key.equalsIgnoreCase(connection.getConnectionId())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void autoEnableSubscription(DossierSummary dossierSummary, Dossier dossier, Handler handler,String language){
        List<Connection> createSubscriptionByConnections = new ArrayList<>();
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        if(dossierSummary != null && dossierSummary.isDossierPushEnabled()){
            if(dossier != null) {
                for (Connection connection : dossier.getConnections()) {
                    if(connection != null){
                        if(connection.getArrival().after(new Date())) {
                            //The connections are created subscription.
                            if (!pushService.isSubscriptionExistLocal(connection.getReconCtx(), connection.getDeparture())) {
                                createSubscriptionByConnections.add(connection);
                            }
                        }
                    }
                }
            }
        }
        if(createSubscriptionByConnections != null && createSubscriptionByConnections.size() > 0){
            CreateAllSubScriptionAsyncTask asyncTask = new CreateAllSubScriptionAsyncTask(handler, dossier,
                    createSubscriptionByConnections, pushService, context,language);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            if(handler != null){
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }

        }
    }

    public boolean isConnectionArrivalPast(Connection connection){
        if(connection != null){
            if(connection.getArrival().after(new Date())){
                return false;
            }else{
                return true;
            }
        }
        return true;
    }

    public boolean isMonitoringActive(Connection connection){
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        if(connection != null){
            if(!pushService.isSubscriptionExistLocal(connection.getReconCtx(), connection.getDeparture())){
                return true;
            }else{

            }
        }
        return false;
    }



    public void deleteDossierSubscription(DossierSummary dossierSummary, Dossier dossier){

        IPushService pushService = NMBSApplication.getInstance().getPushService();
        List<Subscription> subscriptionList = null;
        String dossierId = "";
        if(dossierSummary != null){
            dossierId = dossierSummary.getDossierId();
        }
        if(dossier != null){
            dossierId = dossier.getDossierId();
        }
        //Log.e(TAG, "deleteDossierSubscription...The Dossier id is..." + dossierId);
        /*if(dossierSummary != null && !dossierSummary.isDossierPushEnabled()){
            if(dossierSummary != null) {
                subscriptionList = pushService.readAllSubscriptionsByDnr(dossierId);
                if(subscriptionList == null || subscriptionList.size() == 0){

                    dossierSummary.setDossierPushEnabled(false);
                    updateDossier(dossierSummary);

                }
            }
        }*/
        List<String> dnrList = new ArrayList<String>();
        dnrList.add(dossierId);
        DeleteAllSubScriptionAsyncTask asyncTask = new DeleteAllSubScriptionAsyncTask(subscriptionList,
                pushService, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
                context,true,dnrList,DeleteAllSubScriptionAsyncTask.Need_To_Clear_Subscription_Dnr, false);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void enableSubscription(Dossier dossier, Handler handler,String language){
        Log.d(TAG, "enableSubscription...." + dossier);
        List<Connection> createSubscriptionByConnections = new ArrayList<>();
        IPushService pushService = NMBSApplication.getInstance().getPushService();

        if(dossier != null){
            for(Connection connection : dossier.getConnections()){
                if(connection != null){
                    //deleteSubscription(dossier);
                    if(connection.getArrival().after(new Date())){
                        //The connections are created subscription.
                        if(!pushService.isSubscriptionExistLocal(connection.getReconCtx(), connection.getDeparture())){
                            createSubscriptionByConnections.add(connection);
                        }else{
                            //The connection is exist in local, to linked dossier
                            Subscription subscription = pushService.getSubscriptionFromLocal(connection.getReconCtx(), connection.getDeparture());

                            if(subscription != null){
                                Log.d(TAG, "This connection is exist in local");
                                if(subscription.getDnr() == null || subscription.getDnr().isEmpty()){
                                    //Log.d(TAG, "This connection is exist in local and no linked to dossier");
                                    boolean isDeleted = pushService.deleteSubscriptionInLocal(subscription.getSubscriptionId());
                                    if(isDeleted){
                                        subscription.setDnr(dossier.getDossierId());
                                        subscription.setConnectionId(connection.getConnectionId());
                                        pushService.saveSubscriptionInLocal(subscription);
                                        DossierSummary dossierSummary = getDossier(dossier.getDossierId());
                                        removeDossier(dossierSummary);
                                        dossierSummary.setDossierPushEnabled(true);
                                        addDossier(dossierSummary);
                                    }
                                }

                            }
                        }
                    }
                }
            }
            if(createSubscriptionByConnections != null && createSubscriptionByConnections.size() > 0){
                Log.d(TAG, "createSubscriptionByConnections......" + createSubscriptionByConnections.size());
                CreateAllSubScriptionAsyncTask asyncTask = new CreateAllSubScriptionAsyncTask(handler, dossier,
                        createSubscriptionByConnections, pushService, context,language);
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else{
                if(handler != null){
                    Log.d(TAG, "handler......");
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                }
            }
        }
    }

    public boolean showOverlay(Dossier dossier){

        if(dossier != null) {
            for (Connection connection : dossier.getConnections()) {
                if (connection != null) {
                    if (connection.getArrival().after(new Date()) && connection.getReconCtx() != null && !connection.getReconCtx().isEmpty()) {
                        //The connections are created subscription.
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void disableSubscription(Dossier dossier, DossierSummary dossierSummary, boolean isCallback){
        //Log.d(TAG, "Disable Subscription....");
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        //pushService.readAllSubscriptions();
        ISettingService settingService = NMBSApplication.getInstance().getSettingService();

        if(dossier != null) {
            List<Subscription> subscriptionList = pushService.readAllSubscriptionsByDnr(dossier.getDossierId());
            if(subscriptionList == null || subscriptionList.size() == 0){
                if (dossierSummary != null){
                    dossierSummary.setDossierPushEnabled(false);
                    updateDossier(dossierSummary);
                }
            }
            List<String> dnrList = new ArrayList<String>();
            dnrList.add(dossier.getDossierId());
            DeleteAllSubScriptionAsyncTask asyncTask = new DeleteAllSubScriptionAsyncTask(subscriptionList,
                    pushService, settingService.getCurrentLanguagesKey(), context,true,dnrList,DeleteAllSubScriptionAsyncTask.Need_To_Delete_Subscription, isCallback);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void deleteSubscription(Dossier dossier){
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        List<Subscription> subscriptionList = null;
        if(dossier != null) {
            for (Connection connection : dossier.getConnections()) {
                if (connection != null) {
                    //The connections no longer
                    if (connection.getReconCtx() == null || connection.getReconCtx().isEmpty()) {
                        if(subscriptionList == null){
                            subscriptionList = new ArrayList<>();
                        }
                        Subscription subscription = pushService.readSubscriptionByConnection(connection.getConnectionId());
                        if(subscription != null){
                            subscriptionList.add(subscription);
                            //Log.d(TAG, "The connections no longer.....");
                        }
                    }else{
                        //Log.d(TAG, "No connections no longer.....");
                    }
                }
            }
        }
        List<String> dnrList = new ArrayList<String>();
        dnrList.add(dossier.getDossierId());
        DeleteAllSubScriptionAsyncTask asyncTask = new DeleteAllSubScriptionAsyncTask(subscriptionList,
                pushService, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
                context, true, dnrList, DeleteAllSubScriptionAsyncTask.Need_To_Delete_Subscription, false);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isPushEnabled(Dossier dossier){
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        if(dossier != null){
            return dossierDatabaseService.isPushEnable(dossier.getDossierId());
        }
        return false;
    }
    public boolean isLinkedDnr(Dossier dossier){
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        if(dossier != null){
           return pushService.isLinkedDnr(dossier.getDossierId());
        }
        return false;
    }

    public int checkSubscriptionStatusForConnection(Connection connection){
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        if(connection != null){
            if(connection.getReconCtx() != null && !connection.getReconCtx().isEmpty()){
                //Log.d("Connection", "Connection ReconCtx is not null, the Ctx====" + connection.getReconCtx());
                //Subscription subscription = pushService.readAllSubscriptions().get(0);

                //Log.d("Connection", "subscription..." + subscription.getDeparture());
                if(pushService.isSubscriptionExistLocal(connection.getReconCtx(), connection.getDeparture())){
                    //Log.d("Connection", "isSubscription...");
                    return Dossier_Active;
                }
            }else{
                //Log.d("Connection", "Connection ReconCtx is null...");
                if(pushService.isSubscriptionExistLocal(connection.getOriginStationRcode(), connection.getDestinationStationRcode(), connection.getDeparture())){
                    //Log.d("Connection", "isSubscription...");
                    return Dossier_Active;
                }
            }
            if(connection.getArrival().after(new Date())){
                //Log.d("Connection", "Connection Arrival is after Now date...The Arrival is..."+ connection.getArrival());
                if(connection.getReconCtx() != null && !connection.getReconCtx().isEmpty()){
                    //Log.d("Connection", "Connection ReconCtx is not null, the Ctx====" + connection.getReconCtx());
                    if(!pushService.isSubscriptionExistLocal(connection.getOriginStationRcode(), connection.getDestinationStationRcode(), connection.getDeparture())){
                        //Log.d("Connection", "Not isSubscription...");
                        return Dossier_Failed;

                    }
                }else{
                    if(!pushService.isSubscriptionExistLocal(connection.getOriginStationRcode(), connection.getDestinationStationRcode(), connection.getDeparture())){
                        //Log.d("Connection", "Not isSubscription...");
                        return Dossier_NoActive;
                    }
                }
            }
        }
        return Dossier_Nothing;
    }

    private Connection getConnectionByTravelSegment(DossierTravelSegment dossierTravelSegment, Dossier dossier){
        List<Connection> connections = dossier.getConnections();
        if(dossierTravelSegment != null){
            if(dossierTravelSegment.getConnectionId() != null && !dossierTravelSegment.getConnectionId().isEmpty()){
                for (Connection connection : connections){
                    if(connection != null && connection.getConnectionId() != null && connection.getConnectionId().equalsIgnoreCase(dossierTravelSegment.getConnectionId())){
                        return connection;
                    }
                }
            }else {
                return null;
            }
        }
        return null;
    }

    public int checkSubscriptionStatusForTravelSegment(DossierTravelSegment dossierTravelSegment, Dossier dossier){
        Connection connection = getConnectionByTravelSegment(dossierTravelSegment, dossier);
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        if(connection != null){
            if(connection.getReconCtx() == null || connection.getReconCtx().isEmpty()){
                if(!pushService.isSubscriptionExistLocal(connection.getOriginStationRcode(), connection.getDestinationStationRcode(), connection.getDeparture())){
                    return checkSubscriptionStatusForTravelSegment(dossierTravelSegment);
                }else{
                    return checkSubscriptionStatusForConnection(connection);
                }
            }else{
                return checkSubscriptionStatusForConnection(connection);
            }
        }else{
            return  checkSubscriptionStatusForTravelSegment(dossierTravelSegment);
        }

    }

    private int checkSubscriptionStatusForTravelSegment (DossierTravelSegment dossierTravelSegment){
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        if(pushService.isSubscriptionExistLocal(dossierTravelSegment.getOriginStationRcode(), dossierTravelSegment.getDestinationStationRcode(), dossierTravelSegment.getDepartureDateTime())){
            return Dossier_Active;
        }else{
            if(dossierTravelSegment.getArrivalDate() == null || dossierTravelSegment.getArrivalTime() == null){
                if(dossierTravelSegment.getDepartureDate() == null || dossierTravelSegment.getDepartureTime() == null){

                }else{
                    if(dossierTravelSegment.getDepartureDateTime().after(new Date())){
                        return Dossier_NoActive;
                    }
                }
            }else{
                if(dossierTravelSegment.getDepartureDateTime().after(new Date())){
                    return Dossier_NoActive;
                }
            }
        }
        return Dossier_Nothing;
    }


    public void deletePastTicket(GeneralSetting generalSetting) {
        //Log.e(TAG, "DeletePastTicket....");
        //Log.e(TAG, "generalSetting...." + generalSetting);
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        int dossierAftersalesLifetime = 0;
        if (generalSetting != null) {
            dossierAftersalesLifetime = generalSetting.getDossierAftersalesLifetime();
        }
        //Log.e(TAG, "DeletePastTicket...The dossierAftersalesLifetime is...." + dossierAftersalesLifetime);
        List<DossierSummary> dossiers = dossierDatabaseService.selectPasetDossier(dossierAftersalesLifetime);
        if(dossiers != null){
            //Log.e(TAG, "DeletePastTicket...The Past dossiers size is...." + dossiers.size());
            for (DossierSummary dossierSummary : dossiers) {
                if(dossierSummary != null){
                    //Log.e(TAG, "DeletePastTicket...");
                    deleteDossier(dossierSummary);
                    /*dossierDatabaseService.deleteDossier(dossierSummary.getDossierId());
                    deleteFileOfPasttedTicket(dossierSummary);
                    deleteDossierSubscription(dossierSummary, null);*/
                }
            }
        }

    }

    public void deleteDossier(DossierSummary dossierSummary){
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        dossierDatabaseService.deleteDossier(dossierSummary.getDossierId());
        deleteFileOfPasttedTicket(dossierSummary);
        deleteDossierSubscription(dossierSummary, null);
    }

    private void deleteFileOfPasttedTicket(DossierSummary dossierSummary) {
        //Log.e(TAG, "DeletePastTicket...delete file for past dossier....");
        File f = FileManager.getInstance().getFileDirectory(context, dossierSummary.getDossierId(), dossierSummary.getDossierId());
        if(f != null){

            if (f.isDirectory()) {
                //Log.e(TAG, "DeletePastTicket...File is Directory....");
                File[] files = f.listFiles();
                for (File file2 : files) {
                    if (file2 != null) {
                        FileManager.getInstance().deleteExternalStoragePrivateFile(context, dossierSummary.getDossierId(), file2.getName());
                    }
                }
            }
            f.delete();
        }
    }



    public Dossier getCurrentDossier() {
        return currentDossier;
    }

    public DossierSummary getCurrentDossierSummary() {
        return currentDossierSummary;
    }

    public DossierTravelSegment getCurrentDossierTravelSegment() {
        return currentDossierTravelSegment;
    }

    public void setCurrentDossier(Dossier currentDossier) {
        this.currentDossier = currentDossier;
    }

    public void setCurrentDossierSummary(DossierSummary currentDossierSummary) {
        this.currentDossierSummary = currentDossierSummary;
    }

    public void setCurrentDossierTravelSegment(DossierTravelSegment currentDossierTravelSegment) {
        this.currentDossierTravelSegment = currentDossierTravelSegment;
    }

    public void autoRetrievalDossiers(){
        String customerId = "";
        String hash = "";
        LogonInfo logonInfo = NMBSApplication.getInstance().getLoginService().getLogonInfo();
        if(logonInfo != null){
            customerId = logonInfo.getCustomerId();
        }
        hash = NMBSApplication.getInstance().getLoginService().getControl();
        AutoRetrievalDossiersTask task = new AutoRetrievalDossiersTask(context, customerId, hash, getDossiers());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
