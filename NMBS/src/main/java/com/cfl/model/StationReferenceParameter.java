package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Station Reference Parameter 
 *@author: Tony
 */
public class StationReferenceParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Code")
	private String code;
	private String name;
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public StationReferenceParameter(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	
}
