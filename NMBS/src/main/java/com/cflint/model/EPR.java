package com.cflint.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.util.DisplayMetrics;

import com.google.gson.annotations.SerializedName;


public class EPR implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Id")
	private String id;
	@SerializedName("StartDate")
	private Date startDate;
	@SerializedName("EndDate")
	private Date endDate;
	@SerializedName("Name")
	private String name;
	@SerializedName("Address")
	private String address;
	@SerializedName("Latitude")
	private String latitude;
	@SerializedName("Longitude")
	private String longitude;
	@SerializedName("Description")
	private String description;
	@SerializedName("ImageHighResolution")
	private String imageHighResolution;
	@SerializedName("ImageLowResolution")
	private String imageLowResolution;
	@SerializedName("Url")
	private String url;
	@SerializedName("GroupIdentification")
	private GroupIdentification groupIdentification;
	@SerializedName("HasPromotions")
	private boolean hasPromotions;
	@SerializedName("Promotions")
	private List<Promotion> promotions;
	
	
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public String getDescription() {
		return description;
	}
	public String getImageHighResolution() {
		return imageHighResolution;
	}
	
	public String getImageLowResolution() {
		return imageLowResolution;
	}
	public String getUrl() {
		return url;
	}
	public GroupIdentification getGroupIdentification() {
		return groupIdentification;
	}
	public boolean isHasPromotions() {
		return hasPromotions;
	}
	public List<Promotion> getPromotions() {
		return promotions;
	}
	
	public EPR(Date startDate, Date endDate, String name, String address, String latitude, String longitude, 
			String description, String imageHighResolution, String imageLowResolution, String url, GroupIdentification groupIdentification, boolean hasPromotions, List<Promotion> promotions){
		this.startDate = startDate;
		this.endDate = endDate;
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		this.imageHighResolution = imageHighResolution;
		this.imageLowResolution = imageLowResolution;
		this.url = url;
		this.groupIdentification = groupIdentification;
		this.hasPromotions = hasPromotions;
		this.promotions = promotions;
	}
	public String getId() {
		return id;
	}
	
	public String getImage(int deviceDensity){
		if (deviceDensity >= DisplayMetrics.DENSITY_HIGH) {
			return this.imageHighResolution;
		}else{
			return this.imageLowResolution;
		}
	}
}
