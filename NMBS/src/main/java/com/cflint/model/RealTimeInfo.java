package com.cflint.model;

import java.io.Serializable;


import com.google.gson.annotations.SerializedName;

public class RealTimeInfo implements Serializable{
	private static final long serialVersionUID = -6835818985161074348L;
	
	@SerializedName("Id")
	private String id;
	
	@SerializedName("CallSuccessfull")
	private boolean callSuccessfull;
	
	@SerializedName("Connection")
	private RealTimeConnection connectionInRealTime;

	@SerializedName("RealTimeInfoTravelSegment")
	private RealTimeInfoTravelSegment realTimeInfoTravelSegment;

	public String getId() {
		return id;
	}

	public boolean isCallSuccessfull() {
		return callSuccessfull;
	}

	public RealTimeConnection getConnectionInRealTime() {
		return connectionInRealTime;
	}

	public RealTimeInfoTravelSegment getRealTimeInfoTravelSegment() {
		return realTimeInfoTravelSegment;
	}
	
	
	
}
