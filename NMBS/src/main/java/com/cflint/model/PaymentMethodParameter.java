package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Payment Method Parameter 
 *@author: Tony
 */
public class PaymentMethodParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Method")
	private String method;

	public String getMethod() {
		return method;
	}

	public PaymentMethodParameter(String method) {
		super();
		this.method = method;
	}

	
	
}
