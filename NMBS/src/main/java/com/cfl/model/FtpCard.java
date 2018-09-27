package com.cfl.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class FtpCard implements Serializable{

	private static final long serialVersionUID = 1L;
	public enum FtpCardType{
		Thalys,
		Eurostar,
		Unkown
	}
	@SerializedName("Number")
	private String number;
	@SerializedName("Type")
	private FtpCardType ftpCardType;
	public String getNumber() {
		return number;
	}
	public FtpCardType getFtpCardType() {
		return ftpCardType;
	}
	public FtpCard(String number, FtpCardType ftpCardType){
		this.number = number;
		this.ftpCardType = ftpCardType;
	}
}
