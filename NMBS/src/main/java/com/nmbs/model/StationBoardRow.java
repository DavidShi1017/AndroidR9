package com.nmbs.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class StationBoardRow implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("DateTime")
	private Date dateTime;
	
	@SerializedName("Delay")
	private double delay;
	
	@SerializedName("IsCancelled")
	private boolean isCancelled;
	
	
	@SerializedName("StationName")
	private String stationName;
	
	@SerializedName("Carrier")
	private String carrier;
	
	@SerializedName("TrainNr")
	private String trainNr;
	
	@SerializedName("Track")
	private String track;
	
	public String getTrack() {
		return track;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public double getDelay() {
		return delay;
	}

	public String getStationName() {
		return stationName;
	}

	public String getCarrier() {
		return carrier;
	}

	public String getTrainNr() {
		return trainNr;
	}
	public StationBoardRow(Date dateTime, double delay, String stationName, String carrier, String trainNr, String track){
		this.dateTime = dateTime;
		this.delay = delay;
		this.stationName = stationName;
		this.carrier = carrier;
		this.trainNr = trainNr;
		this.track = track;

	}

	public boolean isCancelled() {
		return isCancelled;
	}
	
	
}
