package com.cflint.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.cflint.model.TravelRequest.TimePreference;

public class StationBoard implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("ID")
	private String id;

	@Expose
	@SerializedName("StationRCode")
	private String stationRCode;

	@Expose
	@SerializedName("DateTime")
	private Date dateTime;

	@Expose
	@SerializedName("TimePreference")
	private TimePreference timePreference;

	@Expose
	@SerializedName("TrainCategory")
	private String trainCategory;

	@Expose
	@SerializedName("TrainNumber")
	private String trainNumber;

	private String originStationName;

	private String destinationStationName;

	private String dnr;

	private Date sortDate;

	private String type;

	private String travelSegmentID;

	private boolean callSuccessFul;

	private String delay;

	private boolean isCancelled;

	private String parentId;

	public String getId() {
		return id;
	}

	public String getStationRCode() {
		return stationRCode;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public TimePreference getTimePreference() {
		return timePreference;
	}

	public String getTrainCategory() {
		return trainCategory;
	}

	public String getTrainNumber() {
		return trainNumber;
	}

	public String getType() {
		return type;
	}

	public Date getSortDate() {
		return sortDate;
	}

	public String getDnrStr() {

		return dnr;
	}

	public String getOriginStationName() {
		return originStationName;
	}

	public String getDestinationStationName() {
		return destinationStationName;
	}

	public String getTravelSegmentID() {

		return travelSegmentID;
	}

	public boolean isCallSuccessFul() {
		return callSuccessFul;
	}

	public String getDelay() {
		return delay;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public String getParentId() {
		return parentId;
	}

	public StationBoard(String id, String stationRCode, Date dateTime,
			TimePreference timePreference, String trainCategory,
			String trainNumber, String type, Date sortDate, String dnr,
			String originStationName, String destinationStationName,
			boolean callSuccessFul, String delay, boolean isCancelled,
			String parentId, String travelSegmentID) {
		this.id = id;
		this.stationRCode = stationRCode;
		this.dateTime = dateTime;
		this.timePreference = timePreference;
		this.trainCategory = trainCategory;
		this.trainNumber = trainNumber;
		this.type = type;
		this.sortDate = sortDate;
		this.dnr = dnr;
		this.originStationName = originStationName;
		this.destinationStationName = destinationStationName;
		this.callSuccessFul = callSuccessFul;
		this.delay = delay;
		this.isCancelled = isCancelled;
		this.parentId = parentId;
		this.travelSegmentID = travelSegmentID;
	}
}
