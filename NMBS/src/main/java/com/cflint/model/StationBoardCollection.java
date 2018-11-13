package com.cflint.model;

import java.io.Serializable;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StationBoardCollection implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	@Expose
	@SerializedName("Elements")
	private List<StationBoard> stationBoards;

	public List<StationBoard> getStationBoards() {
		return stationBoards;
	}
	
	public StationBoardCollection(List<StationBoard> stationBoards){
		this.stationBoards = stationBoards;
	}
}
