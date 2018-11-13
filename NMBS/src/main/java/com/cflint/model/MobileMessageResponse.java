package com.cflint.model;

import android.util.Log;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

public class MobileMessageResponse extends RestResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	
	
	public final static String MESSAGETYPE_FUNCTIONAL = "FUNCTIONAL";
	public final static String MESSAGETYPE_COMMERCIAL = "COMMERCIAL";
	public final static String MESSAGETYPE_RAILWAY = "RAILWAY";
	

	
	

	@SerializedName("MobileMessages")
	private List<MobileMessage> mobileMessages;
	
	public MobileMessageResponse(List<MobileMessage> mobileMessages){
		this.mobileMessages = mobileMessages;
	}

	public List<MobileMessage> getMobileMessages() {
		return mobileMessages;
	}
	
	public List<MobileMessage> getMobileMessagesByType(String messageType){

		List<MobileMessage> mobileMessages = new ArrayList<MobileMessage>();
		for (MobileMessage mobileMessage : this.mobileMessages) {
			Log.e("mobileMessageResponse", "this.mobileMessages..." + mobileMessage.getMessageType());
			if (StringUtils.equalsIgnoreCase(messageType, mobileMessage.getMessageType())) {
				mobileMessages.add(mobileMessage);
			}
		}
		return mobileMessages;
	}
	
/*	public List<MobileMessage> getMobileMessagesByAutoOverlayDisplayLocation(String location){
		List<MobileMessage> mobileMessages = new ArrayList<MobileMessage>();
		for (MobileMessage mobileMessage : this.mobileMessages) {
			for (String displayLocation : mobileMessage.getRepeatDisplayInOverlay()) {
				if (StringUtils.equalsIgnoreCase(location, displayLocation)) {
					mobileMessages.add(mobileMessage);
				}
			}
			
		}
		return mobileMessages;
	}*/
}
