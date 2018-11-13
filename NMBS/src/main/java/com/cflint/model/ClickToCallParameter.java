package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ClickToCallParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("PhoneNumber")
	private String phoneNumber;
	@SerializedName("ClickToCallContext")
	private ClickToCallContext clickToCallContext;
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public ClickToCallContext getClickToCallContext() {
		return clickToCallContext;
	}
	public void setClickToCallContext(ClickToCallContext clickToCallContext) {
		this.clickToCallContext = clickToCallContext;
	}
	
}
