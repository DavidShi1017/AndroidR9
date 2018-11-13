package com.cflint.services;

import com.cflint.model.Settings;

import android.content.Context;
	/**
	*	Subscribe to push notification service.
	*/
public interface INotificationService {
	/**
	 *  Subscribe Favorite push notification service
	 *  @param context
	 *  @param settings
	 *  
	 */
	public void favoriteNotification(Context context, boolean settings);
	/**
	 * Get notification button status. true is turn on , false is turn off.
	 * @param context
	 */
	public boolean getFavoriteNotificationPreferences(Context context);
	/**
	 * save notification button status. true is turn on , false is turn off.
	 * @param context
	 * @param boolean settings
	 */
	public void setFavoriteNotificationPreferences(Context context, boolean settings);
	
	/**
	 * Get Settings from NotificationService
	 * @return Settings
	 */
	public Settings getSettings();
	/**
	 * Persistent notification on the status bar
	 * @param message
	 */
	public void registerNotificationOnStatusBar(String message);
	/**
	 * Cancel the notification on the status bar
	 */
	public void cancelNotificationOnStatusBar();
	/**
	 * Save notification connection status, true is connection is going, false connection is
	 * @param Context
	 * @param boolean Ongoing
	 */
	public void setNotificationOngoing(Context context, boolean Ongoing);
	/**
	 * Get notification connection status, true is connection is going, false connection is
	 * @param Context
	 * @return boolean
	 */
	public boolean getNotificationOngoingPreferences(Context context);
}
