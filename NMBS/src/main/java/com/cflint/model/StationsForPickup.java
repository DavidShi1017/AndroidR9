package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StationsForPickup implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Name")
	private String name;
	
	@SerializedName("Code")
	private String code;

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
	
	public StationsForPickup(String code, String name ){
		this.name = name;
		this.code = code;
	}
}

