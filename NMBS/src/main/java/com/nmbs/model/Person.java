package com.nmbs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data model implementation record User info from server.
 */
public class Person implements Serializable{

	private static final long serialVersionUID = -5458853700565771114L;
	private int personId;
	private int customerId;
	private Account accountInfo;
	private List<Message> messages = new ArrayList<Message>();
	private List<DebugMessage> debugMessages = new ArrayList<DebugMessage>();
	
	public Person(int personId, int customerId, Account accountInfo,
			List<Message> messages, List<DebugMessage> debugMessages) {
		super();
		this.personId = personId;
		this.customerId = customerId;
		this.accountInfo = accountInfo;
		this.messages = messages;
		this.debugMessages = debugMessages;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getPersonId() {
		return personId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public Account getAccountInfo() {
		return accountInfo;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public List<DebugMessage> getDebugMessages() {
		return debugMessages;
	}
}
