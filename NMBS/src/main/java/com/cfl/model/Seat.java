package com.cfl.model;

import java.io.Serializable;
/**
 * 
 * this is seat model ,display offerlegItem and relation leg info in seat tab
 *
 */
public class Seat implements Serializable{
	private static final long serialVersionUID = 1L;
	private String originnName;
	private String destinationName;
	private String trainType;
	private String reservationType;
	private boolean reservationOptional;
	private boolean isTrainLeg;
	private boolean hasWarning;

	public boolean isTrainLeg() {
		return isTrainLeg;
	}
	public void setTrainLeg(boolean isTrainLeg) {
		this.isTrainLeg = isTrainLeg;
	}
	public boolean isHasWarning() {
		return hasWarning;
	}
	public void setHasWarning(boolean hasWarning) {
		this.hasWarning = hasWarning;
	}
	public boolean isReservationOptional() {
		return reservationOptional;
	}
	public void setReservationOptional(boolean reservationOptional) {
		this.reservationOptional = reservationOptional;
	}
	public String getOriginnName() {
		return originnName;
	}
	public void setOriginnName(String originnName) {
		this.originnName = originnName;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getTrainType() {
		return trainType;
	}
	public void setTrainType(String trainType) {
		this.trainType = trainType;
	}
	public String getReservationType() {
		return reservationType;
	}
	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}
}
