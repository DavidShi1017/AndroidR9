package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *Each person in a travel party can have zero, one or more reduction cards. 
 *@author:Alice
 */
public class ReductionCard implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*public enum ReductionCardType{
		NMBSFIFTYPERCENT,
		RAIPPLUS
	}*/
	@Expose @SerializedName("CardNumber")
	private String cardNumber;
	@Expose @SerializedName("CardType")
	private String type;
	public String getCardNumber() {
		return cardNumber == null ? "" : cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getType() {
		
		return type == null ? "" : type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public ReductionCard(String cardNumber, String type){
		this.cardNumber = cardNumber;
		this.type = type;
	}
}
