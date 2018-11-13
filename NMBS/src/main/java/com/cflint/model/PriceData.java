package com.cflint.model;

public class PriceData {
	private double amount;
	private String currencyCode;
	public double getAmount() {
		return amount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public PriceData(double amount, String currencyCode){
		this.amount = amount;
		this.currencyCode = currencyCode;
	}
}
