package com.cfl.model;

import java.io.Serializable;


/**
 * The TrainSelectionParameter contains all fields required for dossier creation 
 *@author: Tony
 */
public class TrainSelectionParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	private String departureOfferId;
	private boolean addOptionalReservationsForDeparture;
	private String returnOfferId;
	private boolean addOptionalReservationsForReturn;
	private String offerQueryId;
	
	
	public boolean isAddOptionalReservationsForDeparture() {
		return addOptionalReservationsForDeparture;
	}
	public boolean isAddOptionalReservationsForReturn() {
		return addOptionalReservationsForReturn;
	}
	public String getDepartureOfferId() {
		return departureOfferId;
	}
	public String getReturnOfferId() {
		return returnOfferId;
	}
	public String getOfferQueryId() {
		return offerQueryId;
	}
	public TrainSelectionParameter(String departureOfferId, boolean addOptionalReservationsForDeparture,
			String returnOfferId, boolean addOptionalReservationsForReturn, String offerQueryId) {
		super();
		this.departureOfferId = departureOfferId;
		this.addOptionalReservationsForDeparture = addOptionalReservationsForDeparture;
		this.returnOfferId = returnOfferId;
		this.addOptionalReservationsForReturn = addOptionalReservationsForReturn;
		this.offerQueryId = offerQueryId;
	}
	
}
