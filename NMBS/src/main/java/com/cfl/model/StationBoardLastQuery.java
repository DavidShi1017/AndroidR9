package com.cfl.model;

import com.google.gson.annotations.SerializedName;
import com.cfl.model.TravelRequest.TimePreference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StationBoardLastQuery implements Serializable{


	private static final long serialVersionUID = 1L;

	@SerializedName("StationRCode")
	private String stationRCode;

	@SerializedName("DateTime")
	private Date dateTime;

	@SerializedName("TimePreference")
	private TimePreference timePreference;

	@SerializedName("Trains")
	private List<Train> trains = new ArrayList<Train>();
	@SerializedName("StationName")
	String name;

	@SerializedName("StationSynoniem")
	String synoniem;

	public String getStationRCode() {
		return stationRCode;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public TimePreference getTimePreference() {
		return timePreference;
	}

	public List<Train> getTrains() {
		return trains;
	}

	public StationBoardLastQuery(String stationRCode, Date dateTime, TimePreference timePreference, List<Train> trains){
		this.stationRCode = stationRCode;
		this.dateTime = dateTime;
		this.timePreference = timePreference;
		this.trains = trains;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getSynoniem() {
		return synoniem;
	}

	public void setSynoniem(String synoniem) {
		this.synoniem = synoniem;
	}
}
