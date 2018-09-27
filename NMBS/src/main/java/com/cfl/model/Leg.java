package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;




/**
 * The LegData is used to represent the different segments of a connection.
 * @author David.shi
 */
public class Leg implements Serializable{

	private static final long serialVersionUID = 1L;
	
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
	
	@SerializedName("TrainType")
	private String trainType;

	@SerializedName("TrainNr")
	private String trainNr;
	
	@SerializedName("IsNightTrain")
	private boolean isNightTrain;
    
	@SerializedName("DeparturePlatform")
	private String departurePlatform;

	@SerializedName("ArrivalPlatform")
	private String arrivalPlatform;
    
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
		return departurePlatform;
	}

	public String getArrivalPlatform() {
		return arrivalPlatform;
	}

	private String id;



	
	private String originnName;
	private String originStationCode;

	private String destinationStationCode;
	private Date departureDateTime;

	
	
	private String legStatus;
	private Date realTimeDepartureDateTime;
	private Date realTimeArrivalDateTime;
	private Date realTimeDepartureDelta ;
	private Date realTimeArrivalDelta ;
	
	
	
	@SerializedName("Stops")
	private List<Stop> stops = new ArrayList<Stop>();
	/**
	 * @return the hasWarning
	 */
	public boolean isHasWarning() {
		return hasWarning;
	}

	public void setHasWarning(boolean hasWarning) {
		this.hasWarning = hasWarning;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}
		
	/**
	 * @return the trainNr
	 */
	public String getTrainNr() {
		return trainNr;
	}
	/**
	 * @return the trainType
	 */
	public String getTrainType() {
		return trainType;
	}
	/**
	 * @return the isTrainLeg
	 */
	public boolean isTrainLeg() {
		return isTrainLeg;
	}
	public List<Stop> getStops() {
		return stops;
	}
	public Leg(String id, boolean hasWarning, String trainNr,
			String trainType, boolean isTrainLeg, String originnName,
			String originStationCode, String destinationName,
			String destinationStationCode, Date departureDateTime,
			Date arrivalDateTime, List<Stop> stops, String duration,
			List<TrainInfo> trainInfos, String legStatus, Date realTimeDepartureDateTime,
			Date realTimeArrivalDateTime, Date realTimeDepartureDelta, Date realTimeArrivalDelta) {
		this.hasWarning = hasWarning;
		this.duration = duration;
		this.trainNr = trainNr;
		this.trainType = trainType;
		this.isTrainLeg = isTrainLeg;
		this.originnName = originnName;
		this.originStationCode = originStationCode;
		this.destinationName = destinationName;
		this.destinationStationCode = destinationStationCode;
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
		this.stops = stops;
		this.setTrainInfos(trainInfos);
		this.id = id;
		this.legStatus = legStatus;
		this.realTimeDepartureDateTime = realTimeDepartureDateTime;
		this.realTimeArrivalDateTime = realTimeArrivalDateTime;
		this.realTimeDepartureDelta = realTimeDepartureDelta;
		this.realTimeArrivalDelta = realTimeArrivalDelta;
	}
	public String getOriginnName() {
		return originnName;
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

	public String getId() {
		return id;
	}

	public List<TrainInfo> getTrainInfos() {
		return trainInfos;
	}

	public void setTrainInfos(List<TrainInfo> trainInfos) {
		this.trainInfos = trainInfos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLegStatus() {
		return legStatus;
	}

	public void setLegStatus(String legStatus) {
		this.legStatus = legStatus;
	}

	public Date getRealTimeDepartureDateTime() {
		return realTimeDepartureDateTime;
	}

	public Date getRealTimeArrivalDateTime() {
		return realTimeArrivalDateTime;
	}

	public Date getRealTimeDepartureDelta() {
		return realTimeDepartureDelta;
	}

	public Date getRealTimeArrivalDelta() {
		return realTimeArrivalDelta;
	}

	public String getOriginRCode() {
		return originRCode;
	}

	public String getDestinationRCode() {
		return destinationRCode;
	}
}
