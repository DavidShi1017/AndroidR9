package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PromoParameter implements Serializable{
	
    
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Promotion")
	private Promo promo;

	public Promo getPromo() {
		return promo;
	}
	
	public PromoParameter(Promo promo){
		this.promo = promo;
	}
}
