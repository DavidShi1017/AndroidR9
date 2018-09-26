package com.nmbs.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class CorporateCard implements Serializable{
	
	private static final long serialVersionUID = 1L;	
	
	@Expose @SerializedName("CardType")
	private String cardType;
	@Expose @SerializedName("CardNumber")
	private String cardNumber;
	
	
	
	public static final String TCP = "TCP";
	public static final String ECP = "ECP";
	public static final String ICP = "ICP";
	private static final String FCP = "FCP";
	public enum CorporateCardFeedBackTypes {
		 TcpNumberError,
	     IcpNumberError,
	     EcpNumberError,
	     GpNumberError,
	     FYRNumberError,
	     CorporateCardCorrect
	}
	private final static Pattern TCP_PATTERN = Pattern
	.compile("^\\d{11}$|^\\d{12}$");
	private final static Pattern ECP_PATTERN = Pattern
	.compile("^\\d{8}$");
	private final static Pattern ICP_PATTERN = Pattern
	.compile("^\\d{2}-\\d{4}$");
	private final static Pattern FYR_PATTERN = Pattern
	.compile("^\\d{10}$");
	
	public String getCardNumber() {
		return cardNumber;
	}
	public String getCardType() {
		return cardType;
	}
	
	public CorporateCard(String cardType, String cardNumber) {
		this.cardNumber = cardNumber;
		this.cardType = cardType;
		
	}

	public CorporateCardFeedBackTypes validateCustomerParameter() {
		if(StringUtils.equalsIgnoreCase(TCP, cardType)){
			if (!TCP_PATTERN.matcher(cardNumber).matches()) {
				cardNumber = "";
				return CorporateCardFeedBackTypes.TcpNumberError;
			}			
		}
		if(StringUtils.equalsIgnoreCase(ECP, cardType)){
			if (!ECP_PATTERN.matcher(cardNumber).matches()) {
				cardNumber = "";
				return CorporateCardFeedBackTypes.EcpNumberError;
			}			
		}
		if(StringUtils.equalsIgnoreCase(ICP, cardType)){
			if (!ICP_PATTERN.matcher(cardNumber).matches()) {
				cardNumber = "";
				return CorporateCardFeedBackTypes.IcpNumberError;
			}			
		}
		if (StringUtils.equalsIgnoreCase(FCP, cardType)) {
			
			if (!FYR_PATTERN.matcher(cardNumber).matches()) {
				cardNumber = "";
				return CorporateCardFeedBackTypes.FYRNumberError;
			}	
		}
		
		
		return CorporateCardFeedBackTypes.CorporateCardCorrect;
	}
}
