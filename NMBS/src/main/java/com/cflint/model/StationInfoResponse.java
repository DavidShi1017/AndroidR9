package com.cflint.model;

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
public class StationInfoResponse extends RestResponse{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("Stations")
	private List<StationInfo> stations = new ArrayList<StationInfo>();
	
	public StationInfoResponse(){
		
	}
	
	public StationInfoResponse(List<StationInfo> stations){
		this.stations = stations;
	}

	public List<StationInfo> getStations() {
		return stations;
	}

}
