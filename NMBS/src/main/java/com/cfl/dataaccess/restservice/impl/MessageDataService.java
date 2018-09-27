package com.cfl.dataaccess.restservice.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;



import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.cfl.R;
import com.cfl.activity.SettingsActivity;
import com.cfl.dataaccess.converters.MessageResponseConverter;
import com.cfl.dataaccess.database.MessageDatabaseService;
import com.cfl.dataaccess.restservice.IMessageDataService;

import com.cfl.model.MobileMessage;
import com.cfl.model.MobileMessageResponse;
import com.cfl.util.FileManager;
import com.cfl.util.HTTPRestServiceCaller;

import com.cfl.util.SharedPreferencesUtils;

public class MessageDataService implements IMessageDataService {
	public final static String LAST_MODIFIED_TIME = "last_modified_message_time";  
	public final static String SERVER_LAST_MODIFIED_TIME = "server_last_modified_message_time";  
	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	MessageResponseConverter messageResponseConverter = new MessageResponseConverter();
	private static final String TAG = MessageDatabaseService.class.getSimpleName();
	
	public MobileMessageResponse getMessageResponse(Context context, String language, boolean isChangeLanguage) throws Exception {
		Log.e("Messages", "getMessageResponse...." + language);
		String stringHttpResponse = null;
		MobileMessageResponse messageResponse = null;
		String urlString = context.getString(R.string.server_url_get_messages);

		String lastModifiedInPreferences = readLastModifiedTime(context);
		if (isChangeLanguage) {
			lastModifiedInPreferences = "";
		}
		try{
			stringHttpResponse = httpRestServiceCaller.executeHTTPRequest(context,
					null, urlString, language, HTTPRestServiceCaller.HTTP_GET_METHOD, 6000, true,
					lastModifiedInPreferences, HTTPRestServiceCaller.API_VERSION_VALUE_6);
			//Log.e("Messages", "stringHttpResponse...." + stringHttpResponse);

			/*stringHttpResponse = "{\n" +
					"   \"MobileMessages\":[\n" +
					"      {\n" +
					"         \"Id\":\"001\",\n" +
					"         \"MessageType\":\"FUNCTIONAL\",\n" +
					"         \"Title\":\"test message 1\",\n" +
					"         \"Description\":\"test description test description test description test description\",\n" +
					"         \"Validity\":\"validity\",\n" +
					"         \"LowResImage\":\"/~/media/ImagesNew/OnlyRail1/Banners/Amtrak_FR.ashx\",\n" +
					"\t \"HighResImage\":\"/~/media/ImagesNew/OnlyRail1/Banners/Amtrak_FR.ashx\",\n" +
					"\t \"IncludeActionButton\":true,\n" +
					"\t \"ActionButtonText\":\"action button text\",\n" +
					"\t \"Hyperlink\":\"https://www.accept.b-europe.com/EN/Booking/Mobile?affiliation=App&utm_campaign=beurope_app&utm_medium=referral_app&utm_source=beurope_app\",\n" +
					"         \"DisplayInOverlay\":false,\n" +
					"\t \"DisplayInOverlay\":false,\n" +
					"\t \"NavigationInNormalWebView\":false,\n" +
					"\t \"LowResIcon\":\"/~/media/ImagesNew/Icons/100x100_promo2.ashx\",\n" +
					"         \"HighResIcon\":\"/~/media/ImagesNew/Icons/100x100_promo2.ashx?sc=0.5\"\n" +
					"      }\n" +
					"   ],\n" +
					"   \"Messages\":[\n" +
					" \n" +
					"   ],\n" +
					"   \"DebugMessages\":[\n" +
					" \n" +
					"   ]\n" +
					"}";*/
			if (!"304".equalsIgnoreCase(stringHttpResponse)) {
				Map<String, String> lastModified = httpRestServiceCaller.getLastModified();

				messageResponse = messageResponseConverter.parseMessage(stringHttpResponse);
				MessageDatabaseService databaseService = new MessageDatabaseService(context);
				if (messageResponse != null && messageResponse.getMobileMessages() != null) {
					//System.out.println("&&&&&&&&&&&&&&&&"+messageResponse.getMobileMessages().size());
					if (lastModified != null) {
						saveLastModifiedTime(context, lastModified.get(urlString));
					}
					List<MobileMessage> messagesInDatabase = databaseService.readMessageList();
					List<MobileMessage> messagesInService = messageResponse.getMobileMessages();
					for (MobileMessage message : messagesInDatabase) {
						if (message != null && isNolongerId(message.getId(), messagesInService)){
							databaseService.deleteMessate(message.getId());
						}
					}
					messagesInDatabase = databaseService.readMessageList();
					for (MobileMessage messageInDatabase : messagesInDatabase) {
						for (MobileMessage messageInService : messagesInService) {
							if (messageInDatabase != null && messageInService != null &&
									messageInDatabase.getId().equalsIgnoreCase(messageInService.getId())){
								messageInService.setNextDisplay(messageInDatabase.getNextDisplay());
							}
						}
					}
					databaseService.insertMessageList(messagesInService);
				}else{
					throw new Exception();
				}
			}
		}catch (Exception e){
			throw new Exception();
		}

		return messageResponse;
	}

