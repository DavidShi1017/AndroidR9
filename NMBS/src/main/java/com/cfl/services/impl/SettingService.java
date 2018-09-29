package com.cfl.services.impl;

import com.cfl.R;

import com.cfl.application.NMBSApplication;
import com.cfl.async.MasterDataAsyncTask;
import com.cfl.dataaccess.converters.MasterResponseConverter;
import com.cfl.dataaccess.restservice.impl.MasterDataService;
import com.cfl.dataaccess.restservice.impl.MessageDataService;
import com.cfl.dataaccess.restservice.impl.StationInfoDataService;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NetworkError;
import com.cfl.handler.RestMasterDataHandler;
import com.cfl.listeners.ActivityPostExecuteListener;
import com.cfl.model.CollectionItem;
import com.cfl.model.CollectionResponse;
import com.cfl.model.Currency;
import com.cfl.preferences.SettingsPref;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.ISettingService;
import com.cfl.util.AESUtils;
import com.cfl.util.AppLanguageUtils;
import com.cfl.util.FileManager;
import com.cfl.util.SharedPreferencesUtils;
import com.cfl.util.TrackerConstant;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.Message;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Control Offer Data and View communicate.
 */
public class SettingService implements ISettingService {
	private Context applicationContext;
	private static final String DEFAULT_CURRENCY_CODE = "EUR";
	private Currency currency = null;
	private static final String TAG = SettingService.class.getSimpleName();
	public SettingService(Context context) {
		this.applicationContext = context;
	}

	public static final String LANGUAGE_NL = "NL_BE";
	public static final String LANGUAGE_FR = "FR_BE";
	public static final String LANGUAGE_EN = "EN_GB";
	public static final String LANGUAGE_DE = "DE_BE";




	public void setEmail(String email){
		try {
			SettingsPref.saveSettingsEmail(this.applicationContext, AESUtils.encrypt(email));
		}catch (Exception e){
			//Log.e(TAG, "Encrypt error.." + e.getMessage());
		}

	}

	public void setPwd(String pwd){
		try {
			SettingsPref.saveSettingsPassword(this.applicationContext, AESUtils.encrypt(pwd));
		}catch (Exception e){
			//Log.e(TAG, "Encrypt error.." + e.getMessage());
		}
	}

	public String getEmail(){
		String email = SettingsPref.getSettingsEmail(this.applicationContext);
		try {
			email = AESUtils.decrypt(email);
		}catch (Exception e){
			//Log.e(TAG, "Decrypt error.." + e.getMessage());
		}
		return email;
	}

	public String getPwd(){
		String pwd = SettingsPref.getSettingsPassword(this.applicationContext);
		try {
			pwd = AESUtils.decrypt(pwd);
		}catch (Exception e){
			//Log.e(TAG, "Decrypt error.." + e.getMessage());
		}
		return pwd;
	}

	public void deletePersonalData(){
		SettingsPref.deletePersonalData(this.applicationContext);
	}
	public void saveAutoUpdate(boolean isChecked){
		SettingsPref.saveAutoUpdate(this.applicationContext, isChecked);
	}
	public boolean isAutoUpdate(){
		return SettingsPref.isAutoUpdate(this.applicationContext);
	}

	public void saveTravelReminders(boolean isChecked){
		SettingsPref.saveTravelReminders(this.applicationContext, isChecked);
	}
	public boolean isTravelReminders(){
		return SettingsPref.isTravelReminders(this.applicationContext);
	}

	public void saveDnr(boolean isChecked){
		SettingsPref.saveDnr(this.applicationContext, isChecked);
	}
	public boolean isDnr(){
		return SettingsPref.isDnr(this.applicationContext);
	}

	public void saveOptions(boolean isChecked){
		SettingsPref.saveOption(this.applicationContext, isChecked);
	}
	public boolean isOptions(){
		return SettingsPref.isOption(this.applicationContext);
	}

