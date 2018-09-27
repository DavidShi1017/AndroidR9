package com.cfl.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TariffDetail implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@SerializedName("Id")
	private String id;
	
	@SerializedName("IncludesEBS")
	private boolean includesEBS;
	
	@SerializedName("DisplayText")
	private String displayText;
	
	@SerializedName("FlexInfo")
	private List<InfoText> infoTexts;

	public String getId() {
		return id;
	}

	public boolean isIncludesEBS() {
		return includesEBS;
	}

	public String getDisplayText() {
		return displayText;
	}

	public List<InfoText> getInfoTexts() {
		return infoTexts;
	}
}
