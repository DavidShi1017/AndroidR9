package com.cflint.model;

import com.google.gson.annotations.SerializedName;
import com.cflint.model.TravelRequest.TimePreference;

import java.io.Serializable;
import java.util.Date;

public class ScheduleQuery implements Serializable{


	private static final long serialVersionUID = 1L;
	@SerializedName("OriginStationRcode")
	private String originStationRcode;
	@SerializedName("DestinationStationRcode")
	private String destinationStationRcode;
	@SerializedName("ViaStationRcode")
	private String viaStationRcode;
	@SerializedName("TrainNr")
	private String trainNr;
	@SerializedName("DateTime")
	private Date dateTime;
	@SerializedName("TimePreference")
	private TimePreference timePreference;

	public ScheduleQuery(String originStationRcode, String destinationStationRcode, String viaStationRcode, String trainNr, Date dateTime, TimePreference timePreference){
		this.originStationRcode = originStationRcode;
		this.destinationStationRcode = destinationStationRcode;
		this.trainNr = trainNr;
		this.viaStationRcode = viaStationRcode;
		this.dateTime = dateTime;
		this.timePreference = timePreference;
	}


	public String getOriginStationRcode(){
		return this.originStationRcode;
	}

	public String getDestinationStationRcode(){
		return this.destinationStationRcode;
	}

	public String getViaStationRcode(){
		return this.viaStationRcode;
	}

	public String getTrainNr(){
		return this.trainNr;
	}

	public Date getDateTime(){
		return this.dateTime;
	}

	public TimePreference getTimePreference(){
		return this.timePreference;
	}

}
