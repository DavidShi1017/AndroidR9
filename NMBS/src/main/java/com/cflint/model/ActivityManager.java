package com.cflint.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class ActivityManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Activity> activityList = new LinkedList<Activity>();

	private static ActivityManager instance;

	private ActivityManager() {
	}

	public static ActivityManager getInstance() {
		if (null == instance) {
			instance = new ActivityManager();
		}
		return instance;
	}
	
	public void removeFirstOne(){
		if (activityList.size() > 0) {
			activityList.remove(0);
		}
	}

	public void addActivity(Activity activity) {
		if (!activityList.contains(activity)) {
			activityList.add(activity);
		}		
	}

	public void finishActivities() {
		for (Activity activity : activityList) {
			activity.finish();
		}		
	}

}
