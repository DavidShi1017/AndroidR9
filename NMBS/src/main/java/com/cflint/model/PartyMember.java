package com.cflint.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


import com.google.gson.annotations.SerializedName;

import com.cflint.util.CardUtils;

/**
 * Data model implementation record some info for user input
 * in PassengersSelectionActivity.
 */
public class PartyMember implements Serializable{

	private static final long serialVersionUID = 496658201875400414L;
	//private static final String TAG = PartyMember.class.getName();
	private int partyMemberCount = 1;


	private int partMemberSortorderField;
	public enum PersonType {
		KID0, KID4, KID6, KID12, KID15, YOUTH, ADULT, SENIOR;
	}
	public enum FtpCardFeedbackTypes{
		CORRECT,
		OfferQueryFeedbackTypeFTPCardFirstEurostarIncorrect,
		OfferQueryFeedbackTypeFTPCardFirstThalysIncorrect,
		OfferQueryFeedbackTypeFTPCardSecondEurostarIncorrect,
		OfferQueryFeedbackTypeFTPCardSecondThalysIncorrect,
		OfferQueryFeedbackTypeFTPCardDup,
		OfferQueryFeedbackTypeFTPCardIncorrect;
	}
	
	
	
	@SerializedName("ReductionCards")
	private List<ReductionCard> reductionCards;
	@SerializedName("PersonType")
	private PersonType personType;
	@SerializedName("EurostarFtpCard")
	private String eurostarFtpCard;
	@SerializedName("ThalysFtpCard")
	private String thalysFtpCard;
	public PartyMember(PersonType personType) {
		super();
		this.personType = personType;
	}

	public PartyMember() {
		super();
	}
	
	public List<ReductionCard> getReductionCards() {
		if (reductionCards == null) {
			reductionCards = new ArrayList<ReductionCard>();
		}
		return reductionCards;
	}

	public void setReductionCards(List<ReductionCard> reductionCards) {
		this.reductionCards = reductionCards;
	}

	public PersonType getPersonType() {
		return personType;
	}
	
	
	
	public int getPartyMemberCount() {
		return partyMemberCount;
	}

	public void setPartyMemberCount(int partyMemberCount) {
		this.partyMemberCount = partyMemberCount;
	}

	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}

	public String getEurostarFtpCard() {
		return eurostarFtpCard;
	}

	public String getThalysFtpCard() {
		return thalysFtpCard;
	}

	public void setEurostarFtpCard(String eurostarFtpCard) {
		this.eurostarFtpCard = eurostarFtpCard;
	}

	public void setThalysFtpCard(String thalysFtpCard) {
		this.thalysFtpCard = thalysFtpCard;
	}
	
	public FtpCardFeedbackTypes validateFtpCard(int personNumber) {
		
		//System.out.println("thalysFtpCard======"+eurostarFtpCard);
		if (eurostarFtpCard != null && !StringUtils.isEmpty(eurostarFtpCard)) {
			//System.out.println("CardUtils.isValidFTPCard======"+CardUtils.isValidFTPCard(eurostarFtpCard));
			if(!CardUtils.isValidFTPCard(eurostarFtpCard)){
				//System.out.println("eurostarFtpCard======"+eurostarFtpCard);
				return validateFtpCardEmueTypeForEurostar(personNumber);
			}	
			//System.out.println("CardUtils.luhnTest(eurostarFtpCard======"+CardUtils.luhnTest(eurostarFtpCard));
			if (!CardUtils.luhnTest(eurostarFtpCard)) {
				return validateFtpCardEmueTypeForEurostar(personNumber);		
			}
		}
		if (thalysFtpCard != null && !StringUtils.isEmpty(thalysFtpCard)) {
			if(!CardUtils.isValidFTPCard(thalysFtpCard)){
				return validateFtpCardEmueTypeForThalys(personNumber);
			}	
			if (!CardUtils.luhnTest(thalysFtpCard)) {
				return validateFtpCardEmueTypeForThalys(personNumber);
			}
		}
		
		return FtpCardFeedbackTypes.CORRECT;
	}
	public FtpCardFeedbackTypes validateFtpCardEmueTypeForEurostar(int personNumber){
		
		if (personNumber == 0) {
			
			return FtpCardFeedbackTypes.OfferQueryFeedbackTypeFTPCardFirstEurostarIncorrect;
		}else {
			return FtpCardFeedbackTypes.OfferQueryFeedbackTypeFTPCardSecondEurostarIncorrect;
		}	
	}
	
	public FtpCardFeedbackTypes validateFtpCardEmueTypeForThalys(int personNumber){
		
		if (personNumber == 0) {
			return FtpCardFeedbackTypes.OfferQueryFeedbackTypeFTPCardFirstThalysIncorrect;
		}else {
			return FtpCardFeedbackTypes.OfferQueryFeedbackTypeFTPCardSecondThalysIncorrect;
		}	
	}

	public Integer getPartMemberSortorderField() {
		return partMemberSortorderField;
	}

	public void setPartMemberSortorderField(int partMemberSortorderField) {
		this.partMemberSortorderField = partMemberSortorderField;
	}
	

}
