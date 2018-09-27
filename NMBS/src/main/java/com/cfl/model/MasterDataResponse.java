package com.cfl.model;

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
public class MasterDataResponse extends RestResponse {


	private static final long serialVersionUID = 1L;
	@SerializedName("DeliveryMethods")
	private List<DeliveryMethod> deliveryMethods = new ArrayList<DeliveryMethod>();
	
	@SerializedName("PaymentMethods")
	private List<CollectionItem> paymentMethods = new ArrayList<CollectionItem>();
	
	@SerializedName("ReductionCards")
	private List<CollectionItem> reductionCards = new ArrayList<CollectionItem>();
	
	@SerializedName("CorporateContracts")
	private List<CollectionItem> corporateContracts = new ArrayList<CollectionItem>();
	
	@SerializedName("OriginDestinationRules")
	
	private List<OriginDestinationRule> originDestinationRules = new ArrayList<OriginDestinationRule>();
	
	
	
	public MasterDataResponse(List<DeliveryMethod> deliveryMethods, List<CollectionItem> paymentMethods, List<CollectionItem> reductionCards,
			List<CollectionItem> corporateContracts, List<OriginDestinationRule> originDestinationRules){
		this.deliveryMethods = deliveryMethods;
		this.paymentMethods = paymentMethods;
		this.reductionCards = reductionCards;
		this.corporateContracts = corporateContracts;
		this.originDestinationRules = originDestinationRules;
		
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<DeliveryMethod> getDeliveryMethods() {
		return deliveryMethods;
	}

	public List<CollectionItem> getPaymentMethods() {
		return paymentMethods;
	}

	public List<CollectionItem> getReductionCards() {
		return reductionCards;
	}

	public List<CollectionItem> getCorporateContracts() {
		return corporateContracts;
	}
	
	public List<OriginDestinationRule> getOriginDestinationRules() {
		return originDestinationRules;
	}
}
