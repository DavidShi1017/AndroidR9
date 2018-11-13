package com.cflint.model;

import java.io.Serializable;

public class RealTimeInfoResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id;
	private boolean isSuccess;
	private String content;
	private String realTimeType;

	public RealTimeInfoResponse(String id, boolean isSuccess, String content, String realTimeType){
		this.id = id;
		this.isSuccess = isSuccess;
		this.content = content;
		this.realTimeType = realTimeType;
	}

	public String getId(){
		return this.id;
	}

	public boolean getIsSuccess(){
		return this.isSuccess;
	}

	public String getContent(){
		return content;
	}

	public String getRealTimeType(){
		return this.realTimeType;
	}

	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}
