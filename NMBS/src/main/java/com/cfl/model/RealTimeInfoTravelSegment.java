package com.cfl.model;

import android.util.Log;

import java.io.Serializable;


import com.google.gson.annotations.SerializedName;


public class RealTimeInfoTravelSegment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("LegStatus")
	private String legStatus;
	
	@SerializedName("RealTimeDepartureDelta")
	private String realTimeDepartureDelta;
	
	@SerializedName("RealTimeArrivalDelta")
	private String realTimeArrivalDelta;

	public String getLegStatus() {
		return legStatus;
	}

	public String getRealTimeDepartureDelta() {
		String departure = "";
		if(realTimeDepartureDelta != null && realTimeDepartureDelta.length() > 0){
			departure = realTimeDepartureDelta;
			departure = departure.substring(0, departure.lastIndexOf(":"));
			if(departure.indexOf("-") == -1){
				departure = "+" + departure;
			}
		}
		return departure;
		//return realTimeDepartureDelta;
	}

	public String getRealTimeArrivalDelta() {
		String arrival = "";
		if(realTimeArrivalDelta != null && realTimeArrivalDelta.length() > 0){
			Log.e("realTimeArrivalDelta", "realTimeArrivalDelta..." + realTimeArrivalDelta);
			arrival = realTimeArrivalDelta;
			arrival = arrival.substring(0, arrival.lastIndexOf(":"));
			if(arrival.indexOf("-") == -1){
				arrival = "+" + arrival;
			}
		}
		return arrival;
	}
}
