package com.cfl.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Promotion implements Serializable{

	private static final long serialVersionUID = 1L;
	@SerializedName("StartDate")
	private Date startDate;
	@SerializedName("EndDate")
	private Date endDate;
	@SerializedName("Name")
	private String name;
	@SerializedName("Description")
	private String description;
	public Date getStartdate() {
		return startDate;
	}
	public Date getEnddate() {
		return endDate;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Promotion(String name, Date startdate, Date enddate, String description){
		this.name = name;
		this.startDate = startdate;
		this.endDate = enddate;
		this.description = description;
	}
}
