package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class InfoText implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Key")
	private String key;
	@SerializedName("Value")
	private String text;
	public String getKey() {
		return key;
	}
	public String getText() {
		return text;
	}

}
