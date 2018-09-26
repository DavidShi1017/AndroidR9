package com.nmbs.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Departure or Return date and departure or arrival.
 *@author:Alice
 */
public class TravelRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public enum TimePreference{
		DEPARTURE,
		ARRIVAL
	}
	
	Date dateTime;
	TimePreference timePreference;
	
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public TimePreference getTimePreference() {
		return timePreference;
	}
	public void setTimePreference(TimePreference timePreference) {
		this.timePreference = timePreference;
	}
	
}
