package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cfl.model.PartyMember.PersonType;

public class TariffPassenger implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private PersonType personType;
	private List<ReductionCard> appliedReductionCards = new ArrayList<ReductionCard>();
	
	private Price price;
	private Price priceInPreferredCurrency;
	
	public PersonType getPassengerType() {
		return personType;
	}

	public Price getPriceInPreferredCurrency() {
		return priceInPreferredCurrency;
	}
	public List<ReductionCard> getAppliedReductionCards() {
		return appliedReductionCards;
	}
	/**
	 * @return the priceList
	 */
	public Price getPrice() {
		return price;
	}
	
	public TariffPassenger(PersonType personType, List<ReductionCard> appliedReductionCards, Price price, Price priceInPreferredCurrency){
		this.personType = personType;
		this.appliedReductionCards = appliedReductionCards;
		this.price = price;
		this.priceInPreferredCurrency = priceInPreferredCurrency;
	}
}
