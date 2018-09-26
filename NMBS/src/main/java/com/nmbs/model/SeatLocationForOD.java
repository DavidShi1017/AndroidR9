package com.nmbs.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SeatLocationForOD implements Serializable{


	private static final long serialVersionUID = 1L;

	public enum Direction{
		Outward,
		Return,		
		Roundtrip,
		Single,
		unknown;
	}
	@SerializedName("Origin")
	private String origin;
	@SerializedName("Destination")
	private String destination;
	@SerializedName("Direction")
	private Direction direction;
	@SerializedName("SeatLocation")
	private List<SeatLocation> seats;
	public String getOrigin() {
		return origin;
	}
	public String getDestination() {
		return destination;
	}
	public Direction getDirection() {
		return direction;
	}
	public List<SeatLocation> getSeats() {
		return seats;
	}
	
	public SeatLocationForOD(String origin, String destination, Direction direction, List<SeatLocation> seats){
		this.origin = origin;
		this.destination = destination;
		this.direction = direction;
		this.seats = seats;
	}
}
