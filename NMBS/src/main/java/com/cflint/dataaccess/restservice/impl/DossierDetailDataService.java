package com.cflint.dataaccess.restservice.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


import com.cflint.R;
import com.cflint.activities.view.uk.co.senab.photoview.PhotoViewAttacher;
import com.cflint.application.NMBSApplication;
import com.cflint.async.CheckOptionAsyncTask;
import com.cflint.async.RealTimeInfoAsyncTask;
import com.cflint.dataaccess.converters.CustomErrorMessager;
import com.cflint.dataaccess.converters.DossierDetailConverter;
import com.cflint.dataaccess.converters.DossierListConverter;
import com.cflint.dataaccess.converters.RealTimeInfoResponseConverter;
import com.cflint.dataaccess.converters.ResendInfoConverter;
import com.cflint.dataaccess.database.DossierDatabaseService;
import com.cflint.dataaccess.database.SubscriptionDataBaseService;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.DBooking343Error;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.DonotContainTicket;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.log.LogUtils;
import com.cflint.model.Connection;
import com.cflint.model.Dossier;
import com.cflint.model.DossierDetailsResponse;
import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;
import com.cflint.model.DossiersList;
import com.cflint.model.GeneralSetting;
import com.cflint.model.PDF;
import com.cflint.model.RealTimeInfoRequestForConnection;
import com.cflint.model.RealTimeInfoRequestForTravelSegment;
import com.cflint.model.RealTimeInfoRequestParameter;
import com.cflint.model.RealTimeInfoResponse;
import com.cflint.model.ResendInfoResponse;
import com.cflint.model.Ticket;
import com.cflint.model.TravelSegment;
import com.cflint.services.IPushService;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.util.AESUtils;
import com.cflint.util.DateUtils;
import com.cflint.util.FileManager;
import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.HttpRetriever;
import com.cflint.util.ObjectToJsonUtils;
import com.cflint.util.Utils;

import org.apache.commons.lang.StringUtils;


import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by shig on 2016/2/19.
 */
public class DossierDetailDataService extends CustomErrorMessager {
    private static final String TAG = DossierDetailDataService.class.getSimpleName();
    private HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();


