package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DossiersUpToDateParmeter implements Serializable{
	private static final long serialVersionUID = -6835818985161074348L;

	@SerializedName("Dnr")
	private String dnr;
	@SerializedName("LastUpdated")
	private String lastUpdated;

	public DossiersUpToDateParmeter (String dnr, String lastUpdated){
		this.dnr = dnr;
		this.lastUpdated = lastUpdated;
	}

	public String getDnr() {
		return dnr;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}
}
