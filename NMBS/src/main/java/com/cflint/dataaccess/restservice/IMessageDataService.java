package com.cflint.dataaccess.restservice;



import android.content.Context;

import com.cflint.model.MobileMessageResponse;


public interface IMessageDataService {
	public MobileMessageResponse getMessageResponse(Context context, String language, boolean isChangeLanguage) throws Exception;
	public void storeDefaultData(Context context);
	public void deleteMessages(Context context);
}
