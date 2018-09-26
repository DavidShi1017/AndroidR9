package com.nmbs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RealTimeInfoRequestParameter implements Serializable{
	private static final long serialVersionUID = -6835818985161074348L;

	@SerializedName("Elements")
	private List<Object> realTimeInfoRequests;

	private transient Map<String, Object> map;

	public RealTimeInfoRequestParameter(List<Object> realTimeInfoRequests){
		this.realTimeInfoRequests = realTimeInfoRequests;
	}

	public List<Object> getRealTimeInfoRequests(){
		return this.realTimeInfoRequests;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public Map<String, Object> getMap() {
		return map;
	}
}
