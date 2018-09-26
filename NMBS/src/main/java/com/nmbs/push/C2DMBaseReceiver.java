package com.nmbs.push;

import java.io.IOException;

import com.nmbs.dataaccess.restservice.impl.NotificationDataService;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;


/**
 * Start new Thread . Base class for C2D message receiver. Includes constants for the 
 * strings used in the protocol. 
 */
public abstract class C2DMBaseReceiver extends IntentService{
	
	private static final String C2DM_RECEIVER = ".dataaccess.restservice.impl.NotificationDataService";
	
    private final String senderId;
    public static final String REGISTRATION_CALLBACK_INTENT = "com.google.android.c2dm.intent.REGISTRATION";
    private static final String C2DM_INTENT = "com.google.android.c2dm.intent.RECEIVE";
    private static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";
    private static final String TAG = "C2DMBaseReceiver";    
    
    // Extras in the registration callback intents.
    public static final String EXTRA_UNREGISTERED = "unregistered";

    public static final String EXTRA_ERROR = "error";

    public static final String EXTRA_REGISTRATION_ID = "registration_id";

    public static final String C2DM_MESSAGE_EXTRA = "message";
    public static final String C2DM_MESSAGE_SYNC = "sync";
    public static final String C2DM_ACCOUNT_EXTRA = "account_name";
    
    // wakelock  
    private static final String WAKELOCK_KEY = "C2DM_LIB";    
    private static PowerManager.WakeLock mWakeLock; 
    
    /**
     * The C2DMReceiver class must create a no-arg constructor and pass the 
     * sender id to be used for registration.
     */
    public C2DMBaseReceiver(String senderId) {
        // senderId is used as base name for threads, etc.
        super(senderId);
        this.senderId = senderId;
    }

    /**
     * Called when a cloud message has been received.
     */
    protected abstract void onMessage(Context context, Intent intent);

    /**
     * Called on registration error. Override to provide better
     * error messages.
     *  
     * This is called in the context of a Service - no dialog or UI.
     */
    public abstract void onError(Context context, String errorId);

    /**
     * Called when a registration token has been received.
     */
    public void onRegistered(Context context, String registrationId) throws IOException {
        // registrationId will also be saved
    }

    /**
     * Called when the device has been unregistered.
     */
    public void onUnregistered(Context context) {
    }

    
    /**
     * Called from the broadcast receiver. 
     * Will process the received intent, call handleMessage(), registered(), etc.
     * in background threads, with a wake lock, while keeping the service 
     * alive. 
     */
    static void runIntentInService(Context context, Intent intent) {
        if (mWakeLock == null) {  
            // This is called from BroadcastReceiver, there is no init.  
            PowerManager pm =   
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,   
                    WAKELOCK_KEY);  
        }  
        mWakeLock.acquire();  
    	
    	// Use a naming convention, similar with how permissions and intents are 
        // used. Alternatives are introspection or an ugly use of statics. 
        String receiver = context.getPackageName() + C2DM_RECEIVER;
        intent.setClassName(context, receiver);
        
        context.startService(intent);

    }

    @Override
    public final void onHandleIntent(Intent intent) {
        try {
            Context context = getApplicationContext();
            if (intent.getAction().equals(REGISTRATION_CALLBACK_INTENT)) {
                handleRegistration(context, intent);
                Log.d(TAG, "intent.getAction() = "+REGISTRATION_CALLBACK_INTENT);
            } else if (intent.getAction().equals(C2DM_INTENT)) {
            	Log.d(TAG, "intent.getAction() = "+C2DM_INTENT);
                onMessage(context, intent);
            } else if (intent.getAction().equals(C2DM_RETRY)) {
            	Log.d(TAG, "intent.getAction() = "+C2DM_RETRY);
                NotificationDataService.getInstance().register(context, senderId);
            }
        } finally {
            //  Release the power lock, so phone can get back to sleep.
            // The lock is reference counted by default, so multiple 
            // messages are ok.
            
            // If the onMessage() needs to spawn a thread or do something else,
            // it should use it's own lock.
        	mWakeLock.release();
        }
    }

   /**
    *  Processing after the registration of callback
    * @param context
    * @param intent
    */
    private void handleRegistration(final Context context, Intent intent) {
    	
        final String registrationId = intent.getStringExtra(EXTRA_REGISTRATION_ID);
        if(registrationId != null){
        	 Log.d(TAG, "Received RegistrationId = " + registrationId);        	 
        }
        
        String error = intent.getStringExtra(EXTRA_ERROR);
        String removed = intent.getStringExtra(EXTRA_UNREGISTERED);

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "dmControl: registrationId = " + registrationId +
                ", error = " + error + ", removed = " + removed);
        }

        if (removed != null) {
            // Remember we are unregistered
        	Log.d(TAG, "Unregistered is readying...");
        	onUnregistered(context);
        	//C2DMessaging.getInstance(context).clearRegistrationId(context);        	
            return;
        } else if (error != null) {
            // we are not registered, can try again
        	//C2DMessaging.getInstance(context).clearRegistrationId(context);
            // Registration failed
            Log.e(TAG, "Registration error " + error);
            onError(context, error);
            if ("SERVICE_NOT_AVAILABLE".equals(error)) {
                long backoffTimeMs = C2DMessaging.getInstance(context).getBackoff(context);
                
                Log.d(TAG, "Scheduling registration retry, backoff = " + backoffTimeMs);
                Intent retryIntent = new Intent(C2DM_RETRY);
                PendingIntent retryPIntent = PendingIntent.getBroadcast(context, 
                        0 /*requestCode*/, retryIntent, 0 /*flags*/);
                
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.ELAPSED_REALTIME,
                        backoffTimeMs, retryPIntent);

                // Next retry should wait longer.
                backoffTimeMs *= 2;
                C2DMessaging.getInstance(context).setBackoff(context, backoffTimeMs);
            } 
        } else {
            try {
            	C2DMessaging.getInstance(context).setRegistrationId(context, registrationId);
            	Log.d(TAG, "Registered is readying...");
                onRegistered(context, registrationId);
 
            } catch (IOException ex) {
                Log.e(TAG, "Registration error " + ex.getMessage());
            }
        }
    }


}