    public DossierDetailsResponse executeDossierDetail(
            Context context, String email, String dnr, String languageBeforSetting, boolean isNeedCatchCustomerError, boolean isWaitDownload, boolean executeRealTime)
            throws InvalidJsonError, JSONException, TimeOutError,
            ParseException, RequestFail, IOException, ConnectionError,
            BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError, NoSuchAlgorithmException, DonotContainTicket {
        DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(context);
        DossierSummary dossierSummary = dossierDatabaseService.selectDossier(dnr);
        IPushService pushService = NMBSApplication.getInstance().getPushService();
        boolean isUpload;
        if(dossierSummary == null){
            isUpload = true;
        }else{
            isUpload = false;
        }

        //Log.e(TAG, "languageBeforSetting......................" + languageBeforSetting);
        Log.e(TAG, "dnr......................" + dnr);

        // operate titles part.
        String stringHttpResponse = null;
        String serviceVersion = "";

        String urlString = context.getString(R.string.server_url_get_dossier_detail)+ "/" + dnr;
        if (email != null && !StringUtils.isEmpty(email)) {
            //urlString += "?email=" + email;
            //isUpload = false;
            if (isUpload) {
                urlString += "?Context=Upload";
                urlString += "&Control=" + email;
            }else {
                urlString += "?Context=Refresh";
                urlString += "&Control=" + Utils.sha1(dnr + "20187467");
            }
        }else{
            urlString += "?Context=Refresh";
            urlString += "&Control=" + Utils.sha1(dnr + "20187467");
        }
        serviceVersion = HTTPRestServiceCaller.API_VERSION_VALUE_6;
        stringHttpResponse = httpRestServiceCaller
                .executeHTTPRequest(context, null, urlString, languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 15000, false, "", serviceVersion);
        //stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, null, "error.json");
        //FileManager.getInstance().createExternalStoragePrivateFileFromString(context, dnr, dnr + ".json", stringHttpResponse);
        //stringHttpResponse = FileManager.getInstance().readExternalStoragePrivateFile(context, DNR, DNR + ".json");
        DossierDetailConverter dossierDetailConverter = new DossierDetailConverter();
        DossierDetailsResponse dossierResponse = dossierDetailConverter.parsesDossierDetailResponse(stringHttpResponse);
        super.throwErrorMessage(dossierResponse, context, context.getString(R.string.server_url_get_dossier_detail));
        if (dossierResponse != null && dossierResponse.getDossier() != null) {
            //Log.d(TAG, "Get dossier response Successful......................");
            boolean dossierPushEnabled = false;
            //dossierResponse.getDossier().setTravelSegments(null);
            if (dossierResponse.getDossier().getTravelSegments() != null && dossierResponse.getDossier().getTravelSegments().size() > 0){
                if (!isUpload){
                    Log.e(TAG, "Refresh dossier logic......................");
                    dossierSummary = dossierDatabaseService.selectDossier(dossierResponse.getDossier().getDossierId());
                    if(dossierSummary != null){
                        DossierDetailsResponse dossierResponseOld = dossierDetailConverter.parsesDossierDetailResponse(dossierSummary.getDossierDetails());
                        if(dossierResponseOld != null && dossierResponseOld.getDossier() != null){
                            Log.e(TAG, "Refresh dossier logic.........dossierResponseOld is not null.............");
                            List<DossierTravelSegment> travelSegmentsOld = dossierResponseOld.getDossier().getTravelSegments();
                            for(DossierTravelSegment dossierTravelSegment : travelSegmentsOld){
                                if(dossierTravelSegment != null){
                                    Log.e(TAG, "Refresh dossier logic.........dossierTravelSegmentOld is not null.............");
                                    pushService.deleteLocalNotificationFromDatabase(dossierTravelSegment);
                                }
                            }
                        }

                        dossierPushEnabled = dossierSummary.isDossierPushEnabled();
                    }
                }else {
                    if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
                        CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(context);
                        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
                //dossierPushEnabled = false;
                //Log.d(TAG, "TravelSegments Available......................");

                if(!isWaitDownload && executeRealTime){
                    Map<String, Object> realTimeInfoRequestMap = getRealTimeInfoRequestList(dossierResponse.getDossier(), NMBSApplication.getInstance().getMasterService().loadGeneralSetting());
                    if(realTimeInfoRequestMap != null && realTimeInfoRequestMap.size() > 0){
                        //Log.d(TAG, "RealTimeInfoRequestList Available......................");
                        RealTimeInfoRequestParameter realTimeInfoRequestParameter = new RealTimeInfoRequestParameter(getRealTimeInfoRequestList(realTimeInfoRequestMap));
                        RealTimeInfoAsyncTask asyncTask = new RealTimeInfoAsyncTask(context, NMBSApplication.getInstance().getSettingService(),
                                NMBSApplication.getInstance().getDossierDetailsService(), null, realTimeInfoRequestParameter);
                        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }else{
                        //Log.d(TAG, "RealTimeInfoRequestList Unavailable......................");
                    }
                }

                boolean isSuccessfullyPDF = downloadPdf(context, dossierResponse.getDossier());
                //Log.e(TAG, "isSuccessfullyPDF......................" + isSuccessfullyPDF);
                boolean isSuccessfullyBarcode = downloadBarcode(context, dossierResponse.getDossier());
                boolean travelSegmentAvailable = true;
                boolean displayOverlay = false;
                travelSegmentAvailable = true;
                List<Connection> connections = dossierResponse.getDossier().getConnections();
                if(connections != null && connections.size() > 0){
                    //Log.d(TAG, "Has connections in dossierResponse......................");
                    for(Connection connection : connections){
                        if (connection != null && connection.getReconCtx() != null && !connection.getReconCtx().isEmpty()){
                            //Log.d(TAG, "Should show display Overlay......................");
                            Date now = new Date();
                            Date arrivalDate = connection.getArrival();
                            if(arrivalDate.after(now)){
                                displayOverlay = true;
                            }
                        }
                    }
                }
                dossierSummary = new DossierSummary(dossierResponse.getDossier().getDossierId(), stringHttpResponse, DateUtils.dateTimeToString(new Date()),
                        dossierPushEnabled, isSuccessfullyPDF, isSuccessfullyBarcode, travelSegmentAvailable, displayOverlay, dossierResponse.getDossier().getLatestTravelDate(),
                        dossierResponse.getDossier().getEarliestTravelDate());

                dossierDatabaseService.deleteDossier(dossierResponse.getDossier().getDossierId());
                dossierDatabaseService.insertDossier(dossierSummary);


                pushService.addAllLocalNotification(dossierResponse.getDossier().getTravelSegments());
            }else{
                /*dossierSummary = new DossierSummary(dossierResponse.getDossier().getDossierId(), "", DateUtils.dateTimeToString(new Date()),
                        false, false, false, false, false, dossierResponse.getDossier().getLatestTravelDate(),
                        dossierResponse.getDossier().getEarliestTravelDate());*/
                SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(context);
                subscriptionDataBaseService.deleteSubscriptionByDnr(dnr);
                dossierDatabaseService.deleteDossier(dnr);
                //dossierDatabaseService.insertDossier(dossierSummary);
                //Log.d(TAG, "TravelSegments Unavailable......................");
                //throw new DonotContainTicket();
                //return null;
            }
            return dossierResponse;
        }
        return null;
    }

    private boolean download(Context context, String url, String dossierId, String fileName){
        boolean hasError = false;
        InputStream inputStream = null;
        try {
            inputStream = HttpRetriever.getInstance().retrieveStream(url);
            /*if(inputStream != null){
                if(inputStream.available() < 1){
                    return true;
                }
            }*/

            //Thread.sleep(500);
            Log.d(TAG, "The File name is......" + inputStream.available());
            //InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierId, inputStream));
            //InputStream afterDecryptInputStream = new ByteArrayInputStream(AESUtils.encryptPdfOrBarcode(dossierId, inputStream));
							/*httpDownloader.downloadNetworkFile(urlString, FileManager.getInstance().getFilePath("/pdfpath/"),fileName + ".pdf");  */
            //Log.d(TAG, "The folder name is......" + dossierId);


            FileManager.getInstance().createExternalStoragePrivateFile(context, inputStream, dossierId, "." + fileName);
            //File file = FileManager.getInstance().getExternalStoragePrivateFile(context, dossierId, fileName);
            //Log.d(TAG, "The File name is......" + file.isFile());

        } catch (Exception e) {
            hasError = true;
            e.printStackTrace();
        }

        return hasError;
    }

    private boolean downloadPdf(Context context, Dossier dossier){

        if(NMBSApplication.getInstance().getTestService().isDownloadFile()){
            return false;
        }
        boolean isSuccessfully = true;
        boolean hasError = false;
        List<PDF> pdfs = dossier.getPdfs();
        for (PDF pdf : pdfs) {
            if (pdf != null && pdf.getPdfURL() != null && !pdf.getPdfURL().equalsIgnoreCase("")){
                String fileName = dossier.getDossierId() + "-" + pdf.getPdfId() + ".pdf";
                String pdfUrl = pdf.getPdfURL();
                //String pdfUrl = "https://accept-shared.bene-system.com//hp/pdfservice?pdf=PSFGKKD1526979382140BS2218";
                boolean hasThisFile = FileManager.getInstance().hasExternalStoragePrivateFile(context, dossier.getDossierId(), "." + fileName);
                //Log.d(TAG, "Starting Pdf file name......" + fileName );
                //Log.d(TAG, "Starting Pdf file urlString......" + pdfUrl );
                if (!hasThisFile) {
                    hasError = download(context, pdfUrl, dossier.getDossierId(), fileName);
                    if (hasError){
                        //Log.d(TAG, "Download pdf unsuccessful......");
                        hasError = download(context, pdfUrl, dossier.getDossierId(), fileName);
                        //Log.d(TAG, "Start Download pdf second time......");
                        if (hasError){
                            //Log.d(TAG, "Download pdf unsuccessful second time......");
                        }else{
                            isSuccessfully = true;
                           // Log.d(TAG, "Download pdf Successfully......");
                        }
                    }else{
                        isSuccessfully = true;
                        //Log.d(TAG, "Download pdf Successfully......");
                    }
                }else{
                    isSuccessfully = true;
                }
            }
        }
        return isSuccessfully;
    }


    private boolean downloadBarcode(Context context, Dossier dossier){
        if(NMBSApplication.getInstance().getTestService().isDownloadFile()){
            return false;
        }
        boolean isSuccessfully = true;
        boolean hasError = false;
        String urlPrefixString = context.getString(R.string.barcode_url_prefix);
        List<DossierTravelSegment> dossierTravelSegments = dossier.getTravelSegments();
        for (DossierTravelSegment dossierTravelSegment: dossierTravelSegments) {
            if (dossierTravelSegment != null){
                List<Ticket> tickets = dossierTravelSegment.getTickets();
                for (Ticket ticket : tickets) {
                    if (ticket != null && ticket.getBarcodeURL() != null && !ticket.getBarcodeURL().equalsIgnoreCase("")){
                        Bitmap mBitmap = null;
                        String fileName = dossier.getDossierId() + "-" + ticket.getBarcodeId() + ".png";
                        String pdfUrl = urlPrefixString + ticket.getBarcodeURL();
                        boolean hasThisFile = FileManager.getInstance().hasExternalStoragePrivateFile(context, dossier.getDossierId(), "." + fileName);
                        //Log.d(TAG, "Starting Barcode file......" + fileName );
                        Log.d(TAG, "Starting Barcode file urlString......" + pdfUrl );
                        if (!hasThisFile) {
                            hasError = download(context, pdfUrl, dossier.getDossierId(), fileName);
                            if (hasError){
                                Log.d(TAG, "Download Barcode unsuccessful......");
                                hasError = download(context, pdfUrl, dossier.getDossierId(), fileName);
                                Log.d(TAG, "Start Download Barcode second time......");
                                if (hasError){
                                    Log.d(TAG, "Download Barcode unsuccessful second time......");
                                }else{
                                    isSuccessfully = true;
                                    Log.d(TAG, "Download Barcode Successfully......");
                                }
                            }else{
                                isSuccessfully = true;
                                Log.d(TAG, "Download Barcode Successfully......");
                            }
                        }
                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            //options.inTempStorage = new byte[16 * 1024];
                            options.inSampleSize = 1;
                            mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().
                                    getExternalStoragePrivateFilePath(context, dossier.getDossierId(), "." + fileName), options);
                            //mBitmap = NMBSApplication.getInstance().getAssistantService().getStreamFromEncryptFile(dossier.getDossierId(), ticket.getBarcodeId());
                            if(mBitmap != null){
                                isSuccessfully = true;
                            }else{
                                isSuccessfully = false;
                            }

                        } catch (Exception e) {
                            isSuccessfully = false;
                        }
                    }
                }
            }
        }
        return isSuccessfully;
    }
    public List<Object> getRealTimeInfoRequestList(Map<String, Object> map){
        List<Object> objects = new ArrayList<>();
        Iterator iter = map.entrySet().iterator();  //获得map的Iterator
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            objects.add(entry.getValue());
        }
        return objects;
    }



    public Map<String, Object> getRealTimeInfoRequestList(Dossier dossier, GeneralSetting generalSetting){
        Map<String, Object> realTimeInfoRequestList = new HashMap<String, Object>();
        int maxRealTimeInfoHorizon = 0;
        if(generalSetting != null){
            maxRealTimeInfoHorizon = generalSetting.getMaxRealTimeInfoHorizon();
        }
        //Log.e(TAG, "maxRealTimeInfoHorizon===" + maxRealTimeInfoHorizon);
        if(dossier != null){
            List<DossierTravelSegment> dossierTravelSegments = dossier.getTravelSegments();
            for(DossierTravelSegment dossierTravelSegment : dossierTravelSegments){
                if(dossierTravelSegment != null){
                    if(DossierDetailsService.SegmentType_MARKETPRICE.equalsIgnoreCase(dossierTravelSegment.getSegmentType())
                            || DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                        if (validRealTime(dossierTravelSegment.getDepartureDate(), maxRealTimeInfoHorizon)){
                            realTimeInfoRequestList.put(dossierTravelSegment.getTravelSegmentId(), new RealTimeInfoRequestForTravelSegment(dossierTravelSegment.getTravelSegmentId(), DossierDetailsService.Dossier_Realtime_Segment,
                                    DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDate(), "dd MMM yyyy"),
                                    DateUtils.dateTimeToString(dossierTravelSegment.getDepartureTime(), "HH:mm"), dossierTravelSegment.getTrainNumber(),
                                    dossierTravelSegment.getOriginStationRcode(), dossierTravelSegment.getDestinationStationRcode()));
                        }
                    }
                }
            }
            for(Connection connection : dossier.getConnections()){
                if(connection != null && connection.getReconCtx() != null && !connection.getReconCtx().isEmpty()){
                    if(validRealTime(connection.getDeparture(), maxRealTimeInfoHorizon)){
                        realTimeInfoRequestList.put(connection.getConnectionId(), new RealTimeInfoRequestForConnection(connection.getConnectionId(), DossierDetailsService.Dossier_Realtime_Connection,
                                connection.getReconCtx(), connection.getDeparture()));
                    }
                }
            }
        }
        //Log.e(TAG, "realTimeInfoRequestList===" + realTimeInfoRequestList.size());
        return realTimeInfoRequestList;
    }

    private boolean validRealTime(Date date, int maxRealTimeInfoHorizon){
        boolean isValid = false;
        Date now = new Date();
        Date newDate = DateUtils.getFewLaterDay(now, maxRealTimeInfoHorizon);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date departureDate  = calendar.getTime();
        departureDate.setHours(0); departureDate.setMinutes(0); departureDate.setSeconds(0);
        now.setHours(0); now.setMinutes(0); now.setSeconds(0);
        newDate.setHours(0); newDate.setMinutes(0); newDate.setSeconds(0);

        Calendar calendarDeparture = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarNew = Calendar.getInstance();
        calendarDeparture.setTime(departureDate);
        calendarNow.setTime(now);
        calendarNew.setTime(newDate);
        double departureAndNowCount = (calendarDeparture.getTimeInMillis() - calendarNow.getTimeInMillis())/(1000*3600*24);
        double departureAndNewCount = (calendarDeparture.getTimeInMillis() - calendarNew.getTimeInMillis())/(1000*3600*24);
        //Log.e("Calendar", "newDate DATE:::" + newDate);
        //Log.e("Calendar", "Departure DATE:::" + departureDate);
        //Log.e(TAG, "now DATE:::::::::" + now);
        //Log.e("Calendar", "dayCount:::" + departureAndNowCount);
        //Log.e("Calendar", "dayNewCount:::" + departureAndNewCount);
        //Log.e(TAG, "!departureDate.before(now):::" + (departureDate.getTime() == now.getTime()));
        //Log.e("Calendar", "(departureDate.before(newDate) || departureAndNewCount == 0):::" + (calendarDeparture.before(calendarNew) || departureAndNewCount == 0));
        //Log.e("Calendar", "(!departureDate.before(now) || departureAndNowCount == 0):::" + (!calendarDeparture.before(calendarNow) || departureAndNowCount == 0));
        /*if((calendarDeparture.before(calendarNow) || departureAndNowCount == 0) && (calendarDeparture.before(calendarNew) || departureAndNewCount == 0)){
            isValid = true;
        }*/
        if(departureAndNowCount >= 0 && departureAndNowCount < maxRealTimeInfoHorizon){
            isValid = true;
        }
        //Log.e("Calendar", "isValid:::" + isValid);
        return isValid;
    }

    public List<RealTimeInfoResponse> callRefreshTimeInfo(Context context, String languageBeforSetting, RealTimeInfoRequestParameter realTimeInfoRequestParameter) {
                List<RealTimeInfoResponse> realTimeInfoResponses = null;
                try{
                    HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
                    //Log.i("RealTime", "Refresh RealTime info...... ");
                    RealTimeInfoResponseConverter realTimeInfoResponseConverter = new RealTimeInfoResponseConverter();
                    String postJsonString = ObjectToJsonUtils.getPostRealTimeInfoQueryStr(realTimeInfoRequestParameter);
                    String urlString = context.getString(R.string.server_url_realtimeinfo);

                    String response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString,
                            languageBeforSetting, HTTPRestServiceCaller.HTTP_POST_METHOD, 15000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_6);
                    //Log.d("RealTime", "Refresh RealTime info response...... " + response);
                     realTimeInfoResponses = realTimeInfoResponseConverter.parseRealTimeResponse(response);
                }catch (Exception e){
                    //Log.e("RealTime", "Refresh RealTime error...... " + e.getMessage());
                    e.printStackTrace();
                }



        return realTimeInfoResponses;
    }

    public DossiersList executeDossierList(Context context, String language, String customerId, String hash) throws Exception{
        LogUtils.d("DossierDetailDataService", "executeDossierList------->");
        String errorMsg = "";
        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "{\"CustomerId\":\"" + customerId + "\",\"Control\":\"" + hash + "\"}";
        String urlString = context.getString(R.string.server_url_dossier_list);
        LogUtils.d("DossierDetailDataService", "executeResend----postJsonString--->" + postJsonString + "----language---->" + language);
        String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                HTTPRestServiceCaller.API_VERSION_VALUE_7);
        /*String response = "{\n" +
                "  \"Dnrs\": [\n" +
                "    \"SDPNBCL\",\n" +
                "    \"KTPPJSH\",\n" +
                "    \"KSQKSNN\",\n" +
                "    \"KQJPNHV\",\n" +
                "    \"CPQXPFM\",\n" +
                "    \"BWJKKSR\"\n" +
                "  ],\n" +
                "  \"CallSuccessful\": true,\n" +
                "  \"Messages\": []\n" +
                "}";*/

        LogUtils.d("DossierDetailDataService", "executeResend----response str--->" + response);

        DossierListConverter converter = new DossierListConverter();
        DossiersList dossiersList = converter.parseResponse(response);

        if(dossiersList != null){
            List<String> list = dossiersList.getDnrs();
            for (String dnr : list){
                LogUtils.d("DossierDetailDataService", "String--->" + dnr);
            }
        }

        return dossiersList;
    }

}
