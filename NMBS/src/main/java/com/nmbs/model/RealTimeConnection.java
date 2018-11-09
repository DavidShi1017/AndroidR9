package com.nmbs.model;


import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.nmbs.log.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Connection represents a train traveling from A to B leaving 
 * station A on a certain moment in time (departure
 */
public class RealTimeConnection implements Serializable{

	private static final long serialVersionUID = 1L;


	
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

	@SerializedName("TransferNotPossible")
	private boolean transferNotPossible;
	
	@SerializedName("ConnectionNotPossible")
	private boolean connectionNotPossible;
    
	@SerializedName("RealTimeDepartureDelta")
	private String realTimeDepartureDelta;
    
	@SerializedName("RealTimeArrivalDelta")
	private String realTimeArrivalDelta;
	
	@SerializedName("Legs")
	private List<RealTimeInfoLeg> realTimeInfoLegs = new ArrayList<RealTimeInfoLeg>();

	@SerializedName("UserMessages")
	private List<UserMessage> userMessages = new ArrayList<UserMessage>();

	@SerializedName("HafasMessages")
	private List<HafasMessage> hafasMessages = new ArrayList<HafasMessage>();
	
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


	
	public List<RealTimeInfoLeg> getRealTimeInfoLegs() {
		return realTimeInfoLegs;
	}
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


	
	public Date getArrivalTime(){
		//Date resultDate = new Date(this.getDeparture().getTime()+this.getDuration());
		return arrival;
	}
	public boolean isTransferNotPossible() {
		return transferNotPossible;
	}
	public boolean isConnectionNotPossible() {
		return connectionNotPossible;
	}

	public void setConnectionNotPossible(boolean connectionNotPossible) {
		this.connectionNotPossible = connectionNotPossible;
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
		//return realTimeDepartureDelta;
	}
	public String getRealTimeArrivalDelta() {
		String arrival = "";
		if(realTimeArrivalDelta != null && realTimeArrivalDelta.length() > 0){
			LogUtils.e("realTimeArrivalDelta", "realTimeArrivalDelta..." + realTimeArrivalDelta);
			arrival = realTimeArrivalDelta;
			arrival = arrival.substring(0, arrival.lastIndexOf(":"));
			if(arrival.indexOf("-") == -1){
				arrival = "+" + arrival;
			}
		}
		LogUtils.e("arrival", "arrival..." + arrival);
		return arrival;
		//return realTimeArrivalDelta;
	}

	public List<UserMessage> getUserMessages(){
		return this.userMessages;
	}
	public List<HafasMessage> getHafasMessages() {
		return hafasMessages;
	}

	public void setUserMessages(List<UserMessage> userMessages) {
		this.userMessages = userMessages;
	}

	public void setHafasMessages(List<HafasMessage> hafasMessages) {
		this.hafasMessages = hafasMessages;
	}

	public boolean hasLegStatus(){
		for (RealTimeInfoLeg leg: realTimeInfoLegs) {
			if(leg != null && leg.getLegStatus() != null && !leg.getLegStatus().isEmpty()){
				return true;
			}
		}
		return false;
	}
}
