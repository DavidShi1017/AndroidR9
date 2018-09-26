package com.nmbs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.nmbs.model.TravelRequest.TimePreference;

public class StationBoardQuery implements Serializable{


	private static final long serialVersionUID = 1L;
	public enum StationBoardFeedbackTypes {
		CORRECT,WARNINGSTATION,WARNINGDATETIME,WARNINGTRAINNR,WARNINGTRAINCATEGORY
	}
	@SerializedName("StationRCode")
	private String stationRCode;
	
	@SerializedName("DateTime")
	private Date dateTime;
	
	@SerializedName("TimePreference")
	private TimePreference timePreference;
	
	@SerializedName("Trains")
	private List<Train> trains = new ArrayList<Train>();

	String name;
	String synoniem;

	public String getStationRCode() {
		return stationRCode;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public TimePreference getTimePreference() {
		return timePreference;
	}

	public List<Train> getTrains() {
		return trains;
	}
	
	public StationBoardQuery(String stationRCode, Date dateTime, TimePreference timePreference, List<Train> trains){
		this.stationRCode = stationRCode;
		this.dateTime = dateTime;
		this.timePreference = timePreference;
		this.trains = trains;
	}

	public String getSynoniem() {
		return synoniem;
	}

	public void setSynoniem(String synoniem) {
		this.synoniem = synoniem;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public StationBoardFeedbackTypes validate(){
		if(isEmptyText(this.stationRCode)){
			return StationBoardFeedbackTypes.WARNINGSTATION;
		}
		
		if(isEmptyDate()){
			return StationBoardFeedbackTypes.WARNINGDATETIME;
		}
/*		if(isEmptyText(this.trains.get(0).getCategory())
				&&isEmptyText(this.trains.get(0).getNumber())){
			trains.clear();
		}
		if (trains != null && trains.size() != 0) {
			if (!isEmptyText(this.trains.get(0).getCategory())
					|| !isEmptyText(this.trains.get(0).getNumber())) {
				if (isEmptyText(this.trains.get(0).getCategory())) {
					return StationBoardFeedbackTypes.WARNINGTRAINCATEGORY;
				}
				if (isEmptyText(this.trains.get(0).getNumber())) {
					return StationBoardFeedbackTypes.WARNINGTRAINNR;
				}
			}
		}*/
		return StationBoardFeedbackTypes.CORRECT;
	}
	
	private boolean isEmptyText(String str) {

		if ("".equals(str) || str == null) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isEmptyDate() {
		if (this.getDateTime() == null) {
			return true;
		}
		return false;
	}

}
