package com.cfl.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ClickToCallScenarioResponse extends RestResponse implements Serializable{

	private static final long serialVersionUID = 1L;

	@SerializedName("Scenarios")
	private List<ClickToCallScenario> clickToCallScenario;

	public List<ClickToCallScenario> getClickToCallScenario() {
		return clickToCallScenario;
	}
	
	
}
