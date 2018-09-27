package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.cfl.model.SeatLocationForOD.Direction;

public class TravelSegment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("Id")
	private String id;
	
	@SerializedName("ParentId")
	private String ParentId;
	
	@SerializedName("PnrIds")
	private List<String> pnrIds = new ArrayList<String>();
	
	@SerializedName("Direction")
	private Direction direction;
	
	@SerializedName("ComfortClass")
	private String comfortClass;
	
	@SerializedName("Origin")
	private String origin;
	
	@SerializedName("OriginCode")	
	private String originCode;
	
	@SerializedName("Destination")
	private String destination;
	
	@SerializedName("DestinationCode")
	private String destinationCode;
	
	
	@SerializedName("DepartureDate")
	private Date departureDate;
	
	@SerializedName("DepartureTime")
	private Date departureTime;
	
	@SerializedName("ArrivalDate")
	private Date arrivalDate;
	
	@SerializedName("ArrivalTime")
	private Date arrivalTime;
	
	@SerializedName("ValidityStartDate")
	private Date validityStartDate;
	
	@SerializedName("ValidityEndDate")
	private Date validityEndDate;
	
	@SerializedName("TrainNr")
	private String trainNr;
	
	@SerializedName("TrainType")
	private String trainType;
			
	@SerializedName("HasReservation")
	private boolean HasReservation;
	
	@SerializedName("MainTariffId")
	private String mainTariffId;
	
	@SerializedName("PassengerTariffs")
	private List<PassengerTariff> passengerTariffs = new ArrayList<PassengerTariff>();
	
	@SerializedName("SeatLocations")
	private List<SeatLocation> seatLocations = new ArrayList<SeatLocation>();
	
	public String getId() {
		return id;
	}

	public Direction getDirection() {
		return direction;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public Date getDeparture() {
		return departureDate;
	}

	public Date getArrival() {
		return arrivalDate;
	}

	public String getTrainNr() {
		return trainNr;
	}

	public String getTrainType() {
		return trainType;
	}

	public String getMainTariffId() {
		return mainTariffId;
	}

	public List<PassengerTariff> getPassengerTariffs() {
		return passengerTariffs;
	}

	public boolean isHasReservation() {
		return HasReservation;
	}

	public String getParentId() {
		return ParentId;
	}

	public List<String> getPnrIds() {
		return pnrIds;
	}

	public String getComfortClass() {
		return comfortClass;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public Date getValidityStartDate() {
		return validityStartDate;
	}

	public Date getValidityEndDate() {
		return validityEndDate;
	}

	public List<SeatLocation> getSeatLocations() {
		return seatLocations;
	}

	public String getOriginCode() {
		return originCode;
	}

	public String getDestinationCode() {
		return destinationCode;
	}
	
	
}
