package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * The Facility class represents an installation or other thing which facilitates 
 * or helps a traveler in the train station.
 * @author David.shi
 */
public class Facility implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Description")
	private String description;
	
	@SerializedName("Icon")
	private String iconPath;
	
	public Facility(String description, String iconPath){
		this.description = description;
		this.iconPath = iconPath;
	}

	public String getDescription() {
		return description;
	}

	public String getIconPath() {
		return iconPath;
	}
	
	
}
