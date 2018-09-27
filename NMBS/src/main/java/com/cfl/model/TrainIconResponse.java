package com.cfl.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TrainIconResponse extends RestResponse {
	private static final long serialVersionUID = 1L;
	@SerializedName("TrainIcons")
	private List<TrainIcon> trainIcons;
	public List<TrainIcon> getTrainIcons() {
		return trainIcons;
	}
	
	
}
