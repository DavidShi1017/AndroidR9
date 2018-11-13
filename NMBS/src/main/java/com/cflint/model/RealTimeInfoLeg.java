package com.cflint.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




/**
 * The LegData is used to represent the different segments of a connection.
 * @author David.shi
 */
public class RealTimeInfoLeg implements Serializable{

	private static final long serialVersionUID = 1L;
	

	@SerializedName("TrainNr")
	private String trainNr;
	
	@SerializedName("TrainType")
	private String trainType;
	
	@SerializedName("IsTrainLeg")
	private boolean isTrainLeg;
	
	@SerializedName("HasWarning")
	private boolean hasWarning;
	
	@SerializedName("OriginName")
	private String originName;
	
	@SerializedName("DestinationName")
	private String destinationName;

	@SerializedName("OriginRCode")
	private String originRCode;
	@SerializedName("DestinationRCode ")
	private String destinationRCode;
	
	@SerializedName("DepartureDateTime")
	private Date dastUpdated;
	
	@SerializedName("ArrivalDateTime")
	private Date arrivalDateTime;
     
	@SerializedName("Duration")
	private String duration;
	
	@SerializedName("LegStatus")
	private String legStatus;

	
	@SerializedName("RealTimeDepartureDelta")
	private String realTimeDepartureDelta;
	
	@SerializedName("RealTimeArrivalDelta")
	private String realTimeArrivalDelta;
	
    
	@SerializedName("DeparturePlatform")
	private String departurePlatform;

	@SerializedName("ArrivalPlatform")
	private String arrivalPlatform;
	
	@SerializedName("DeparturePlatformChanged")
	private boolean departurePlatformChanged;

	@SerializedName("ArrivalPlatformChanged")
	private boolean arrivalPlatformChanged;

	@SerializedName("FailureAtOrigin")
	private boolean failureAtOrigin;

	@SerializedName("FailureAtDestination")
	private boolean failureAtDestination;
	
	@SerializedName("IsNightTrain")
	private boolean isNightTrain;
    
	@SerializedName("TrainInfos")
	private List<TrainInfo> trainInfos = new ArrayList<TrainInfo>();


	

	
	public String getOriginName() {
		return originName;
	}

	public Date getDastUpdated() {
		return dastUpdated;
	}

	public boolean isNightTrain() {
		return isNightTrain;
	}

	public String getDeparturePlatform() {
		if(departurePlatform != null && departurePlatform.length() == 8){
			departurePlatform = departurePlatform.substring(0, departurePlatform.lastIndexOf(":"));
		}
		return departurePlatform;
	}

	public String getArrivalPlatform() {
		if(arrivalPlatform != null && arrivalPlatform.length() == 8){
			arrivalPlatform = arrivalPlatform.substring(0, arrivalPlatform.lastIndexOf(":"));
		}
		return arrivalPlatform;
	}

	public String getTrainNr() {
		return trainNr;
	}

	public String getTrainType() {
		return trainType;
	}

	public boolean isTrainLeg() {
		return isTrainLeg;
	}

	public boolean isHasWarning() {
		return hasWarning;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public String getDuration() {
		return duration;
	}

	public String getLegStatus() {
		return legStatus;
	}

	public String getRealTimeDepartureDelta() {
		String departure = "";
		if(realTimeDepartureDelta != null && realTimeDepartureDelta.length() > 0){
			departure = realTimeDepartureDelta;
			departure = departure.substring(0, departure.lastIndexOf(":"));
			if(departure.indexOf("-") == -1){
				departure = "+" + departure;
			}
		}
		return departure;
	}

	public String getRealTimeArrivalDelta() {
		String arrival = "";
		if(realTimeArrivalDelta != null && realTimeArrivalDelta.length() > 0){
			Log.e("realTimeArrivalDelta", "realTimeArrivalDelta..." + realTimeArrivalDelta);
			arrival = realTimeArrivalDelta;
			arrival = arrival.substring(0, arrival.lastIndexOf(":"));
			if(arrival.indexOf("-") == -1){
				arrival = "+" + arrival;
			}
		}
		return arrival;
	}

	public boolean isDeparturePlatformChanged() {
		return departurePlatformChanged;
	}

	public boolean isArrivalPlatformChanged() {
		return arrivalPlatformChanged;
	}

	public boolean isFailureAtOrigin() {
		return failureAtOrigin;
	}

	public boolean isFailureAtDestination() {
		return failureAtDestination;
	}

	public List<TrainInfo> getTrainInfos() {
		return trainInfos;
	}


	public String getOriginRCode() {
		return originRCode;
	}

	public String getDestinationRCode() {
		return destinationRCode;
	}
}
