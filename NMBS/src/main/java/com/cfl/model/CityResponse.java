package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The CityResponse class contains the actual 
 * reponse payload sent by the server when a client 
 * application requests for City Master Data.
 * @author DAVID
 *
 */
public class CityResponse extends RestResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Cities")
	private List<City> cities = new ArrayList<City>();
	public CityResponse(List<City> cities){
		this.cities = cities;
	}
	public List<City> getCities() {
		return cities;
	}
}
