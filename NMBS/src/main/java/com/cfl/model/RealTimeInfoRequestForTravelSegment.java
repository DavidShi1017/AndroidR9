package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class RealTimeInfoRequestForTravelSegment implements Serializable{
	private static final long serialVersionUID = -6835818985161074348L;

	@SerializedName("Id")
	private String id;

	@SerializedName("Context")
	private String context;

	@SerializedName("DepartureDate")
	private String departureDate;

	@SerializedName("DepartureTime")
	private String departureTime;

	@SerializedName("TrainNumber")
	private String trainNumber;

	@SerializedName("OriginStationRcode")
	private String originStationRcode;

	@SerializedName("DestinationStationRcode")
	private String destinationStationRcode;

	public RealTimeInfoRequestForTravelSegment(String id, String context, String departureDate, String departureTime, String trainNumber, String originStationRcode,String destinationStationRcode){
		this.id = id;
		this.context = context;
		this.trainNumber = trainNumber;
		this.departureDate = departureDate;
		this.departureTime = departureTime;
		this.originStationRcode = originStationRcode;
		this.destinationStationRcode = destinationStationRcode;
	}

	public String getId(){
		return this.id;
	}

	public String getContext(){
		return this.context;
	}

	public String getDepartureDate(){
		return this.departureDate;
	}

	public String getDepartureTime(){
		return this.departureTime;
	}

	public String getTrainNumber(){
		return this.trainNumber;
	}

	public String getOriginStationRcode(){
		return this.originStationRcode;
	}

	public String getDestinationStationRcode(){
		return this.destinationStationRcode;
	}
}
