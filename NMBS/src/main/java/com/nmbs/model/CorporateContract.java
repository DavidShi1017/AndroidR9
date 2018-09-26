package com.nmbs.model;

import java.io.Serializable;

public class CorporateContract implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private String number;
	
	public CorporateContract(String code, String name, String number){
		
		this.code = code;
		this.name = name;
		this.number = number;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}
	
	
}
