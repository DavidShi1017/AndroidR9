package com.cfl.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The Connection represents a train traveling from A to B leaving 
 * station A on a certain moment in time (departure
 */
public class ConnectionData implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("DepartureDateTime")
	private Date departureDateTime ;
	@SerializedName("ArrivalDateTime")
	private Date arrivalDateTime;
	@SerializedName("NrOfTransfers")
	private int nrOfTransfers;
	@SerializedName("OptionalReservationsIncluded")
	private boolean optionalReservationsIncluded;
	@SerializedName("OptionalReservationsPricePerPerson")
	private Price optionalReservationsPricePerPerson;
	@SerializedName("HasLegsWithTicketNotIncluded")
	private boolean hasLegsWithTicketNotIncluded;
    @SerializedName("LegsWithTicketNotIncluded")
	private List<TrainLegData> legsWithTicketNotIncluded;
    private TrainLegData mixCode;
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getDepartureDateTime() {
		return departureDateTime;
	}

	public List<TrainLegData> getLegsWithTicketNotIncluded() {
		return legsWithTicketNotIncluded;
	}

	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public int getNrOfTransfers() {
		return nrOfTransfers;
	}

	public boolean isOptionalReservationsIncluded() {
		return optionalReservationsIncluded;
	}

	public Price getOptionalReservationsPricePerPerson() {
		return optionalReservationsPricePerPerson;
	}

	public boolean isHasLegsWithTicketNotIncluded() {
		return hasLegsWithTicketNotIncluded;
	}

	public ConnectionData(Date departureDateTime, Date arrivalDateTime,
			int nrOfTransfers, boolean optionalReservationsIncluded,
			Price optionalReservationsPricePerPerson,
			boolean hasLegsWithTicketNotIncluded,List<TrainLegData> legsWithTicketNotIncluded) {
		super();
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
		this.nrOfTransfers = nrOfTransfers;
		this.optionalReservationsIncluded = optionalReservationsIncluded;
		this.optionalReservationsPricePerPerson = optionalReservationsPricePerPerson;
		this.hasLegsWithTicketNotIncluded = hasLegsWithTicketNotIncluded;
		this.legsWithTicketNotIncluded = legsWithTicketNotIncluded;
	}
	
}
