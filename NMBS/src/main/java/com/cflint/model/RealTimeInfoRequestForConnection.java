package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class RealTimeInfoRequestForConnection implements Serializable{
	private static final long serialVersionUID = -6835818985161074348L;
	
	@SerializedName("Id")
	private String id;

	@SerializedName("Context")
	private String context;

	@SerializedName("ReconCtx")
	private String reconCtx;

	@SerializedName("DepartureDate")
	private Date departureDate;

	public RealTimeInfoRequestForConnection(String id, String context, String reconCtx, Date departureDate){
		this.id = id;
		this.context = context;
		this.reconCtx = reconCtx;
		this.departureDate = departureDate;
	}

	public String getId(){
		return this.id;
	}

	public String getContext(){
		return this.context;
	}

	public Date getDepartureDate(){
		return this.departureDate;
	}
}
