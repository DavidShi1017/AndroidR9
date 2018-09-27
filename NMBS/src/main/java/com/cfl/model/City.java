package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;


/**
 * Represents a city by holding the cityname. The name is 
 * in the language of the user.
 * @author DAVID
 *
 */
public class City implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("Id")
	private String id;
	@SerializedName("Name")
	private String name;
	@SerializedName("MainImageHighResolution")
	private String mainImageHighResolution;
	@SerializedName("MainImageLowResolution")
	private String mainImageLowResolution;
	@SerializedName("IconHighResolution")
	private String iconHighResolution;
	@SerializedName("IconLowResolution")
	private String iconLowResolution;
	@SerializedName("MainStation")
	private String mainStation;
	@SerializedName("RestaurantIds")
	private List<String> restaurantIds = new ArrayList<String>();
	@SerializedName("EventIds")
	private List<String> eventIds = new ArrayList<String>();
	@SerializedName("PointOfInterestIds")
	private List<String> pointOfInterestIds = new ArrayList<String>();
	
	private int eventnewIDNumber;
	private int poiIDNumber;
	private int restoIDNumber;
	public String getName() {
		this.name = this.name != null? this.name:"";
		return name;
	}
	
	public City(String id, String name, String mainImageHighResolution, String mainImageLowResolution, String iconHighResolution, String iconLowResolution, String mainStation, List<String> pointOfInterestIds, 
			List<String> eventIds, List<String> restaurantIds, int eventnewIDNumber, int poiIDNumber, int restoIDNumber){
		this.id = id;
		this.name = name;
		this.mainImageHighResolution = mainImageHighResolution;
		this.mainImageLowResolution = mainImageLowResolution;
		this.iconHighResolution = iconHighResolution;
		this.iconLowResolution = iconLowResolution;
		this.mainStation = mainStation;
		this.restaurantIds = restaurantIds;
		this.eventIds = eventIds;
		this.pointOfInterestIds = pointOfInterestIds;
		this.eventnewIDNumber = eventnewIDNumber;
		this.poiIDNumber = poiIDNumber;
		this.restoIDNumber = restoIDNumber;
	}

	public String getId() {
		return id;
	}

	public String getMainImageHighResolution() {
		this.mainImageHighResolution = this.mainImageHighResolution != null? this.mainImageHighResolution:"";
		return mainImageHighResolution;
	}

	public String getIconHighResolution() {
		this.iconHighResolution = this.iconHighResolution != null? this.iconHighResolution:"";
		return iconHighResolution;
	}

	public List<String> getRestaurantIds() {
		return restaurantIds;
	}

	public List<String> getEventIds() {
		return eventIds;
	}

	public List<String> getPointOfInterestIds() {
		return pointOfInterestIds;
	}

	public String getMainStation() {
		this.mainStation = this.mainStation != null? this.mainStation:"";
		return mainStation;
	}

	public int getEventnewIDNumber() {
		return eventnewIDNumber;
	}

	public void setEventnewIDNumber(int eventnewIDNumber) {
		this.eventnewIDNumber = eventnewIDNumber;
	}

	public int getPoiIDNumber() {
		return poiIDNumber;
	}

	public void setPoiIDNumber(int poiIDNumber) {
		this.poiIDNumber = poiIDNumber;
	}

	public int getRestoIDNumber() {
		return restoIDNumber;
	}

	public void setRestoIDNumber(int restoIDNumber) {
		this.restoIDNumber = restoIDNumber;
	}

	public String getMainImageLowResolution() {
		return mainImageLowResolution;
	}

	public String getIconLowResolution() {
		return iconLowResolution;
	}
	
	public String getMainImage(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.mainImageHighResolution;
		}else{
			return this.mainImageLowResolution;
		}
	}
	
	public String getIconImage(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.iconHighResolution;
		}else{
			return this.iconLowResolution;
		}
	}
}
