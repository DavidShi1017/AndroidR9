package com.nmbs.model;

import java.io.Serializable;

import com.nmbs.model.OfferQuery.TravelType;

/**
 * To get earlier or later trains for a specific offer query
 *@author:Alice
 */
public class AdditionalConnectionsParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	public enum Direction{
		FORWARD,
		BACKWARD
	}
	String travelId;
	Direction direction;
	TravelType travelType;
	boolean isDeparture;
	
	public String getTravelId() {
		return travelId;
	}
	public void setTravelId(String travelId) {
		this.travelId = travelId;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public TravelType getTravelType() {
		return travelType;
	}
	public void setTravelType(TravelType travelType) {
		this.travelType = travelType;
	}
	public boolean isDeparture() {
		return isDeparture;
	}
	public void setDeparture(boolean isDeparture) {
		this.isDeparture = isDeparture;
	}
	
	
}
