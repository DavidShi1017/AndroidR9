package com.cflint.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The class RestResponse is the base class for all different types of responses 
 * the server sent by the server in reply to a request. 
 * @author David.shi
 */
public class RealTimeConnectionResponse implements Serializable{

	private static final long serialVersionUID = 1845709995151595360L;
	@SerializedName("Connection")
	protected RealTimeConnection realTimeConnection;
	@SerializedName("Messages")
	protected List<Message> messages = new ArrayList<Message>();
	@SerializedName("DebugMessages")
	protected List<DebugMessage> debugMessages = new ArrayList<DebugMessage>();

	public RealTimeConnectionResponse() {

	}
	public RealTimeConnectionResponse(RealTimeConnection realTimeConnection,List<Message> messages, List<DebugMessage> debugMessages) {
		super();
		this.realTimeConnection = realTimeConnection;
		this.messages = messages;
		this.debugMessages = debugMessages;
	}
	public RealTimeConnection getRealTimeConnection(){
		return this.realTimeConnection;
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
