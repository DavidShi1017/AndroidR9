package com.cflint.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StationBoardResponse extends RestResponse{
	private static final long serialVersionUID = 1L;
	
	@SerializedName("StationBoardRows")
	private List<StationBoardRow> stationBoardRows = new ArrayList<StationBoardRow>();

	public List<StationBoardRow> getStationBoardRows() {
		return stationBoardRows;
	}
}
