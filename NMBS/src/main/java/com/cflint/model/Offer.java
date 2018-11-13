package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cflint.model.OfferQuery.ComforClass;

/**
 * The Offer represent a specific type of ticket for a connection. 
 */
public class Offer implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String offerInfoId;
	private Price price;
	private Price priceInPreferredCurrency;
	
	private Price priceWithOptionalReservations;
	private Price priceWithOptionalReservationsInPreferredCurrency;
	
	public Price getPriceInPreferredCurrency() {
		return priceInPreferredCurrency;
	}
	public void setPriceInPreferredCurrency(
			Price priceInPreferredCurrency) {
		this.priceInPreferredCurrency = priceInPreferredCurrency;
	}
	private boolean hasEbsLeg;
	private boolean showPromoFlag;
	private boolean showCorporateFlag;
	private boolean showReductionFlag;
	private String flexibility;
	private List<OfferItem> offerItems = new ArrayList<OfferItem>();
	private ComforClass comforClass;
	
	private List<CorporateContract> appliedCorporateContracts = new ArrayList<CorporateContract>();
	private List<ReductionCard> reductionCards = new ArrayList<ReductionCard>();
	private boolean showLowSeatAvailabilityFlag;
	/**
	 * @return the offerInfoId
	 */
	public String getOfferInfoId() {
		return offerInfoId;
	}
	/**
	 * @return the priceInMainCurrency
	 */
	public Price getPrice() {
		return price;
	}
	/**
	 * @return the hasEbsEnsLeg
	 */
	public boolean isHasEbsLeg() {
		return hasEbsLeg;
	}
	/**
	 * @return the showPromoFlag
	 */
	public boolean isShowPromoFlag() {
		return showPromoFlag;
	}
	/**
	 * @return the showCorporateFlag
	 */
	public boolean isShowCorporateFlag() {
		return showCorporateFlag;
	}
	/**
	 * @return the showReductionFlag
	 */
	public boolean isShowReductionFlag() {
		return showReductionFlag;
	}
	
	/**
	 * @return the flexibility
	 */
	public String getFlexibility() {
		return flexibility;
	}
	/**
	 * @return the offerItemDatas
	 */
	public List<OfferItem> getOfferItems() {
		return offerItems;
	}
	
	/**
	 * @return ComforClass
	 */
	public ComforClass getComforClass() {
		return comforClass;
	}
	
	
	public Price getPriceWithOptionalReservations() {
		return priceWithOptionalReservations;
	}
	public Price getPriceWithOptionalReservationsInPreferredCurrency() {
		return priceWithOptionalReservationsInPreferredCurrency;
	}
	/**
	 * @param object
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addOfferItem(OfferItem object) {
		return offerItems.add(object);
	}
	public Offer(String offerInfoId, Price price, 
			Price priceInPreferredCurrency, Price priceWithOptionalReservations,
			Price priceWithOptionalReservationsInPreferredCurrency, boolean hasEbsLeg,
			boolean showPromoFlag, boolean showCorporateFlag,
			boolean showReductionFlag, String flexibility,
			List<OfferItem> offerItems ,ComforClass comforClass, List<CorporateContract> appliedCorporateContracts, 
			List<ReductionCard> reductionCards, boolean showLowSeatAvailabilityFlag) {
		this.offerInfoId = offerInfoId;
		this.price = price;
		this.priceInPreferredCurrency = priceInPreferredCurrency;
		this.priceWithOptionalReservations = priceWithOptionalReservations;
		this.priceWithOptionalReservationsInPreferredCurrency = priceWithOptionalReservationsInPreferredCurrency;
		this.hasEbsLeg = hasEbsLeg;
		this.showPromoFlag = showPromoFlag;
		this.showCorporateFlag = showCorporateFlag;
		this.showReductionFlag = showReductionFlag;
		this.flexibility = flexibility;
		this.offerItems = offerItems;
		this.comforClass = comforClass;
		this.appliedCorporateContracts = appliedCorporateContracts;
		this.reductionCards = reductionCards;
		this.showLowSeatAvailabilityFlag = showLowSeatAvailabilityFlag;
	}
	public List<CorporateContract> getAppliedCorporateContracts() {
		return appliedCorporateContracts;
	}
	public List<ReductionCard> getReductionCards() {
		return reductionCards;
	}
	public boolean isShowLowSeatAvailabilityFlag() {
		return showLowSeatAvailabilityFlag;
	}
	
	
}
