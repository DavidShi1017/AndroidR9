package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class TrainInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("Key")
	private String key;
	
	public TrainInfo(String key){
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	
}
