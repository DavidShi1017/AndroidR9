package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * The Currency class represents the currency applicable for a certain price.
 * @author David.shi
 */
public class Currency implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("CurrencyCode")
	private String currencyCode;
	
	@SerializedName("CurrencyName")
	private String currencyName;
	
	@SerializedName("CurrencySymbol")
	private String currencySymbol;
	
	@SerializedName("NumberOfDigits")
	private int numberOfDecimals;
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public String getCurrencyName() {
		return currencyName;
	}
	
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	
	public int getNumberOfDecimals() {
		return numberOfDecimals;
	}
	public Currency(String currencyCode, String currencyName,
			String currencySymbol, int numberOfDecimals) {
		this.currencyCode = currencyCode;
		this.currencyName = currencyName;
		this.currencySymbol = currencySymbol;
		this.numberOfDecimals = numberOfDecimals;
	}
	
	
}
