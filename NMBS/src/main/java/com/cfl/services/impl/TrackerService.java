package com.cfl.services.impl;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;
import com.cfl.application.NMBSApplication;

public class TrackerService {
	public static final String TAG = "Tracker";
	private Context gaContext;
	private Tracker easyTracker;
	private TrackerService() {
	}

	private static TrackerService instance;

	public static TrackerService getTrackerService() {
		if (instance == null) {
			instance = new TrackerService();
			
		}
		return instance;
	}
	
	private Tracker getGATracker(){
		if (easyTracker == null) {
			//easyTracker = NMBSApplication.getInstance().getGaTracker();
		}
		return easyTracker;
	}

	public void setContext(Context ctx) {
		if (ctx == null) {
			Log.e(TAG, "Context cannot be null");
		}else {
			gaContext = ctx;
		}	
	}
}
