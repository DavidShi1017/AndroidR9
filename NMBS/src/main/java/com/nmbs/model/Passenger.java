package com.nmbs.model;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.nmbs.R;
import com.nmbs.model.PartyMember.PersonType;

public class Passenger implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("SegmentPassengerId")
	private String segmentPassengerId;
	
	@SerializedName("FirstName")
	private String firstName;
		
	@SerializedName("LastName")
	private String lastName;
    
	@SerializedName("PassengerType")
	private String passengerType;
    
	@SerializedName("AppliedReductionCards")
	private String [] appliedReductionCards;

	
	
	@SerializedName("Id")
	private String id;

	

	public String getSegmentPassengerId() {
		return segmentPassengerId;
	}
	public String[] getAppliedReductionCards() {
		return appliedReductionCards;
	}

	@SerializedName("Name")
	private String name;

	@SerializedName("ReductionCards")
	private List<String> reductionCards;
	@SerializedName("FtpCards")
	private List<FtpCard> ftpCards;
	
	private int passengerSortorderField;
	public String getId() {
		return segmentPassengerId;
	}
	public String getPassengerType() {
		return this.passengerType;
	}
	public String getFirstName() {
		return this.firstName == null? "" : this.firstName;
		
	}
	public List<String> getReductionCards() {
		return reductionCards;
	}
	public List<FtpCard> getFtpCards() {
		return ftpCards;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getName() {
		return name;
	}
	public Passenger(String id, String passengerType, String firstName, String lastName, String name, List<String> reductionCards, List<FtpCard> ftpCards){
		this.id = id;
		this.passengerType = passengerType;
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.reductionCards = reductionCards;
		this.ftpCards = ftpCards;
	}
	
	public Integer getPassengerSortorderField() {
		return passengerSortorderField;
	}

	public void setPassengerSortorderField(int passengerSortorderField) {
		this.passengerSortorderField = passengerSortorderField;
	}


}
