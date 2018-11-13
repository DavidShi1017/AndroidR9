package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The class StationDetail reprensents the detail description for a train station.
 * @author David.Shi
 *
 */
public class StationInformationResult implements Serializable{
	private static final long serialVersionUID = 1L;
	@SerializedName("Address")
	private Address address;
	
	@SerializedName("Parkings")
	private List<Parking> parkings = new ArrayList<Parking>();
	
	@SerializedName("FacilitiesBlock")
	private StationFacilitiesBlock stationFacilitiesBlock;
	
	@SerializedName("TextBlocks")
	private List<StationTextBlock> stationTextBlocks = new ArrayList<StationTextBlock>();
	
	@SerializedName("Code")
	private String code;
	
	@SerializedName("Name")
	private String name;
	
	@SerializedName("Destination")
	private String destination;

	
	public Address getAddress() {
		return address;
	}

	public List<Parking> getParkings() {
		return parkings;
	}

	public StationFacilitiesBlock getFacilitiesBlock() {
		return stationFacilitiesBlock;
	}

	public List<StationTextBlock> getTextBlocks() {
		return stationTextBlocks;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	@SerializedName("Longitude")
	private String longitude;
	
	@SerializedName("Latitude")
	private String latitude;
	
	
	
	
	public StationInformationResult(String code, String name, String destination, List<Facility> facilities){
		this.code = code;
		this.name = name;
		this.destination = destination;
		
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDestination() {
		return destination;
	}	
	
}
