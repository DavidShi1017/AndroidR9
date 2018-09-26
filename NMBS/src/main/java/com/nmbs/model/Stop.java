package com.nmbs.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Stop implements Serializable{
	@SerializedName("StationName")
	
	private String stationName;
	@SerializedName("StationCode")
	private String stationCode;
	@SerializedName("DepartureDateTime")
	private Date departureDateTime;
	@SerializedName("ArrivalDateTime")
	private Date arrivalDateTime;
	private static final long serialVersionUID = 1L;
	public String getStationName() {
		return stationName;
	}
	public String getStationCode() {
		return stationCode;
	}
	public Date getDepartureDateTime() {
		return departureDateTime;
	}
	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public Stop(String stationName, String stationCode,Date departureDateTime,
			Date arrivalDateTime) {
		this.stationName = stationName;
		this.stationCode = stationCode;
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
		
	}
}
