package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class TariffCondition implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Key")
	private String key;
	
	@SerializedName("Value")
	private String value;

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	
}
