package com.cfl.model;


import com.google.gson.annotations.SerializedName;



public class AftersalesResponse extends RestResponse {
	
	private static final long serialVersionUID = 1L;

	@SerializedName("ActionAllowed")
	private String actionAllowed;

	@SerializedName("Url")
	private String url;

	@SerializedName("NotAllowedReason")
	private String notAllowedReason;

	public String getActionAllowed() {
		return actionAllowed;
	}

	public String getUrl() {
		return url;
	}

	public String getNotAllowedReason() {
		return notAllowedReason;
	}
	
	
}
