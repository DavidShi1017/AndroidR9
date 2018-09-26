package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class HafasMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("Header")
	private String header;
	
	@SerializedName("Text")
	private String text;

	@SerializedName("Lead")
	private String lead;

	@SerializedName("URL")
	private String url;

	@SerializedName("StationStart")
	private String stationStart;

	@SerializedName("StationEnd")
	private String stationEnd;

	public HafasMessage(String header, String text, String lead, String url, String stationStart, String stationEnd){
		this.header = header;
		this.text = text;
		this.lead = lead;
		this.url = url;
		this.stationStart = stationStart;
		this.stationEnd = stationEnd;
	}

	public String getHeader() {
		return header;
	}


	public String getText() {
		return text;
	}

	public String getLead(){
		return this.lead;
	}

	public String getUrl(){
		return url;
	}

	public String getStationStart(){
		if(stationStart == null){
			return "";
		}
		return stationStart;
	}

	public String getStationEnd(){
		if(stationEnd == null){
			return "";
		}
		return stationEnd;
	}
	
	
}
