package com.cflint.model;

import com.google.gson.annotations.SerializedName;

/**
 * The class Origin represents the location where the traveler starts its journey.
 * @author David.shi
 */
public class Origin {
	
	@SerializedName("Code")
	private String code;
	@SerializedName("Name")
	private String name;
	
	@SerializedName("Synonyms")
	private String synoniem;
	
	public Origin(String code, String name, String synoniem){
		this.code = code;
		this.name = name;
		this.synoniem = synoniem;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getSynoniem() {
		return synoniem;
	}

}
