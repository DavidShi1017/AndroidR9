package com.cflint.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Collection of messages added by the service handler to further clarify the 
 * problems and exceptions that occurred while processing the request.
 * @author David.Shi
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = -6835818985161074348L;
	@SerializedName("Description")
	private String description;
	@SerializedName("Severity")
	private String severity;
	@SerializedName("StatusCode")
	private String statusCode;
	@SerializedName("BackendErrorKey")
	private String backendErrorKey;
	public Message(String description, String severity, String statusCode) {
		super();
		this.description = description;
		this.severity = severity;
		this.statusCode = statusCode;
	}
	public String getDescription() {
		return description;
	}
	public String getSeverity() {
		return severity;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public String getBackendErrorKey() {
		return backendErrorKey;
	}
		
}
