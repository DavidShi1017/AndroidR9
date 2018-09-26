package com.nmbs.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.util.Log;


import com.nmbs.dataaccess.database.MessageDatabaseService;
import com.nmbs.dataaccess.restservice.IMessageDataService;
import com.nmbs.dataaccess.restservice.impl.MessageDataService;
import com.nmbs.model.MobileMessage;
import com.nmbs.model.MobileMessageResponse;
import com.nmbs.services.IMessageService;
import com.nmbs.services.ISettingService;
import com.nmbs.util.DateUtils;
import com.nmbs.util.Utils;


public class MessageService implements IMessageService {
	
	private Context applicationContext;
	private MobileMessageResponse messageResponse;
	private boolean isShowCheckUpdate = false;

	private final static String TAG = MessageService.class.getSimpleName();
	private final static String MESSAGE_SHAREDPREFERENCES_KEY = "Message_Info";
	
	private final static String MESSAGE_NULL_KEY = "IsNullMessageResponse";
	public MessageService(Context context){
		this.applicationContext = context;
	}
	
	public boolean isShowCheckUpdate() {
		return isShowCheckUpdate;
	}

	
	public void setShowCheckUpdate(boolean isShowCheckUpdate) {
		this.isShowCheckUpdate = isShowCheckUpdate;
	}
	
	public void getMessageData(String language, boolean isChangeLanguage){
				
		MessageDataService messageDataService = new MessageDataService();
		MobileMessageResponse response = null;
		try {
			messageDataService.getMessageResponse(applicationContext, language, isChangeLanguage);
		} catch (Exception e) {
			e.printStackTrace();
			if(isChangeLanguage){
				//Log.e("Messages", "No MobileMessageResponse...");
				messageResponse = null;
				messageDataService.deleteMessages(applicationContext);
				messageDataService.cleanLastModifiedTime(applicationContext);
				deleteMessageState();
			}
		}
	}

	public MobileMessageResponse readMobileMessages(){
		//Log.e("Messages", "readMobileMessages...");
		MessageDatabaseService databaseService = new MessageDatabaseService(applicationContext);		
		List<MobileMessage> mobileMessages = databaseService.readMessageList();
		messageResponse = new MobileMessageResponse(mobileMessages);
		//Log.e("Messages", "readMobileMessages..."+ messageResponse);
		return messageResponse;
	}

	public MobileMessageResponse getMessageResponse() {
		//if (messageResponse == null) {
			readMobileMessages();
		//}
		return messageResponse;
	} 
	
	
	public void saveMessageStateInWichTab(String whichTab){
		SharedPreferences messageInfo = applicationContext.getSharedPreferences(MESSAGE_SHAREDPREFERENCES_KEY, 0);  
		Date date = new Date();
		String dateString = DateUtils.dateTimeToString(date);
		messageInfo.edit().putString(whichTab, dateString).commit();  
	}

	public void saveMessageNextDisplay(List<MobileMessage> messages){
		Date date = new Date();
		Date afterHours = null;
		String nextDisplay = "";
		MessageDatabaseService databaseService = new MessageDatabaseService(applicationContext);
		for (MobileMessage message : messages) {
			if (message != null){
				afterHours = DateUtils.getAfterManyHour(date, message.getRepeatDisplayInOverlay());
				nextDisplay = DateUtils.dateTimeToString(afterHours);
				databaseService.updateMessageNextDisplay(message.getId(), nextDisplay);
				//Log.d(TAG, "Saved message id is===" + message.getId() + "===and its nextDisplay is===" + nextDisplay);
			}else{
				//Log.e(TAG, "This message is null...");
			}
		}
	}

	public void deleteMessageState(){
		SharedPreferences messageInfo = applicationContext.getSharedPreferences(MESSAGE_SHAREDPREFERENCES_KEY, 0);  
		messageInfo.edit().clear().commit();
	}
	
	public boolean isShowed(String whichTab){
		boolean isShowed = false;
		
		SharedPreferences messageInfo = applicationContext.getSharedPreferences(MESSAGE_SHAREDPREFERENCES_KEY, 0); 
		String ShowedTimeString = messageInfo.getString(whichTab, "");  
		
		if (StringUtils.isEmpty(ShowedTimeString)) {
			isShowed = false;
			return isShowed;
		}
        Date showedDateTime = DateUtils.stringToDateTime(ShowedTimeString);
        Date showedDateTimeAfterSixHours = DateUtils.getAfterManyHour(showedDateTime, 6);
        //System.out.println("showedDateTimeAfterSixHours======" + showedDateTimeAfterSixHours);
        Date date = new Date();
        if (showedDateTimeAfterSixHours.before(date)) {
        	isShowed = false;
		}else {
			isShowed = true;
		}
        //System.out.println("showedDateTimeAfterSixHours======" + date);
        //System.out.println("isShowed======" + isShowed);
		return isShowed;
	}
	
	public boolean isShowOverLay(String whichTab,MobileMessageResponse mobileMessageResponse){
		boolean isSHowOverLay = true;
		/*List<MobileMessage> mobileMessages = messageResponse.getMobileMessagesByAutoOverlayDisplayLocation(whichTab);
		if (mobileMessages != null) {
			for(MobileMessage mobileMessage:mobileMessages){
				if(!isShowed(mobileMessage.getId())){
					isSHowOverLay = false;
					return isSHowOverLay;
				}
			}
		}*/
		return isSHowOverLay;
	}
	
	public MobileMessageResponse getMobileMessagesByAutoOverlayDisplayLocation(String whichTab){
		/*List<MobileMessage> mobileMessages = getMessageResponse().getMobileMessagesByAutoOverlayDisplayLocation(whichTab);
		MobileMessageResponse mobileMessageResponse = new MobileMessageResponse(mobileMessages);
		if (mobileMessages.size() < 1) {
			return null;
		}*/
		return null;
	}
	
	public void saveShouldShowMessage(boolean isNull){
		
		SharedPreferences messageInfo = applicationContext.getSharedPreferences(MESSAGE_NULL_KEY, 0); 
		SharedPreferences.Editor editor = messageInfo.edit();
		editor.putBoolean("IsNullMessageResponse", isNull);
		editor.commit();	
		//Log.e(TAG, "save IsNullMessageResponse=====" + isNull);
	}
	
	public boolean shouldShowMessage(){
		SharedPreferences messageInfo = applicationContext.getSharedPreferences(MESSAGE_NULL_KEY, 0); 
		boolean isNullMessageResponse = messageInfo.getBoolean("IsNullMessageResponse", false);
		//Log.e(TAG, "IsNullMessageResponse=====" + isNullMessageResponse);
		return isNullMessageResponse;
	}

	public List<MobileMessage> getShowMessage(List<MobileMessage> messages){
		List<MobileMessage> showMessages = new ArrayList<>();

		Date date = new Date();
		for (MobileMessage message : messages) {
			String nextDisplay = message.getNextDisplay();
			//Log.e(TAG, "nextDisplay is:::" + nextDisplay);
			if(nextDisplay == null || nextDisplay.isEmpty()){
				//Log.e(TAG, "message.isDisplayInOverlay() is:::" + message.isDisplayInOverlay());
				if(message.isDisplayInOverlay()){
					showMessages.add(message);
				}
			}else{
				Date display = DateUtils.stringToDateTime(nextDisplay);
				if (date.after(display) && message.isDisplayInOverlay() && message.getRepeatDisplayInOverlay() != 0){
					showMessages.add(message);
				}
			}
		}
		return showMessages;
	}
}