	public void saveFacebookTrack(boolean isChecked){
		SettingsPref.saveFacebookTrack(this.applicationContext, isChecked);
	}
	public void save3rdTrack(boolean isChecked){
		SettingsPref.save3rdTrack(this.applicationContext, isChecked);
	}
	public boolean isFacebookTrack(){
		return SettingsPref.isFacebookTrack(this.applicationContext);
	}

	public boolean is3rdTrack(){
		return SettingsPref.is3rdTrack(this.applicationContext);
	}

	public void setStartNotifiTime(String time){
		SettingsPref.saveStartNotifiTime(this.applicationContext, time);
	}
	public String getStartNotifiTime(){
		return SettingsPref.getStartNotifiTime(this.applicationContext);
	}
	public int getStartNotifiTimeIntger(){
		int startTime = 30;
		int space = 0;
		String changedStartTime = SettingsPref.getStartNotifiTime(this.applicationContext);
		space = changedStartTime.indexOf(" ");
		startTime = Integer.valueOf(changedStartTime.substring(0, space));
		if(startTime != 30){
			startTime = startTime * 60;
		}
		return startTime;
	}
	public int getDelayNotifiTimeIntger(){
		int delayTime = 5;
		int space = 0;
		String changedDelayTime = SettingsPref.getDelayNotifiTime(this.applicationContext);
		space = changedDelayTime.indexOf(" ");
		delayTime = Integer.valueOf(changedDelayTime.substring(0, space));
		return delayTime;
	}
	public void setDelayNotifiTime(String time){
		SettingsPref.saveDelayNotifiTime(this.applicationContext, time);
	}
	public String getDelayNotifiTime(){
		return SettingsPref.getDelayNotifiTime(this.applicationContext);
	}

	public int selectedNotifiTime(String time){
		if(time != null && !time.isEmpty()){
			String[] timeArray = this.applicationContext.getResources().getStringArray(R.array.settings_time);
			for (int i = 0; i < timeArray.length; i++) {
				if (time.equalsIgnoreCase(timeArray[i])){
					return i;
				}
			}
		}
		return 0;
	}

	public List<CollectionItem> readLanguages(){
		InputStream is = this.applicationContext.getResources().openRawResource(R.raw.languages);
		String languagesResponse = FileManager.getInstance().readFileWithInputStream(is);
		CollectionResponse collectionResponse = null;
		MasterResponseConverter masterResponseConverter = new MasterResponseConverter();
		try {
			collectionResponse = masterResponseConverter.parseCollectionItem(languagesResponse);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InvalidJsonError invalidJsonError) {
			invalidJsonError.printStackTrace();
		}
		if(collectionResponse != null){
			return collectionResponse.getCollectionItems();
		}
		return null;
	}

	public void setCurrentLanguagesKey(String languagesKey){
		SettingsPref.saveCurrentLanguagesKey(this.applicationContext, languagesKey);
	}

	public String getCurrentLanguagesKey() {

		String languagesKey = SettingsPref.getCurrentLanguagesKey(this.applicationContext);
		return languagesKey;
	}

	@Override
	public String getCurrentLanguage(String language) {
		return null;
	}

	public String getCurrentLanguage(String languagesKey, List<CollectionItem> languages) {
		for (CollectionItem language : languages) {
			if (language != null){
				if(language.getKey().toUpperCase().contains(languagesKey.toUpperCase())){
					return language.getLable();
				}
			}
		}
		return "";
	}
	/**
	 * Used for setting the language for current application.
	 * @param /context
	 * @param /mLocale
	 */
	public void initLanguageSettings(){

		String appCurrentLanguage = SettingsPref.getCurrentLanguagesKey(this.applicationContext);

		if ("".equals(appCurrentLanguage)) {

			String defaultLanguage = Locale.getDefault().getLanguage();
			if(defaultLanguage != null && defaultLanguage.length() > 0){
				defaultLanguage = defaultLanguage.substring(0,2);
				if(StringUtils.equalsIgnoreCase("NL", defaultLanguage)){
					appCurrentLanguage = "NL_BE";
				}else if(StringUtils.equalsIgnoreCase("FR", defaultLanguage)){
					appCurrentLanguage = "FR_BE";
				}else if(StringUtils.equalsIgnoreCase("DE", defaultLanguage)){
					appCurrentLanguage = "DE_BE";
				}else{
					appCurrentLanguage = "EN_GB";
				}
			}
			SettingsPref.saveCurrentLanguagesKey(this.applicationContext, appCurrentLanguage);
			updateConfigurationLocale(appCurrentLanguage);


		}else {
			updateConfigurationLocale(appCurrentLanguage);
		}
	}

