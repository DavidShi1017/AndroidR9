package com.cflint.model;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;



public class DossiersUpToDate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@SerializedName("Dnr")
	private String dnr;

	@SerializedName("CallSuccessful")
	private boolean callSuccessful;

	@SerializedName("UpToDate")
	private boolean upToDate;

	public String getDnr() {
		return dnr;
	}

	public boolean isCallSuccessful() {
		return callSuccessful;
	}

	public boolean isUpToDate() {
		return upToDate;
	}
}
