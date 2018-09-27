package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class WeatherInformationResponse extends RestResponse implements Serializable{


	private static final long serialVersionUID = 1L;
	@SerializedName("WeatherInformation")
	private WeatherInformation weatherInformation;
	public WeatherInformation getWeatherInformation() {
		return weatherInformation;
	}
}
