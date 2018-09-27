package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * The class TariffData represents the refund and exchange conditions of a ticket
 * @author David.Shi
 *
 */
public class Tariff implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("TariffId")
	private String tariffId;
	
	@SerializedName("TariffName")
	private String tariffName;
	
	@SerializedName("TariffConditions")
	private List<TariffCondition> tariffConditions;
	
	@SerializedName("ExtraTariffInfo")
	private String extraTariffInfo;
	
	

	
	
	
	
	
	public String getTariffId() {
		return tariffId;
	}
	public String getTariffName() {
		return tariffName;
	}
	public List<TariffCondition> getTariffConditions() {
		return tariffConditions;
	}
	public String getExtraTariffInfo() {
		return extraTariffInfo;
	}

	private boolean hasFlexInfo;
	private String flexTitle;
	private List<TariffPassenger> tariffPassengers  = new ArrayList<TariffPassenger>();;
	
	private Map<String, String> flexInfo = new LinkedHashMap<String, String>();;
	
	private boolean isEbsTariff ;
	/**
	 * @return the hasFlexInfo
	 */
	public boolean isHasFlexInfo() {
		return hasFlexInfo;
	}
	/**
	 * @return the flexTitle
	 */
	public String getFlexTitle() {
		return flexTitle;
	}

	

	/**
	 * @return the flexInfo
	 */
	public Map<String, String> getFlexInfo() {
		return flexInfo;
	}
	

	
	public List<TariffPassenger> getTariffPassengers() {
		return tariffPassengers;
	}
	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public String putFlexInfo(String key, String value) {
		return flexInfo.put(key, value);
	}
	
	public Tariff(boolean hasFlexInfo, String flexTitle,
			List<TariffPassenger> tariffPassengers, Map<String, String> flexInfo, boolean isEbsTariff) {
		this.hasFlexInfo = hasFlexInfo;
		this.flexTitle = flexTitle;
		this.tariffPassengers = tariffPassengers;
		this.flexInfo = flexInfo;
		this.isEbsTariff =isEbsTariff;
	}
	public boolean isEbsTariff() {
		return isEbsTariff;
	}
	
	public boolean isAllTariffPassengersPriceAvailable(){
		boolean isAllTariffPassengersPriceAvailable = true;
		
		for (TariffPassenger tariffPassenger : tariffPassengers) {
			Price price = tariffPassenger.getPrice();
			Price priceInPreferredCurrency = tariffPassenger.getPriceInPreferredCurrency();
			if (price == null && priceInPreferredCurrency == null) {
				return false;
			}
		}
		return isAllTariffPassengersPriceAvailable;
	}
}
