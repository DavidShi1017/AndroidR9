package com.nmbs.model;

/**
 * Setup related properties, mainly for the local setting.
 * @author David.Shi
 */

public class Settings {
	private boolean favoritePushNotifications;

	public boolean isFavoritePushNotifications() {
		return favoritePushNotifications;
	}

	public void setFavoritePushNotifications(boolean favoritePushNotifications) {
		this.favoritePushNotifications = favoritePushNotifications;
	}
	
}
