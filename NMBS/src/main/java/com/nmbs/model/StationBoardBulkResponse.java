package com.nmbs.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StationBoardBulkResponse extends RestResponse{


	private static final long serialVersionUID = 1L;
	@SerializedName("Elements")
	private List<StationBoardBulk> stationBoardBulks = new ArrayList<StationBoardBulk>();

	public List<StationBoardBulk> getStationBoardBulks() {
		return stationBoardBulks;
	}
}
