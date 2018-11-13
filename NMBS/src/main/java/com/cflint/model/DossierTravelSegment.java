package com.cflint.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DossierTravelSegment implements Serializable{
	private static final long serialVersionUID = 1L;

	@SerializedName("TravelSegmentId")
	private String travelSegmentId;
	
	@SerializedName("ConnectionId")
	private String connectionId;
	
	@SerializedName("SegmentType")
	private String segmentType;
    
	@SerializedName("IsNVSAdmission")
	private boolean isNVSAdmission;
	
	@SerializedName("ParentTravelSegmentId")
	private String parentTravelSegmentId;
	
	@SerializedName("HasABS")
	private boolean hasABS;
	
	@SerializedName("HasADS")
	private boolean hasADS;
    
	@SerializedName("OriginStationRcode")
	private String originStationRcode;
	
	@SerializedName("OriginStationName")
	private String originStationName;
    
	@SerializedName("OriginStationIsVirtualText")
	private String originStationIsVirtualText;
	
	@SerializedName("DestinationStationRcode")
	private String destinationStationRcode;
	
	@SerializedName("DestinationStationName")
	private String destinationStationName;
    
	@SerializedName("DestinationStationIsVirtualText")
	private String destinationStationIsVirtualText;
    
	@SerializedName("LegsInSameZone")
	private List<LegsInSameZone> legsInSameZone;

	@SerializedName("HasOverbooking")
	private boolean hasOverbooking;
	
	@SerializedName("ComfortClass")
	private int comfortClass;
    
	@SerializedName("TrainType")
	private String trainType;
	
	@SerializedName("TariffGroupText")
	private String tariffGroupText;
    
	@SerializedName("TrainNumber")
	private String trainNumber;
	
	@SerializedName("DepartureDate")
	private Date departureDate;

	@SerializedName("DepartureTime")
	private Date departureTime;
	
	@SerializedName("ArrivalDate")
	private Date arrivalDate;
	
	@SerializedName("ArrivalTime")
	private Date arrivalTime;
    
	@SerializedName("IndicativeDepartureTime")
	private Date indicativeDepartureTime;
		
	@SerializedName("ValidityEndDate")
	private Date validityEndDate;
    
	@SerializedName("SegmentState")
	private String segmentState;
    
	@SerializedName("InventoryDossierNumbers")
	private String inventoryDossierNumbers[];
	
	@SerializedName("SegmentPassengers")
	private List<Passenger> segmentPassengers;
	
	@SerializedName("SeatLocations")
	private List<SeatLocation> seatLocations;
	
	@SerializedName("Tickets")
	private List<Ticket> tickets;
	
	@SerializedName("Direction")
	private String direction;

	@SerializedName("AccommodationType")
	private String accommodationType;


	public String getTravelSegmentId() {
		return travelSegmentId;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public String getSegmentType() {
		return segmentType;
	}

	public boolean isNVSAdmission() {
		return isNVSAdmission;
	}

	public String getParentTravelSegmentId() {
		return parentTravelSegmentId;
	}

	public boolean isHasABS() {
		return hasABS;
	}

	public boolean isHasADS() {
		return hasADS;
	}

	public String getOriginStationRcode() {
		return originStationRcode;
	}

	public String getOriginStationName() {
		return originStationName;
	}

	public String getOriginStationIsVirtualText() {
		return originStationIsVirtualText;
	}

	public String getDestinationStationRcode() {
		return destinationStationRcode;
	}

	public String getDestinationStationName() {
		return destinationStationName;
	}

	public String getDestinationStationIsVirtualText() {
		return destinationStationIsVirtualText;
	}

	public List<LegsInSameZone> getLegsInSameZone() {
		return legsInSameZone;
	}

	public String getAccommodationType() {
		return accommodationType;
	}

	public boolean isHasOverbooking() {
		return hasOverbooking;
	}

	public int getComfortClass() {
		return comfortClass;
	}

	public String getTrainType() {
		return trainType;
	}

	public String getTariffGroupText() {
		return tariffGroupText;
	}

	public String getTrainNumber() {
		if(trainNumber == null){
			return "";
		}
		return trainNumber;
	}

	public Date getDepartureDate() {
		return departureDate;
	}


	public Date getSortedDate (List<DossierTravelSegment> dossierTravelSegments){
		Calendar calendarDeparture = Calendar.getInstance();
		calendarDeparture.setTime(departureDate);
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.set(Calendar.YEAR, calendarDeparture.get(Calendar.YEAR));
		calendarDate.set(Calendar.MONTH, calendarDeparture.get(Calendar.MONTH));
		calendarDate.set(Calendar.DAY_OF_MONTH, calendarDeparture.get(Calendar.DAY_OF_MONTH));
		Calendar calendarTime = Calendar.getInstance();
		if(departureTime != null){
			if(departureTime != null){
				calendarTime.setTime(departureTime);
				calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
				calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
			}
		}else{
			if(indicativeDepartureTime != null){
				if(indicativeDepartureTime != null){
					calendarTime.setTime(indicativeDepartureTime);
					calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
					calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
				}
				//calendar.set(departureDate.getYear(), departureDate.getMonth(), departureDate.getDay(), indicativeDepartureTime.getHours(), indicativeDepartureTime.getMinutes());
			}else{
				DossierTravelSegment dossierChildTravelSegment = null;
				if(getParentTravelSegmentId() == null || getParentTravelSegmentId().isEmpty()){
					for(DossierTravelSegment dossierTravelSegment : dossierTravelSegments){
						if(getTravelSegmentId().equalsIgnoreCase(dossierTravelSegment.parentTravelSegmentId)){
							dossierChildTravelSegment = dossierTravelSegment;
							break;
						}
					}
				}
				if(dossierChildTravelSegment != null && dossierChildTravelSegment.getDepartureTime() != null){
					calendarTime.setTime(dossierChildTravelSegment.getDepartureTime());
					calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
					calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

					//calendar.set(departureDate.getYear(), departureDate.getMonth(), departureDate.getDay(), dossierChildTravelSegment.getDepartureTime().getHours(), dossierChildTravelSegment.getDepartureTime().getMinutes());
				}else{
					if(departureTime != null){
						calendarTime.setTime(departureTime);
						calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
						calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
					}
					//calendar.set(departureDate.getYear(), departureDate.getMonth(), departureDate.getDay(), departureDate.getHours(), departureDate.getMinutes());
				}
			}

		}
		//Log.d("SortedDate", "SortedDate===" + calendarDate.getTime());
		return calendarDate.getTime();
	}

	public Date getDepartureDateTime (){
		Calendar calendarDate = Calendar.getInstance();
		Calendar calendarTime = Calendar.getInstance();
		calendarDate.setTime(departureDate);
		if(departureTime != null){
			calendarTime.setTime(departureTime);
			calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
			calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
		}
		Log.d("getDateTime", "getDateTime===" + calendarDate.getTime());
		return calendarDate.getTime();
	}
	public Date getArrivalDateTime (){
		Calendar calendarDate = Calendar.getInstance();
		Calendar calendarTime = Calendar.getInstance();
		if(arrivalDate == null){
			return null;
		}
		calendarDate.setTime(arrivalDate);
		if(arrivalTime != null){
			calendarTime.setTime(arrivalTime);
			calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
			calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
		}
		Log.d("getDateTime", "getDateTime===" + calendarDate.getTime());
		return calendarDate.getTime();
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

	public Date getIndicativeDepartureTime() {
		return indicativeDepartureTime;
	}

	public Date getValidityEndDate() {
		return validityEndDate;
	}

	public String getSegmentState() {
		return segmentState;
	}

	public String[] getInventoryDossierNumbers() {
		return inventoryDossierNumbers;
	}

	public List<Passenger> getSegmentPassengers() {
		return segmentPassengers;
	}

	private String getPassegersName(String id){
		String name = "";
		for(int i = 0; i < segmentPassengers.size(); i++){
			Passenger passenger = segmentPassengers.get(i);
			if(passenger != null){
				Log.e("Passengers", "id is:::" + id);
				Log.e("Passengers", "passenger id is:::" + passenger.getId());
				if(id.equalsIgnoreCase(passenger.getId())){
					Log.e("Passengers", "passenger id is equals....");
					name = passenger.getFirstName() + " " + passenger.getLastName();
				}
			}
		}
		Log.e("Passengers", "name is:::" + name);
		return name;
	}

	public String [] getPassegersName(Ticket ticket){
		if(ticket != null){
			String [] name = new String [ticket.getSegmentPassengerIds().length];
			for(int i = 0; i < ticket.getSegmentPassengerIds().length; i++){
				String id = ticket.getSegmentPassengerIds()[i];
				if(id != null){
					name[i] = getPassegersName(id);
				}
			}
			return name;
		}
		return  null;
	}

	public List<SeatLocation> getSeatLocations() {
		return seatLocations;
	}
	public List<SeatLocation> getSeatLocations(Ticket ticket) {
		List<SeatLocation> seatLocations = new ArrayList<>();
		for(SeatLocation seatLocation : this.seatLocations){
			for(String id : ticket.getSegmentPassengerIds()){
				if(seatLocation != null && seatLocation.getSegmentPassengerId() != null && seatLocation.getSegmentPassengerId().equalsIgnoreCase(id)){
					seatLocations.add(seatLocation);
				}
			}
			if(seatLocation != null && seatLocation.getSegmentPassengerId() != null && seatLocation.getSegmentPassengerId().isEmpty()){
				seatLocations.add(seatLocation);
			}
		}

		return seatLocations;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public String getDirection() {
		return direction;
	}
	
	public SeatLocation getSeatLocation(String id){
		for(SeatLocation seatLocation : seatLocations){
			if (seatLocation != null && seatLocation.getSegmentPassengerId().equals(id)){
				return seatLocation;
			}
		}
		return null;
	}
	public SeatLocation getSeatLocation(String[] ids){
		String id = "";
		if(ids != null && ids.length > 0){
			id = ids[0];
		}

		for(SeatLocation seatLocation : seatLocations){
			if (seatLocation != null && seatLocation.getSegmentPassengerId().equals(id)){
				return seatLocation;
			}
		}
		return null;
	}


}
