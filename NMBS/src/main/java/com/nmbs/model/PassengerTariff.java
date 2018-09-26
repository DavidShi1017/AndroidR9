package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PassengerTariff implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("PassengerId")
	private String passengerId;
	
	@SerializedName("TariffId")
	private String tariffId;

	public String getPassengerId() {
		return passengerId;
	}

	public String getTariffId() {
		return tariffId;
	}

}
