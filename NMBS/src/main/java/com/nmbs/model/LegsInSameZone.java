package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class LegsInSameZone implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("OriginRcode")
	private String originRcode;

	@SerializedName("OriginName")
	private String originName;

	@SerializedName("DestinationRcode")
	private String destinationRcode;

	@SerializedName("DestinationName")
	private String destinationName;

	public String getOriginRcode() {
		return originRcode;
	}

	public String getOriginName() {
		return originName;
	}

	public String getDestinationRcode() {
		return destinationRcode;
	}

	public String getDestinationName() {
		return destinationName;
	}
 

	
}
