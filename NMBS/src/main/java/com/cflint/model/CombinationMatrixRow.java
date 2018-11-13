package com.cflint.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data model implementation record CombinationMatrixRow from server.
 */
public class CombinationMatrixRow implements Serializable{

	private static final long serialVersionUID = 1L;
	private String offerInfoId;
	private Map<String,List<String>> notCombinableIds = new HashMap<String, List<String>>();

	public String getOfferInfoId() { 
		return offerInfoId;
	}
	public Map<String, List<String>> getNotCombinableIds() {
		return notCombinableIds;
	}
	

	public void putNotCombinableIds(String key, List<String> values) {
		 notCombinableIds.put(key, values);
	}
	
	public CombinationMatrixRow(String offerInfoId,
			Map<String, List<String>> notCombinableIds) {
		super();
		this.offerInfoId = offerInfoId;
		this.notCombinableIds = notCombinableIds;
	}
		
}
