package com.nmbs.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Collection of messages explaining the different actions execute 
 * while handling a request.
 * @author David.Shi
 */
public class DebugMessage implements Serializable{

	private static final long serialVersionUID = -3882628653971418823L;
	@SerializedName("Origin")
	private String origin;
	@SerializedName("Type")
	private String type;
	@SerializedName("Value")
	private String value;
	public DebugMessage(String origin, String type, String value) {
		super();
		this.origin = origin;
		this.type = type;
		this.value = value;
	}
	public String getOrigin() {
		return origin;
	}
	public String getType() {
		return type;
	}
	public String getValue() {
		return value;
	}
		
}
