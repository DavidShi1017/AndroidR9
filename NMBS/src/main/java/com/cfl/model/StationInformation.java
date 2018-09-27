package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StationInformation implements Serializable{

	private static final long serialVersionUID = 1L;

	
	@SerializedName("Title")
	private String title;
	
	@SerializedName("Content")
	private String content;
	
	@SerializedName("Image")
	private String image;

	public StationInformation(String title, String content, String image){
		this.title = title;
		this.content = content;
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getImage() {
		return image;
	}
	
	
	
}
