package com.cflint.model;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents a collection item. It holds an identification key 
 * unique within the requested master data subset. 
 * @author David.shi
 */
public class CollectionItem implements Serializable{

	private static final long serialVersionUID = 1L;

	@SerializedName("Key")
	private String key;
	
	@SerializedName("Label")
	private String lable;
	
	public CollectionItem(String key, String lable){
		this.key = key;
		this.lable = lable;
	}

	public String getKey() {
		return key;
	}

	public String getLable() {
		return lable;
	}
	
}
