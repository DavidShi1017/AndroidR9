package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SeatLocation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@SerializedName("CoachNumber")
	private String coachNumber;
	
	@SerializedName("SeatNumberInfo")
	private String seatNumberInfo;
	
	@SerializedName("SegmentPassengerId")
	private String segmentPassengerId;
	
	
	
	
	public String getSeatNumberInfo() {
		return seatNumberInfo;
	}
	public String getSegmentPassengerId() {
		return segmentPassengerId;
	}
	@SerializedName("SeatNumber")
	private String seatNumber;
	@SerializedName("PassengerId")
	private String passengerId;
	public String getCoachNumber() {
		return coachNumber;
	}
	public String getSeatNumber() {
		return seatNumber;
	}
	public String getPassengerId() {
		return passengerId;
	}
	public SeatLocation(String coachNumber, String seatNumber, String passengerId){
		this.coachNumber = coachNumber;
		this.seatNumber = seatNumber;
		this.passengerId = passengerId;
	}
}
