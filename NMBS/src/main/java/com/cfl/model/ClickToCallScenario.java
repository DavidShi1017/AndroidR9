package com.cfl.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class ClickToCallScenario implements Serializable{

	
	public enum ClickToCallScenarioType  {
	 ClickToCallScenarioTypeOtherTravelParty,
	 ClickToCallScenarioTypeBeforePayment,
	 ClickToCallScenarioTypeAfterPayment,
	 ClickToCallScenarioTypeExchange,
	 ClickToCallScenarioTypeCancel
	} ;
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Id")
	private String id;
	@SerializedName("DefaultPhoneNumber")
	private String defaultPhoneNumber;
	@SerializedName("ProviderSettings")
	private List<ProviderSetting> providerSettings;
	private ClickToCallScenarioType clickToCallScenarioType;
	public String getId() {
		return id;
	}
	public String getDefaultPhoneNumber() {
		return defaultPhoneNumber;
	}
	public List<ProviderSetting> getProviderSettings() {
		return providerSettings;
	}

	public ClickToCallScenarioType getClickToCallScenarioType() {
		return clickToCallScenarioType;
	}
	
	public ClickToCallScenario(String id, String defaultPhoneNumber, List<ProviderSetting> providerSettings){
		this.id = id;
		this.defaultPhoneNumber = defaultPhoneNumber;
		this.providerSettings = providerSettings;
	}
}
