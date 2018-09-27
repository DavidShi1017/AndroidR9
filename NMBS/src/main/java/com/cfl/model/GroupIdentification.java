package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class GroupIdentification implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("Id")
	private String id;
	@SerializedName("Title")
	private String title;
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public GroupIdentification(String id, String title){
		this.id = id;
		this.title = title;
	}
}
