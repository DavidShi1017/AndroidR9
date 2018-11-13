package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The OfferItem class specifies the relation between the OfferData and the TariffData.
 */
public class OfferItem implements Serializable{

	private static final long serialVersionUID = -7569361269847025213L;
	private List<Tariff> tariffs = new ArrayList<Tariff>();
	private List<OfferLegItem> offerLegItem = new ArrayList<OfferLegItem>();
	
	
	private Price price;
	private Price priceInPreferredCurrency;
	private String originName;
	private String originStationCode;
	private String destinationName;
	public List<OfferLegItem> getOfferLegItem() {
		return offerLegItem;
	}
	
	public Price getPrice() {
		return price;
	}

	public Price getPriceInPreferredCurrency() {
		return priceInPreferredCurrency;
	}

	public String getOriginName() {
		return originName;
	}

	public String getOriginStationCode() {
		return originStationCode;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public String getDestinationStationCode() {
		return destinationStationCode;
	}

	public List<String> getEBSMessageTypes() {
		return EBSMessageTypes;
	}


	private String destinationStationCode;
	private List<String> EBSMessageTypes = new ArrayList<String>();
	

	public List<Tariff> getTariffs() {
		return tariffs;
	}
	public boolean addTariff(Tariff object) {
		return tariffs.add(object);
	}
	public List<OfferLegItem> getOfferLegItems() {
		return offerLegItem;
	}
	public OfferItem(List<Tariff> tariffs, List<OfferLegItem> offerLegItem
			, Price price, Price priceInPreferredCurrency, String originName, String originStationCode
			, String destinationName, String destinationStationCode, List<String> EBSMessageTypes) {		
		this.tariffs = tariffs;
		this.offerLegItem = offerLegItem;
		
		this.price = price;
		this.priceInPreferredCurrency = priceInPreferredCurrency;
		this.originName = originName;
		this.originStationCode = originStationCode;
		this.destinationName = destinationName;
		this.destinationStationCode = destinationStationCode;
		this.EBSMessageTypes = EBSMessageTypes;
	}
}
