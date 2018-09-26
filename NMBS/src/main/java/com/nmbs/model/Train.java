package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Train implements Serializable{


	private static final long serialVersionUID = 1L;

	@SerializedName("Category")
	private String category;
	
	@SerializedName("Number")
	private String number;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCategory() {
		return category;
	}

	public String getNumber() {
		return number;
	}
	
	public Train(String category, String number){
		this.category = category;
		this.number = number;
	}
}