	/**
	 * Update configuration language, Store the newly updated configuration.
	 * @param /mLocale
	 * @param /localeLanguage
	 */
	public void updateConfigurationLocale(String MLocale){
		Log.e("LocaleChangedUtils", "updateConfigurationLanguage. Locale is " + MLocale);
		/*Locale locale = null;
		if(!"".equals(MLocale) && MLocale != null){
			if(MLocale.contains("_")){
				String languageCode =  MLocale.substring(0, MLocale.indexOf("_"));
				String countryCode = MLocale.substring(MLocale.indexOf("_") + 1) ;
				locale = new Locale(languageCode, countryCode);
				Locale.setDefault(locale);
			}
		}else{
			Locale.setDefault(Locale.UK);
		}
		Configuration config = new Configuration();
		config.locale = locale;
		//context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
//		Log.e("LocaleChangedUtils", "updateConfigurationLanguage. Locale is " + config.locale);
		this.applicationContext.getResources().updateConfiguration(config, null);*/


		Resources resources = this.applicationContext.getResources();
		Configuration configuration = resources.getConfiguration();
		Locale locale = AppLanguageUtils.getLocaleByLanguage(MLocale);
		Log.e("locale", "locale---->" + locale.getLanguage());
		// app locale
/*		Locale locale = null;

		if(!"".equals(MLocale) && MLocale != null){
			if(MLocale.contains("_")){
				String languageCode =  MLocale.substring(0, MLocale.indexOf("_"));
				String countryCode = MLocale.substring(MLocale.indexOf("_") + 1) ;
				locale = new Locale(languageCode, countryCode);
				//Locale.setDefault(locale);
			}
		}else{
			locale = Locale.US;
		}*/

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			configuration.setLocale(locale);
		} else {
			configuration.locale = locale;
		}

		// updateConfiguration
		DisplayMetrics dm = resources.getDisplayMetrics();
		resources.updateConfiguration(configuration, dm);
	}

	public static Context attachBaseContext(Context context, String language) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return updateResources(context, language);
		} else {
			return context;
		}
	}

	@TargetApi(Build.VERSION_CODES.N)
	private static Context updateResources(Context context, String language) {
		Resources resources = context.getResources();
		Locale locale = AppLanguageUtils.getLocaleByLanguage(language);

		Configuration configuration = resources.getConfiguration();
		configuration.setLocale(locale);
		configuration.setLocales(new LocaleList(locale));
		return context.createConfigurationContext(configuration);
	}

	public void changeLanguageData(ActivityPostExecuteListener listener, IMessageService messageService, String beforeLanguage){
		FileManager.getInstance().deleteAllExternalStoragePrivateFile(applicationContext, FileManager.FOLDER_HOMEBANNER);
		FileManager.getInstance().deleteAllExternalStoragePrivateFile(applicationContext, FileManager.FOLDER_FILE);
		MessageDataService messageDataService = new MessageDataService();
		messageDataService.deleteMessages(applicationContext);
		NMBSApplication.getInstance().getStationService().cleanAllStation();
		RestMasterDataHandler handler = new RestMasterDataHandler();
		handler.setPostExecuteListener(listener);
		MasterDataAsyncTask asyncTask = new MasterDataAsyncTask(beforeLanguage, this.applicationContext, handler, messageService);
		asyncTask.execute();
	}
	public void cleanLastModifiedTime(){
		new MasterDataService().cleanLastModifiedTime(applicationContext);
		new MessageDataService().cleanLastModifiedTime(applicationContext);
		new StationInfoDataService().cleanLastModifiedTime(applicationContext);
	}
}
