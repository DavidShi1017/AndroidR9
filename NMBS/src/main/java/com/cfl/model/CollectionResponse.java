package com.cfl.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The CollectionResponse class is a subclass of the RestResponse class. 
 * The CollectionResponse contains the actual response payload sent by the 
 * server in reply to a request for Country, Title or Language master data.
 * @author David.shi
 */
public class CollectionResponse extends RestResponse {

	private static final long serialVersionUID = 1L;
 
	@SerializedName("Items")
	private List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();
	public CollectionResponse(List<CollectionItem> collectionItems){
		this.collectionItems = collectionItems;
	}
	public List<CollectionItem> getCollectionItems() {
		return collectionItems;
	}
	
	
}
