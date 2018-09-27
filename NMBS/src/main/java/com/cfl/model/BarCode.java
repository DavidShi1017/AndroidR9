package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BarCode implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("RestId")
	private String restId;
	
	@SerializedName("PassengerIds")
	private List<String> passengerIds = new ArrayList<String>();
	
	@SerializedName("TravelSegmentIds")
	private List<String> travelSegmentIds = new ArrayList<String>();

	public String getRestId() {
		return restId;
	}

	public List<String> getPassengerIds() {
		return passengerIds;
	}

	public List<String> getTravelSegmentIds() {
		return travelSegmentIds;
	}
}
