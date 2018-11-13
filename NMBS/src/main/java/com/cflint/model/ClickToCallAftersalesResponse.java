package com.cflint.model;

import com.google.gson.annotations.SerializedName;

public class ClickToCallAftersalesResponse extends RestResponse{
	private static final long serialVersionUID = 1L;
	@SerializedName("ActionAllowed")
	private boolean actionAllowed;

	@SerializedName("Url")
	private String url;

	@SerializedName("NotAllowedReason")
	private String notAllowedReason;

	public boolean isActionAllowed() {
		return actionAllowed;
	}

	public String getUrl() {
		return url;
	}

	public String getNotAllowedReason() {
		return notAllowedReason;
	}
}
