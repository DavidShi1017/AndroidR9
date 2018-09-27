package com.cfl.model;

import java.io.Serializable;
import java.util.Date;

;
public class ClickToCallContext implements Serializable{

	private static final long serialVersionUID = 1L;
	private String dnr;
	private String scenario;
	private String language;
	private Date timeStamp;
	public String getDnr() {
		return dnr;
	}
	public void setDnr(String dnr) {
		this.dnr = dnr;
	}
	public String getScenario() {
		return scenario;
	}
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
