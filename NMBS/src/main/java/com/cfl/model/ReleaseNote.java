package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ReleaseNote implements Serializable {
	private static final long serialVersionUID = 1L;
	@SerializedName("Description")
	private String description;
	public ReleaseNote(String description){
		this.description = description;
	}
	public String getDescription() {
		return description;
	}

}
