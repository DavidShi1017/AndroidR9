package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Parking implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Content")
	private String content;
	
	
	@SerializedName("Address")
	private String address;
	
	
	@SerializedName("Longitude")
	private String longitude;
	
	
	@SerializedName("Latitude")
	private String latitude;

	public Parking(String title,String content,String address, String longitude, String latitude){
		this.title = title;
		this.content = content;
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public String getTitle() {
		return title;
	}


	public String getContent() {
		return content;
	}


	public String getAddress() {
		return address;
	}


	public String getLongitude() {
		return longitude;
	}


	public String getLatitude() {
		return latitude;
	}

	
	
	
}
