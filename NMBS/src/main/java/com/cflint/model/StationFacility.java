package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * The Facility class represents an installation or other thing which facilitates 
 * or helps a traveler in the train station.
 * @author David.shi
 */
public class StationFacility implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	@SerializedName("Title")
	private String title;


	public String getTitle() {
		return title;
	}

	
	
}
