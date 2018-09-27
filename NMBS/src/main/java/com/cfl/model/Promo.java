package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Promo implements Serializable{
	
    
	private static final long serialVersionUID = 1L;

	@SerializedName("Code")
    private String code;
	@SerializedName("AddPromotion")
	private boolean addpromotion;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isAddpromotion() {
		return addpromotion;
	}
	public void setAddpromotion(boolean addpromotion) {
		this.addpromotion = addpromotion;
	}	
}
