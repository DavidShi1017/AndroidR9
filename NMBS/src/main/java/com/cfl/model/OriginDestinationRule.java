package com.cfl.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The class OriginDestinationRule represents a origin-destination rule 
 * configuration as specified by the user, stored and managed in the 
 * Content Management System. 
 */
public class OriginDestinationRule extends RestResponse {

	private static final long serialVersionUID = 1L;
	@SerializedName("ApplicableForReverse")
	private boolean applicableForReverse;
	
	@SerializedName("Origins")
	private List<Origin> origins = new ArrayList<Origin>();
	@SerializedName("Destinations")
	private List<Destination> destinations = new ArrayList<Destination>();
	public OriginDestinationRule(List<Origin> origins, List<Destination> destinations){
		this.origins = origins;
		this.destinations = destinations;
	}

	public List<Origin> getOrigins() {
		return origins;
	}

	public boolean isApplicableForReverse() {
		return applicableForReverse;
	}
	
	public List<Destination> getDestinations() {
		return destinations;
	}
}
