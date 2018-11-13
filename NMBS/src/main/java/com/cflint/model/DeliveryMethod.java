package com.cflint.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeliveryMethod implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Method")
	private String method;
	@SerializedName("Name")
	private String name;
	@SerializedName("DisplayMethod")
	private String displaymethod;
	@SerializedName("AllowedStationsForPickUp")	
	private List<StationsForPickup> allowedStationsForPickup;
	@SerializedName("AllowedCountriesForMail")
	private List<String> allowedCountriesForMail;
	
	public static final String DELIVERYMETHOD_MAIL = "Mail";
	public static final String DELIVERYMETHOD_STATION = "Station";
	public static final String DELIVERYMETHOD_PRINT = "Print";
	public static final String DELIVERYMETHOD_THALYSTICKETLESS = "ThalysTicketless";
	
	public String getMethod() {
		return method;
	}
	public String getName() {
		return name;
	}
	public String getDisplaymethod() {
		return displaymethod;
	}
	public List<StationsForPickup> getAllowedStationsForPickup() {
		return allowedStationsForPickup;
	}
	public List<String> getAllowedCountriesForMail() {
		return allowedCountriesForMail;
	}
	public DeliveryMethod(String method, String name, String displaymethod, List<StationsForPickup> allowedStationsForPickup, List<String> allowedCountriesForMail){
		this.method = method;
		this.name = name;
		this.displaymethod = displaymethod;
		this.allowedStationsForPickup = allowedStationsForPickup;
		this.allowedCountriesForMail = allowedCountriesForMail;
	}
}
