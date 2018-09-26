package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClickToCallAftersalesParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Dnr")
	private String dnr;
	@SerializedName("Control")
	private String control;
	@SerializedName("Context")
	private String context;
	@SerializedName("ReturnUrlWithRefresh")
	private String returnUrlWithRefresh;
	@SerializedName("ReturnUrlWithoutRefresh")
	private String returnUrlWithoutRefresh;


	public ClickToCallAftersalesParameter(String dnr, String control, String context, String returnUrlWithRefresh, String returnUrlWithoutRefresh) {
		this.dnr = dnr;
		this.control = control;
		this.context = context;
		this.returnUrlWithRefresh = returnUrlWithRefresh;
		this.returnUrlWithoutRefresh = returnUrlWithoutRefresh;
	}

	public String getDnr() {
		return dnr;
	}

	public String getControl() {
		return control;
	}

	public String getContext() {
		return context;
	}

	public String getReturnUrlWithRefresh() {
		return returnUrlWithRefresh;
	}

	public String getReturnUrlWithoutRefresh() {
		return returnUrlWithoutRefresh;
	}
}
