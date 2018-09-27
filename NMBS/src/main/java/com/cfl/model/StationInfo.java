package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The class Station reprensents a train station. 
 * This class holds the general information that identifies the train station.
 * @author David.shi
 */
public class StationInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	

	@SerializedName("Code")
	private String code;
	
	@SerializedName("Name")
	private String name;
	

	@SerializedName("OpeningHours")
	private String openingHours;

	@SerializedName("FloorPlanDownloadURL")
	private String floorPlanDownloadURL;

	@SerializedName("MoreDetailsURL")
	private String moreDetailsURL;

	@SerializedName("Latitude")
	private String latitude;

	@SerializedName("Longitude")
	private String longitude;

	@SerializedName("Street")
	private String street;

	@SerializedName("Number")
	private String number;

	@SerializedName("PostalCode")
	private String postalCode;

	@SerializedName("City")
	private String city;

	@SerializedName("Country")
	private String country;

	@SerializedName("StationInfos")
	private List<StationInformation> stations = new ArrayList<StationInformation>();

	@SerializedName("Parkings")
	private List<Parking> parkings = new ArrayList<Parking>();

	@SerializedName("StationFacilities")
	private List<StationFacility> stationFacilities = new ArrayList<StationFacility>();

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public String getFloorPlanDownloadURL() {
		return floorPlanDownloadURL;
	}

	public String getMoreDetailsURL() {
		return moreDetailsURL;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getStreet() {
		return street;
	}

	public String getNumber() {
		return number;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public List<StationInformation> getStations() {
		return stations;
	}

	public List<Parking> getParkings() {
		return parkings;
	}

	public List<StationFacility> getStationFacilities() {
		return stationFacilities;
	}

	public void setParkings(List<Parking> parkings) {
		this.parkings = parkings;
	}

	public void setStations(List<StationInformation> stations) {
		this.stations = stations;
	}

	public void setStationFacilities(List<StationFacility> stationFacilities) {
		this.stationFacilities = stationFacilities;
	}
}
