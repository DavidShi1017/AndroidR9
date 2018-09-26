package com.nmbs.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class TrainLegData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("Id")
	private String id;
	@SerializedName("HasWarning")
	private String hasWarning;
	@SerializedName("WarningMessageKey")
	private String warningMessageKey;
	@SerializedName("TrainType")
	private String trainType;
	@SerializedName("OriginName")
	private String originName;
	@SerializedName("OriginStationCode")
	private String originStationCode;
	@SerializedName("DestinationName")
	private String destinationName;
	@SerializedName("DestinationStationCode")
	private String destinationStationCode;
	@SerializedName("DepartureDateTime")
	private Date departureDateTime;
	@SerializedName("ArrivalDateTime")
	private Date arrivalDateTime;

	public TrainLegData(String id, String hasWarning, String warningMessageKey,
			String trainType, String originName, String originStationCode,
			String destinationName, String destinationStationCode,
			Date departureDateTime, Date arrivalDateTime) {
		super();
		this.id = id;
		this.hasWarning = hasWarning;
		this.warningMessageKey = warningMessageKey;
		this.trainType = trainType;
		this.originName = originName;
		this.originStationCode = originStationCode;
		this.destinationName = destinationName;
		this.destinationStationCode = destinationStationCode;
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
	}

	public String getId() {
		return id;
	}

	public String getHasWarning() {
		return hasWarning;
	}

	public String getWarningMessageKey() {
		return warningMessageKey;
	}

	public String getTrainType() {
		return trainType;
	}

	public String getOriginName() {
		return originName;
	}

	public String getOriginStationCode() {
		return originStationCode;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public String getDestinationStationCode() {
		return destinationStationCode;
	}

	public Date getDepartureDateTime() {
		return departureDateTime;
	}

	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

}
