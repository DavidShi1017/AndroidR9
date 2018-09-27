package com.cfl.model;


import java.util.List;

import com.google.gson.annotations.SerializedName;



public class DossiersUpToDateResponse extends RestResponse {
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Elements")
	List<DossiersUpToDate> elements;

	public List<DossiersUpToDate> getElements() {
		return elements;
	}
	
	
	
}
