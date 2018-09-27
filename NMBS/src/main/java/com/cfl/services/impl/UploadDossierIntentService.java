 package com.cfl.services.impl;


 import android.app.IntentService;
 import android.content.Context;
 import android.content.Intent;
 import android.os.AsyncTask;


 import com.cfl.async.RealTimeAsyncTask;
 import com.cfl.dataaccess.restservice.IStationBoardDataService;
 import com.cfl.dataaccess.restservice.impl.DossierDetailDataService;
 import com.cfl.exceptions.ConnectionError;
 import com.cfl.exceptions.CustomError;
 import com.cfl.exceptions.DonotContainTicket;
 import com.cfl.exceptions.JourneyPast;
 import com.cfl.exceptions.NetworkError;
 import com.cfl.model.DossierDetailParameter;
 import com.cfl.model.DossierDetailsResponse;

 import java.io.Serializable;
 import java.util.List;


 /**
  * OfferIntentService runs in a new thread the executions of the OfferService
  *
  * An Intent is received with the in parameter as key PARAM_IN_MSG
  *
  * An Intent is broadcasted with the result
  */
 public class UploadDossierIntentService extends IntentService {

     private static final String TAG = UploadDossierIntentService.class.getSimpleName();
	 private static final String PARAM_DNR = "DNR";
	 private static final String PARAM_EMAIL = "Email";
     private static final String PARAM_IsUpload = "IsUpload";
     private static final String PARAM_IsWaitDownload = "IsWaitDownload";

     private static Context mContext;
     private Intent broadcastIntent = new Intent();

     public UploadDossierIntentService() {
         super(".intentservices.UploadDossierIntentService");
     }

     private DossierDetailsResponse dossierResponse;

     private List<DossierDetailParameter> dossiers;
     private String language;
     private DossierDetailDataService dossierDetailDataService;
     private boolean hasError;
     private boolean isCorrupted;
     private int dnrCount;
     private boolean isUpload;
     private boolean isWaitDownload;
     @Override
     protected void onHandleIntent(Intent intent) {

         dossiers = (List<DossierDetailParameter>) intent.getSerializableExtra(PARAM_DNR);
         language = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
         isUpload = intent.getBooleanExtra(PARAM_IsUpload, false);
         isWaitDownload = intent.getBooleanExtra(PARAM_IsWaitDownload, false);
		 dossierDetailDataService = new DossierDetailDataService();
         handleOrder();
     }

     private void handleOrder(){
         try {
             for (DossierDetailParameter dossierDetailParameter : dossiers){
                 if(dossierDetailParameter != null){
                     //Log.d(TAG, "dossierDetailParameter...." + dossierDetailParameter.getEmail());
                     dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, dossierDetailParameter.getEmail(), dossierDetailParameter.getDnr(),
                             language, true, isWaitDownload, true);
                     if (dossierResponse != null) {
                         dnrCount++;
                     }
                 }
             }
             if(dnrCount == dossiers.size()){
                // Log.d(TAG, "get the dossier...Response...succeed");
                 sendBroadcast(dossierResponse);
             }else{
                // Log.d(TAG, "Exception...: ");
                 catchError(NetworkError.TIMEOUT, null);
             }


         }catch (ConnectionError e) {
             //Log.d(TAG, "ConnectionError...: ");
             catchError(NetworkError.wrongCombination, null);

         }catch (DonotContainTicket e) {
             //Log.d(TAG, "DonotContainTicket...: ");
             catchError(NetworkError.donotContainTicke, null);

         }/*catch (JourneyPast e) {
             Log.d(TAG, "JourneyPast...: ");
             catchError(NetworkError.journeyPast, null);

         }*/catch (CustomError e) {
             catchError(NetworkError.CustomError, e);
         }catch (Exception e) {
             e.printStackTrace();
             //Log.d(TAG, "Exception...: ");
             catchError(NetworkError.TIMEOUT, null);
         }

     }

     private void createStationBoard(String dnr){

         RealTimeAsyncTask realTimeAsyncTask = new RealTimeAsyncTask(language, mContext, null);
         realTimeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
     }

     private void sendBroadcast(DossierDetailsResponse dossierResponse){

         if(dossierResponse != null){
             if (isCorrupted) {
                 hasError = true;
             }
             broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_DETAIL_RESPONSE);
             broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
             broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, dossierResponse);
             broadcastIntent.putExtra("hasError", hasError);
             sendBroadcast(broadcastIntent);
         }
     }


     /**
      * Start this IntentService.
      * @param context
      */
     public static void startService(Context context, List<DossierDetailParameter> dossiers, String languageBeforSetting, boolean isUpload, boolean isWaitDownload){
         //Log.d(TAG, "UploadDossierIntentService start......");
         mContext = context;

         Intent msgIntent = new Intent(context, UploadDossierIntentService.class);
         msgIntent.putExtra(PARAM_DNR, (Serializable)dossiers);
         msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
         msgIntent.putExtra(PARAM_IsUpload, isUpload);
         msgIntent.putExtra(PARAM_IsWaitDownload, isWaitDownload);
         context.startService(msgIntent);
     }

     /**
      * Set error broadcast action,and sent broadcast.
      */
     public void catchError(NetworkError error, Exception e){
         //Log.e(TAG, "catchError error" + error);
         broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_DOSSIER_DETAIL_ERROR);
         broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
         broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
         if(e != null)
         broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
         sendBroadcast(broadcastIntent);
         if(e != null){
             e.printStackTrace();
         }
     }
 }