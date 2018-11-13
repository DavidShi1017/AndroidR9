package com.cflint.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

public class Address implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Street")
	private String street;
	
	@SerializedName("Number")
	private String number;
	
	@SerializedName("City")
	private String city;
	
	@SerializedName("Zip")
	private String zip;
	
	@SerializedName("Country")
	private String country;

	public String getStreet() {
		return street;
	}

	public String getNumber() {
		return number;
	}

	public String getCity() {
		return city;
	}

	public String getZip() {
		return zip;
	}

	public String getCountry() {
		return country;
	}
	public boolean isNotAvailablAddress(){
		if(StringUtils.isEmpty(street) && StringUtils.isEmpty(number) && StringUtils.isEmpty(city) && StringUtils.isEmpty(zip)){
			return true;
		}else {
			return false;
		}
	}
}
