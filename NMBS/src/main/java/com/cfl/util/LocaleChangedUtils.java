package com.cfl.util;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.cfl.activity.SettingsActivity;
import com.cfl.preferences.SettingsPref;

import android.content.Context;
import android.content.res.Configuration;


/**
 * Now it is used for language changed. After user change the application language
 * , it will be changed for application automatically.
 * @author Tony
 *
 */
public class LocaleChangedUtils {
	
	public static final String LANGUAGE_NL = "NL_BE";
	public static final String LANGUAGE_FR = "FR_BE";
	public static final String LANGUAGE_EN = "EN_GB";
	public static final String LANGUAGE_DE = "DE_BE";
	/**
	 * Used for setting the language for current application.
	 * @param context
	 * @param mLocale
	 */
	public static void initLanguageSettings(Context context){

		String sharedLocale = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_LANGUAGE,context);
		//Log.i("LocaleChangedUtils", "initLanguageSettings.It's NULL, Locale is " + sharedLocale);
		// Open the APP, 
		// if the PREFS_LOCALE is "", set the PREFS_LOCALE = Device's language, then set language for APP
		if ("".equals(sharedLocale)) {
			//store language and country(e.x. EN_GB)
			// first store EN_US
			//sharedLocale = "EN_US";
			//sharedLocale = Locale.getDefault().getLanguage()+"_"+Locale.getDefault().getCountry();
			String defaultLanguage = Locale.getDefault().getLanguage();
			if(defaultLanguage != null && defaultLanguage.length() > 0){
				defaultLanguage = defaultLanguage.substring(0,2);
				if(StringUtils.equalsIgnoreCase("NL", defaultLanguage)){
					sharedLocale = "NL_BE";
				}else if(StringUtils.equalsIgnoreCase("FR", defaultLanguage)){
					sharedLocale = "FR_BE";
				}else if(StringUtils.equalsIgnoreCase("DE", defaultLanguage)){
					sharedLocale = "DE_BE";
				}else{
					sharedLocale = "EN_GB";
				}
			}			
			SharedPreferencesUtils.storeSharedPreferences(SettingsActivity.PREFS_LANGUAGE, sharedLocale,context);
			//Log.i("LocaleChangedUtils", "initLanguageSettings.It's NULL, Locale is " + sharedLocale);
			updateConfigurationLocale(context, sharedLocale);
			
			
		}else {//immediately, get the PREFS_LOCALE , set it for APP's language
			//Log.i("LocaleChangedUtils", "initLanguageSettings.Not NULL, Locale is " + sharedLocale );
			updateConfigurationLocale(context, sharedLocale);
		}
	}
	
	/**
	 * Update configuration language, Store the newly updated configuration.
	 * @param context
	 * @param mLocale
	 * @param localeLanguage
	 */
	public static void updateConfigurationLocale(Context context, String MLocale){
		//Log.i("LocaleChangedUtils", "updateConfigurationLanguage. Locale is " + MLocale);
		Locale locale = null;
		if(!"".equals(MLocale) && MLocale != null){
			if(MLocale.contains("_")){
				String languageCode =  MLocale.substring(0, MLocale.indexOf("_"));
				String countryCode = MLocale.substring(MLocale.indexOf("_") + 1) ;
				locale = new Locale(languageCode, countryCode); 
				Locale.setDefault(locale);
				//Log.i("LocaleChangedUtils", "languageCode is " + languageCode);
				//Log.i("LocaleChangedUtils", "countryCode is " + countryCode);
			}
		}else{
			Locale.setDefault(Locale.UK);
		}		
		Configuration config = new Configuration();
		config.locale = locale;
		//context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
		config.setLocale(locale);
		context.createConfigurationContext(config);
		context.getResources().updateConfiguration(config, null);
	}
	
}
