package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

/**
 * The class Destination represents the endpoint of a journey.
 * 
 * @author David.shi
 */
public class Destination {
	@SerializedName("Code")
	private String code;
	@SerializedName("Name")
	private String name;
	@SerializedName("Synonyms")
	private String synoniem;

	public Destination(String code, String name, String synoniem) {
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
