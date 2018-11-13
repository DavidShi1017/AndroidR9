package com.cflint.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;



public class ScheduleResponse extends RestResponse {
	
	private static final long serialVersionUID = 1L;

	@SerializedName("OriginStationRcode")
	private String originStationRcode;

	@SerializedName("OriginStationName")
	private String originStationName;

	@SerializedName("DestinationStationRcode")
	private String destinationStationRcode;
	
	@SerializedName("DestinationStationName")
	private String destinationStationName;
	
	@SerializedName("Connections")
	private List<RealTimeConnection> connections = new ArrayList<RealTimeConnection>();

	@SerializedName("UserMessages")
	private List<UserMessage> userMessages = new ArrayList<UserMessage>();

	public String getOriginStationRcode() {
		return originStationRcode;
	}

	public String getOriginStationName() {
		return originStationName;
	}

	public String getDestinationStationRcode() {
		return destinationStationRcode;
	}

	public String getDestinationStationName() {
		return destinationStationName;
	}

	public List<RealTimeConnection> getConnections() {
		return connections;
	}

	public List<UserMessage> getUserMessages() {
		return userMessages;
	}

	public void setUserMessages(List<UserMessage> userMessages){
		this.userMessages = userMessages;
	}

}
