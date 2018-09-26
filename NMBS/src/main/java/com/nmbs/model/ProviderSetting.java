package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ProviderSetting implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Provider")
	private String provider;
	@SerializedName("PhoneNumber")
	private String phoneNumber;
	public String getProvider() {
		return provider;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	
	public ProviderSetting(String provider, String phoneNumber){
		this.provider = provider;
		this.phoneNumber = phoneNumber;
	}
}
