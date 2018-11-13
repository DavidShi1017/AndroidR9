package com.cflint.services.impl;

import com.cflint.R;

import com.cflint.dataaccess.restservice.impl.NotificationDataService;
import com.cflint.model.Settings;
import com.cflint.services.INotificationService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
/**
*	Subscribe to push notification service.
*/
public class NotificationService implements INotificationService {
	
	public static final String NOTIFICATION_SETTINGS = "NOTIFICATION_SETTINGS";
	public static final String NOTIFICATION_ONGOING = "NOTIFICATION_ONGOING";
	private Context context;	
	private static NotificationService instance;
	public static final int NOTIFICATION_ID = 4745;	
	private SharedPreferences prefs;
	private Settings settings;
	private Notification notification;
	private NotificationManager notificationManager;
	private PendingIntent pendingIntent;
	
	public NotificationService(Context context){
		this.context = context;
	}
	
	public static NotificationService getInstance(Context context) {			
		if(instance == null){
			instance = new NotificationService(context);	
		}
		return instance;
	}
	
	/**
	 * Get Settings from NotificationService
	 * @return Settings
	 */
	public Settings getSettings(){
		if (this.settings == null) {
			this.settings = new Settings();
		}
		return this.settings;
	}
	
	/**
	 * Save notification button status. true is turn on , false is turn off.
	 * @param context
	 * @param boolean settings
	 */
	public void setFavoriteNotificationPreferences(Context context, boolean settings){
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = this.prefs.edit();
		editor.putBoolean("NOTIFICATION_SETTINGS", settings);
		editor.commit();
		this.settings.setFavoritePushNotifications(settings);
	}
	/**
	 * Get notification button status. true is turn on , false is turn off.
	 * @param context
	 */
	public boolean getFavoriteNotificationPreferences(Context context){
		if (this.prefs == null) {
			this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return this.prefs.getBoolean(NOTIFICATION_SETTINGS, false);
	}
		
	/**
	 *  Subscribe Favorite push notification service
	 *  @param context
	 *  @param settings
	 *  
	 */
	public void favoriteNotification(Context context, boolean settings) {
		AsyncNotificationResponse asyncNotificationResponse = AsyncNotificationResponse.getInstance();
		asyncNotificationResponse.registerReceiver(context);
		NotificationDataService.getInstance()
		.executeFavoriteNotification(settings, context, "");		
	}
	
	/**
	 * Persistent notification on the status bar
	 * @param message
	 */
	public void registerNotificationOnStatusBar(String message){		
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);		
		notification = new Notification(android.R.drawable.stat_sys_download, "Action", System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL; //the notification should be canceled when it is clicked
		//click it, load to a activity. 
		//NMBSA.tabSpec = 2;		
		/*Intent intent = new Intent(context, Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		pendingIntent = PendingIntent.getActivity(context, 0, intent , PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context,context.getString(R.string.notification_content),message, pendingIntent);
		notification.when = System.currentTimeMillis();		
		notification.defaults |= Notification.DEFAULT_SOUND; // sound
		notificationManager.notify(NOTIFICATION_ID, notification);*/
	}
	
	/**
	 * Cancel the notification on the status bar
	 */
	public void cancelNotificationOnStatusBar(){
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}
	
	/**
	 * Get notification connection status, true is connection is going, false connection is
	 * @param Context
	 * @return boolean
	 */
	public boolean getNotificationOngoingPreferences(Context context) {
		if (this.prefs == null) {
			this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return this.prefs.getBoolean(NOTIFICATION_ONGOING, false);
	}

	/**
	 * Save notification connection status, true is connection is going, false connection is
	 * @param Context
	 * @param boolean Ongoing
	 */
	public void setNotificationOngoing(Context context, boolean Ongoing) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = this.prefs.edit();
		editor.putBoolean(NOTIFICATION_ONGOING, Ongoing);
		editor.commit();
	}
	

}
