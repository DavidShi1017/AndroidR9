package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PaymentOption implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@SerializedName("Cost")
	private Price cost;
	@SerializedName("Method")
	private String method;
	public Price getCost() {
		return cost;
	}

	public String getMethod() {
		return method;
	}
	public PaymentOption(Price cost, String method){
		this.cost= cost;
		this.method= method;		
	}
}
