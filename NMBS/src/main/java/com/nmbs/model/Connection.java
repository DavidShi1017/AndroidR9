package com.nmbs.model;


import android.content.Context;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.nmbs.R;

/**
 * The Connection represents a train traveling from A to B leaving 
 * station A on a certain moment in time (departure
 */
public class Connection implements Serializable{

	private static final long serialVersionUID = 1L;

	@SerializedName("ConnectionId")
	private String connectionId;
	
	@SerializedName("OriginStationRcode")
	private String originStationRcode;
	
	@SerializedName("OriginStationName")
	private String originStationName;

	@SerializedName("DestinationStationRcode")
	private String destinationStationRcode;
	
	@SerializedName("DestinationStationName")
	private String destinationStationName;

	@SerializedName("Departure")
	private Date departure;
	
	@SerializedName("Arrival")
	private Date arrival;
	
	@SerializedName("Duration")
	private Date duration;
	
	@SerializedName("NumberOfTransfers")
	private int numberOfTransfers;
	
	@SerializedName("ReconCtx")
	private String reconCtx;

	@SerializedName("Legs")
	private List<Leg> legs;
	
	
	
	
	
	

	public String getConnectionId() {
		return connectionId;
	}
	public String getOriginStationRcode() {
		return originStationRcode;
	}
	public String getOriginStationName() {
		return originStationName;
	}
	public String getDestinationStationRcode() {
		return destinationStationRcode;
	}
	public String getDestinationStationName() {
		return destinationStationName;
	}
	public Date getArrival() {
		return arrival;
	}
	public String getReconCtx() {
		return reconCtx;
	}
	public List<Offer> getOffers() {
		return offers;
	}
	public List<Leg> getLegs() {
		return legs;
	}
	private List<Offer> offers = new ArrayList<Offer>();
	//private List<Leg> legs = new ArrayList<Leg>();
	

	private Offer selectedOffer;
	private SeatingPreference selectedSeating;
	
	private List<HafasMessage> hafasMessages = new ArrayList<HafasMessage>();
	
	/**
	 * @return the departure
	 */
	public Date getDeparture() {
		return departure;
	}
	/**
	 * @return the duration
	 */
	public Date getDuration() {
		return duration;
	}
	/**
	 * @return the numberOfTransfers
	 */
	public int getNumberOfTransfers() {
		return numberOfTransfers;
	}
	public String getNumberOfTransfers(Context context){
		String str = "";
		if(numberOfTransfers > 1){
			str = numberOfTransfers + " " + context.getString(R.string.general_transfers);
		}else if(numberOfTransfers == 0){
			str = context.getString(R.string.general_direct_train);
		}else{
			str = numberOfTransfers + " " + context.getString(R.string.general_transfer);
		}
		return str;
	}
	/**
	 * @return the offerData
	 */
	public List<Offer> getOfferData() {
		
		return offers;
	}
	/**
	 * @return the legData
	 */
	public List<Leg> getLegData() {
		return legs;
	}

	
	
	/**
	 * @param object
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addLegData(Leg object) {
		return legs.add(object);
	}
	/**
	 * @param object
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addOfferData(Offer object) {
		return offers.add(object);
	}

	public Connection(Date departure, Date arrival, Date duration, int numberOfTransfers, List<Offer> offers, List<Leg> legs, List<HafasMessage> hafasMessages) {
		this.departure = departure;
		this.arrival = arrival;
		this.duration = duration;
		this.numberOfTransfers = numberOfTransfers;
		this.offers = offers;
		this.legs = legs;
		this.hafasMessages = hafasMessages;
		
	}
	public Offer getSelectedOffer() {
		return selectedOffer;
	}
	public void setSelectedOffer(Offer selectedOffer) {
		this.selectedOffer = selectedOffer;
	}
	public SeatingPreference getSelectedSeating() {
		return selectedSeating;
	}
	public void setSelectedSeating(SeatingPreference selectedSeating) {
		this.selectedSeating = selectedSeating;
	}
	
	public Date getArrivalTime(){
		//Date resultDate = new Date(this.getDeparture().getTime()+this.getDuration());
		return arrival;
	}
	public List<HafasMessage> getHafasMessages() {
		return hafasMessages;
	}

	public boolean hasWarning(){
		for (Leg leg: legs) {
			if(leg != null && leg.isHasWarning()){
				return true;
			}
		}
		return false;
	}

	public boolean hasLegInSameZone(List<DossierTravelSegment> dossierTravelSegments, Connection connection){

		if(connection != null){
			List<Leg> legs = connection.getLegs();
			for (DossierTravelSegment dossierTravelSegment: dossierTravelSegments) {
				if(dossierTravelSegment != null && dossierTravelSegment.getConnectionId() != null && dossierTravelSegment.getConnectionId().equalsIgnoreCase(connection.getConnectionId())){
					if(dossierTravelSegment.getLegsInSameZone() != null && dossierTravelSegment.getLegsInSameZone().size() > 0){
						for(LegsInSameZone legsInSameZone : dossierTravelSegment.getLegsInSameZone()){
							for(Leg leg : legs){
								if(legsInSameZone != null && leg != null){
									if(legsInSameZone.getOriginRcode() != null && leg.getOriginRCode() != null && legsInSameZone.getOriginRcode().equalsIgnoreCase(leg.getOriginRCode())){
										if(legsInSameZone.getDestinationRcode() != null && leg.getDestinationRCode() != null && legsInSameZone.getDestinationRcode().equalsIgnoreCase(leg.getDestinationRCode())){
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public String getLegInSameZoneText(List<DossierTravelSegment> dossierTravelSegments){
		String text = "";
		for (DossierTravelSegment dossierTravelSegment: dossierTravelSegments) {
			if(dossierTravelSegment != null && dossierTravelSegment.getConnectionId() != null && dossierTravelSegment.getConnectionId().equalsIgnoreCase(connectionId)){
				if(dossierTravelSegment.getLegsInSameZone() != null && dossierTravelSegment.getLegsInSameZone().size() > 0){
					for (LegsInSameZone legsInSameZone : dossierTravelSegment.getLegsInSameZone()){
						if(!text.contains(legsInSameZone.getOriginName()) || !text.contains(legsInSameZone.getDestinationName())){
							text += legsInSameZone.getOriginName() + " - " + legsInSameZone.getDestinationName() + "\n";
						}
					}
				}
			}
		}
		return text;
	}

	public List<Leg> getWarningLegs(){
		List<Leg> warningLegs = new ArrayList<>();
		for (Leg leg: legs) {
			if(leg != null && leg.isHasWarning()){
				warningLegs.add(leg);
			}
		}
		return warningLegs;
	}
	public String getWarningLegsText(){
		String text = "";
		for (Leg leg: legs) {
			if(leg != null && leg.isHasWarning()){
				text += leg.getOriginName() + " - " + leg.getDestinationName() + "\n";
			}
		}
		return text;
	}
}