	public void deleteMessages(Context context){
		MessageDatabaseService databaseService = new MessageDatabaseService(context);
		databaseService.deleteMessages();
	}

	private boolean isNolongerId(String messageId, List<MobileMessage> messages){
		boolean isNolongerId = false;
		for (MobileMessage message : messages) {
			if(messageId.equalsIgnoreCase(message.getId())){
				isNolongerId = false;
			}else {
				isNolongerId = true;
			}
		}
		return  isNolongerId;
	}

	private String readLastModifiedTime(Context context){
		
		SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
		
		String lastModified = settings.getString(SERVER_LAST_MODIFIED_TIME, "").toString();
		return lastModified;
	}
	
	private void saveLastModifiedTime(Context context, String lastModified){
		if (lastModified != null && lastModified.indexOf(':') != -1) {
			lastModified = lastModified.substring(lastModified.indexOf(':') + 2);
		}			
		
		SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);
		if(!"".equals(lastModified)||!"".equals(lastModified.trim()))
		settings.edit().putString(SERVER_LAST_MODIFIED_TIME, lastModified.trim()).commit();
		
	}
	public void cleanLastModifiedTime(Context context){

		//Log.d("Station" ,"cleanLastModifiedTime......... "  );
		SharedPreferences settings = context.getSharedPreferences(LAST_MODIFIED_TIME, 0);

		//settings.getAll().clear();
		settings.edit().clear().commit();

	}
	
	public void storeDefaultData(Context context){
		String sharedLocale = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_LANGUAGE,context);
		
		storeMessageData(context, sharedLocale);
		
	}
	
	private void storeMessageData(Context context, String language){
		int resourcesId = getMessageResource(language);
		InputStream is = context.getResources().openRawResource(resourcesId);  
		String stringHttpResponse = FileManager.getInstance().readFileWithInputStream(is);
		//Log.e(TAG, "storeMessageData....");
		MobileMessageResponse messageResponse = null;
		try {
			messageResponse = messageResponseConverter.parseMessage(stringHttpResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if (messageResponse != null) {
			MessageDatabaseService databaseService = new MessageDatabaseService(context);
			List<MobileMessage> messages = databaseService.readMessageList();
			if (messages == null || messages.size() == 0) {
				//Log.e(TAG, "insertMessageData....");
				databaseService.insertMessageList(messageResponse.getMobileMessages());
			}
			
		}
	}
	
	private int getMessageResource(String language){
		int resourcesId = 0;
		/*if (StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_EN)) {
			resourcesId = R.raw.message_en;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_FR)){
			resourcesId = R.raw.message_fr;
		}else if(StringUtils.equalsIgnoreCase(language, LocaleChangedUtils.LANGUAGE_NL)){
			resourcesId = R.raw.message_nl;
		}else {				
			resourcesId = R.raw.clicktocall_de;
		}*/
		return resourcesId;
	}

}
