package com.cfl.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GreenPointsResponse extends RestResponse {

	private static final long serialVersionUID = 1L;
	
	@SerializedName("CorporateCards")
	private List<CorporateCard> listCorporateCards = new ArrayList<CorporateCard>();
	
	public GreenPointsResponse(List<CorporateCard> corporateCards){
		this.listCorporateCards = corporateCards;
	}

	public List<CorporateCard> getCorporateCards() {
		return listCorporateCards;
	}
	
	
}
