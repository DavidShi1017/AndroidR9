package com.nmbs.model;

import java.io.Serializable;
import java.util.List;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;

/**
 * The CityDataDetails class represents the detail information on a city.
 * @author DAVID	
 *
 */
public class CityDetails implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@SerializedName("Name")
	private String name;
	
	@SerializedName("Longitude")
	private String longitude;
	
	@SerializedName("TravelTime")
	private String travelTime;
	
	@SerializedName("LowestPrice")
	private Price lowestPrice;
	
	@SerializedName("LowestPriceInPreferredCurrency")
	private Price lowestPriceInPreferredCurrency;
	
	@SerializedName("Latitude")
	private String latitude;
	
	@SerializedName("DetailImageHighResolution")
	private String detailImageHighResolution;
	@SerializedName("DetailImageLowResolution")
	private String detailImageLowResolution;
	@SerializedName("Ufi")
	private String ufi;

	@SerializedName("Description")
	private String description;
	@SerializedName("Icon")
	private String icon;
	@SerializedName("MainStation")
	private String mainStation;
	@SerializedName("Restaurants")
	List<EPR> restaurants;
	@SerializedName("Events")
	List<EPR> events;
	public List<EPR> getRestaurants() {
		return restaurants;
	}
	public List<EPR> getEvents() {
		return events;
	}
	public List<EPR> getPointsOfInterest() {
		return pointsOfInterest;
	}

	@SerializedName("PointsOfInterest")
	List<EPR> pointsOfInterest;
	
	public String getTravelTime() {
		return travelTime;
	}
	public Price getLowestPrice() {
		return lowestPrice;
	}
	
	public Price getLowestPriceInPreferredCurrency() {
		return lowestPriceInPreferredCurrency;
	}
	public String getIcon() {
		return icon;
	}
	public String getMainStation() {
		return mainStation;
	}
	public String getName() {
		return name;
	}
	public String getLongitude() {
		return longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getDetailImageHighResolution(){
		return detailImageHighResolution;
	}
	public String getDescription() {
		return description;
	}
	
	public String getUfi() {
		return ufi;
	}
	public CityDetails(String name, String longitude, String latitude, String detailImageHighResolution, String description){
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.detailImageHighResolution = detailImageHighResolution;
		
		this.description = description;
	}
	public String getDetailImageLowResolution() {
		return detailImageLowResolution;
	}
	
	public String getDetailImage(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.detailImageHighResolution;
		}else{
			return this.detailImageLowResolution;
		}
	}
}
