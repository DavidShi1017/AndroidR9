package com.nmbs.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The CurrencyResponse class is a subclass of the class RestResponse. 
 * The CurrencyResponse class contains the actual response payload sent 
 * by the server when a client application requests for the Currency 
 * master data information.
 * @author David.shi
 */
public class CurrencyResponse extends RestResponse {


	private static final long serialVersionUID = 1L;
	@SerializedName("Currencies")
	private List<Currency> currencies = new ArrayList<Currency>();
	
	public CurrencyResponse(List<Currency> currencies){
		this.currencies = currencies;
	}

	public List<Currency> getCurrencies() {
		return currencies;
	}
	
	
}
