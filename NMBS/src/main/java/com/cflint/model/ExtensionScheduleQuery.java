package com.cflint.model;

import com.cflint.model.TravelRequest.TimePreference;

import java.util.Date;

public class ExtensionScheduleQuery extends ScheduleQuery{

	private String originName;
	private String destinationName;
	private String viaStationName;

	public ExtensionScheduleQuery(String originStationRcode, String destinationStationRcode, String viaStationRcode, String trainNr, Date dateTime, TimePreference timePreference, String originName, String destinationName,String viaStationName){
		super(originStationRcode,destinationStationRcode,viaStationRcode,trainNr,dateTime,timePreference);
		this.originName = originName;
		this.destinationName = destinationName;
		this.viaStationName = viaStationName;
	}

	public String getViaStationName(){
		return this.viaStationName;
	}

	public void setOriginName(String originName){
		this.originName = originName;
	}

	public void setDestinationName(String destinationName){
		this.destinationName = destinationName;
	}

	public String getOriginName(){
		return this.originName;
	}

	public String getDestinationName(){
		return this.destinationName;
	}
}
