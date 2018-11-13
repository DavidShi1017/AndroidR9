package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * The class Station reprensents a train station.
 * This class holds the general information that identifies the train station.
 * @author David.shi
 */
public class Station implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String BE_CODE = "BE";
	public static final String FR_CODE = "FR";
	public static final String DE_CODE = "DE";
	public static final String NL_CODE = "NL";
	public static final String LU_CODE = "LU";
	public static final String GB_CODE = "GB";

	@SerializedName("Code")
	private String code;

	@SerializedName("Name")
	private String name;

	@SerializedName("DetailInfoPath")
	private String detailInfoPath;

	@SerializedName("Destination")
	private String destination;

	@SerializedName("Synoniem")
	private String synoniem;

	@SerializedName("Query")
	private String query;

	@SerializedName("StationboardEnabled")
	private boolean stationboardEnabled;

	public Station(String code, String name, String detailInfoPath, String destination, String synoniem, String query){
		this.code = code;
		this.name = name;
		this.detailInfoPath = detailInfoPath;
		this.destination = destination;
		this.synoniem = synoniem;
		this.query = query;
	}

	public Station(String code, String name, String detailInfoPath, String destination, String synoniem, boolean stationboardEnabled, String query){
		this.code = code;
		this.name = name;
		this.detailInfoPath = detailInfoPath;
		this.destination = destination;
		this.synoniem = synoniem;
		this.query = query;
		this.stationboardEnabled = stationboardEnabled;
	}

	public Station(){

	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDetailInfoPath(String detailInfoPath) {
		this.detailInfoPath = detailInfoPath;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getName() {
		return name;
	}

	public String getDestination() {
		return destination;
	}

	public String getDetailInfoPath() {
		return detailInfoPath;
	}

	@Override
	public String toString() {
		return this.getName().toString();
	}

	public String getSynoniem() {
		return synoniem;
	}

	public String getQuery() {
		return query;
	}

	public void setSynoniem(String synoniem) {
		this.synoniem = synoniem;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean getStationBoardEnabled(){
		return this.stationboardEnabled;
	}


}
