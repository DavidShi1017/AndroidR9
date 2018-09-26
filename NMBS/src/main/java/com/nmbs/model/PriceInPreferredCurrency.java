package com.nmbs.model;

import java.io.Serializable;


/**
 * The cost of a ticket is represented by the Price class.
 */
public class PriceInPreferredCurrency implements Serializable{

	private static final long serialVersionUID = 1L;
	private double amount;
	//private Currency realCurrency;
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

	public PriceInPreferredCurrency(double amount, String currencyCode) {
		this.amount = amount;
		this.currencyCode = currencyCode;
	}
	
}
