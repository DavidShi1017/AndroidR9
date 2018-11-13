package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class WeatherInformation implements Serializable{
	

	private static final long serialVersionUID = 1L;

	@SerializedName("Temperature")
	private double temperature;
	
	@SerializedName("Type")
	private String type;

	public double getTemperature() {
		return temperature;
	}

	public String getType() {
		return type;
	}
	
}
