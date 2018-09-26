package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * The CityDetailResponse class contains the actual 
 * response payload sent by the server when a client application 
 * requests for the detail information on a city.
 * @author DAVID	
 *
 */
public class CityDetailResponse extends RestResponse implements Serializable{

	
	private static final long serialVersionUID = 1L;
	@SerializedName("City")
	private CityDetails cityDetails;

	public CityDetails getCityDataDetails() {
		return cityDetails;
	}
	public CityDetailResponse(CityDetails cityDetails){
		this.cityDetails = cityDetails;
	}
	public CityDetailResponse(){
		
	}
}
