package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

/**
 * The StationDetailResponse class is a subclass of the class RestResponse. 
 * The StationDetailResponse class contains the actual response payload 
 * sent by the server when a client application requests for detail 
 * information concerning a specific train station.
 * @author David.shi
 *
 */
public class StationDetailResponse extends RestResponse {

	private static final long serialVersionUID = 1L;
	
	@SerializedName("StationDetails")
	private StationInformationResult stationInformationResult;

	public StationDetailResponse(StationInformationResult stationInformationResult) {
		this.stationInformationResult = stationInformationResult;
	}

	public StationInformationResult getStationDetail() {
		return stationInformationResult;
	}
	
}
