package com.cfl.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class FAQResponse extends RestResponse{


	private static final long serialVersionUID = 1L;
	
	@SerializedName("Categories")	
	private List<FAQCategory> categories;

	public List<FAQCategory> getCategories() {
		return categories;
	}
}
