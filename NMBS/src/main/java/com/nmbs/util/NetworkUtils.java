package com.nmbs.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.nmbs.R;
import com.nmbs.activities.AlertActivity;
import com.nmbs.activities.view.DialogAlertError;

public class NetworkUtils {
	public final static int NONE = 0; // No network

	public final static int WIFI = 1; // Wi-Fi

	public final static int MOBILE = 2; // 3G,GPRS

	/**
	 * Get current network status
	 * @param context
	 * @return NONE = 0, No network;     WIFI = 1, Wi-Fi ;     MOBILE = 2, 3G,GPRS  ;  
	 */
	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state;
		// mobile network
		if (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return MOBILE;
			}
		}
		 
		if (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) {
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return WIFI;
			}
		}
		
		// Wifi network
		
		
		return NONE;
	}

	public static boolean isOnline (Activity activity){
		if(getNetworkState(activity) > 0){
			return true;
		}else {
			if(!activity.isFinishing()){
				DialogAlertError dialogError = new DialogAlertError(activity, activity.getResources().getString(R.string.general_information),
						activity.getResources().getString(R.string.alert_no_network));
				dialogError.show();
			}

			return false;
		}
	}


}
