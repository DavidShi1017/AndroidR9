package com.cfl.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeliveryOption implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Cost")
	private Price cost;
	@SerializedName("Method")
	private String method;
	@SerializedName("PaymentOptions")
	private List<PaymentOption> paymentOptions;

	public List<PaymentOption> getPaymentOptions() {
		return paymentOptions;
	}

	public Price getCost() {
		return cost;
	}

	public String getMethod() {
		return method;
	}
	public DeliveryOption(Price cost, String method, List<PaymentOption> paymentOptions){
		this.cost= cost;
		this.method= method;
		this.paymentOptions= paymentOptions;
	}
}

