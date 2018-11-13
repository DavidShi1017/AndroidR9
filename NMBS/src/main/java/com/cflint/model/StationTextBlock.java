package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StationTextBlock implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Content")
	private String content;

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

}
