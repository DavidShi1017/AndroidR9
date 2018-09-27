package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StationFacilitiesBlock implements Serializable{
	private static final long serialVersionUID = 1L;

	@SerializedName("Title")
	private String title;
	
	@SerializedName("Facilities")
	private List<Facility> facilities = new ArrayList<Facility>();

	public String getTitle() {
		return title;
	}

	public List<Facility> getFacilities() {
		return facilities;
	}
}
