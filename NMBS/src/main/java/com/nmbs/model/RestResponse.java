package com.nmbs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.annotations.SerializedName;

/**
 * The class RestResponse is the base class for all different types of responses 
 * the server sent by the server in reply to a request. 
 * @author David.shi
 */
public class RestResponse implements Serializable{

	private static final long serialVersionUID = 1845709995151595360L;
	@SerializedName("Messages")
	protected List<Message> messages = new ArrayList<Message>();
	@SerializedName("DebugMessages")
	protected List<DebugMessage> debugMessages = new ArrayList<DebugMessage>();
	
	public RestResponse() {
		
	}
	public RestResponse(List<Message> messages, List<DebugMessage> debugMessages) {
		super();
		this.messages = messages;
		this.debugMessages = debugMessages;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public List<DebugMessage> getDebugMessages() {
		return debugMessages;
	}
	public boolean add(DebugMessage object) {
		return debugMessages.add(object);
	}
	public void add(int location, Message object) {
		messages.add(location, object);
	}



}
