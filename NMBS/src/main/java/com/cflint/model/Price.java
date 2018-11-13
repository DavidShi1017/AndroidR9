package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * The cost of a ticket is represented by the Price class.
 */
public class Price implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Amount")
	private double amount;
	//private Currency realCurrency;
	@SerializedName("CurrencyCode")
	private String currencyCode;
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public Price(double amount, String currencyCode) {
		this.amount = amount;
		this.currencyCode = currencyCode;
	}
	
}
