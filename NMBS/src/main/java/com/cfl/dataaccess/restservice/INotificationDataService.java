package com.cfl.dataaccess.restservice;

import android.content.Context;

/**
 * Call web service and Support Notification Data for NotificationService.
 */
public interface INotificationDataService {
	/**
	 *  Execute to subscribe Favorite push notification service
	 *  @param settings
	 *  @param context 
	 *  @param senderId	 
	 */
	public void executeFavoriteNotification(boolean settings, Context context, String senderId) throws Exception;
}
