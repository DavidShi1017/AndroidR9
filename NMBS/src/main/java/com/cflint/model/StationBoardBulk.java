package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StationBoardBulk implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	@SerializedName("Id")
	private String id;
	
	@SerializedName("CallSuccessFul")
	private boolean callSuccessFul;
	
	@SerializedName("Delay")
	private double delay;
	
	@SerializedName("IsCancelled")
	private boolean isCancelled;

	public String getId() {
		return id;
	}

	public boolean isCallSuccessFul() {
		return callSuccessFul;
	}

	public double getDelay() {
		return delay;
	}

	public boolean isIsCancelled() {
		return isCancelled;
	}
	
	public StationBoardBulk(String id, boolean callSuccessFul, double delay, boolean isCancelled){
		this.id = id;
		this.callSuccessFul = callSuccessFul;
		this.delay = delay;
		this.isCancelled = isCancelled;
	}
	
	public String getDnrStr(){
		String dnr = "";
		if (id != null) {
			dnr = id.substring(0, id.indexOf("_"));
		}
		return dnr;
	}
	
	public String getTravelSegmentID(){
		String travelSegmentID = "";
		if (id != null) {
			travelSegmentID = id.substring(id.indexOf("_") + 1);
		}
		return travelSegmentID;
	}
}
