package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;




/**
 * The StationResponse class is a subclass of the class RestResponse.
 * The StationResponse class contains the actual response payload sent
 * by the server when a client application requests for the Station
 * master data.
 * @author David.shi
 */
public class StationResponse extends RestResponse{

	private static final long serialVersionUID = 1L;

	@SerializedName("Stations")
	private List<Station> stations = new ArrayList<Station>();

	public StationResponse(){

	}

	public StationResponse(List<Station> stations){
		this.stations = stations;
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}


}
