package com.nmbs.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OriginDestinationResponse {
	
	@SerializedName("OriginDestinationRules")
	
	private List<OriginDestinationRule> originDestinationRules = new ArrayList<OriginDestinationRule>();
	
	public OriginDestinationResponse(List<OriginDestinationRule> originDestinationRules){
		this.originDestinationRules = originDestinationRules;
	}
	public List<OriginDestinationRule> getOriginDestinationRules() {
		return originDestinationRules;
	}
	
}
