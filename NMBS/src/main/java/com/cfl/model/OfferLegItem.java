package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OfferLegItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String legId;
	private String reservationType;
	private List<SeatingPreference> seatingPreferences = new ArrayList<SeatingPreference>();
	
	
	/**
	 * @return the seatingPreferencesData
	 */
	public List<SeatingPreference> getSeatingPreferencesData() {
		return seatingPreferences;
	}
	
	/**
	 * @param object
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addSeatingPreferencesData(SeatingPreference object) {
		return seatingPreferences.add(object);
	}

	public String getLegId() {
		return legId;
	}

	public String getReservationType() {
		return reservationType;
	}
	
	public OfferLegItem(String legId, String reservationType, List<SeatingPreference> seatingPreferences){
		this.legId = legId;
		this.reservationType = reservationType;
		this.seatingPreferences = seatingPreferences;
	}
}
