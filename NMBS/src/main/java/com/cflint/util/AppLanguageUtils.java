package com.cflint.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;
import com.cflint.preferences.SettingsPref;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;


/**
 * @author YanLu
 * @since 17/5/12
 */

public class AppLanguageUtils {

    private static HashMap<String, Locale> mAllLanguages = new HashMap<String, Locale>(7) {{
        put(ConstantLanguages.ENGLISH, Locale.ENGLISH);
        put(ConstantLanguages.FRANCE, Locale.FRANCE);
        put(ConstantLanguages.GERMAN, Locale.GERMANY);
        //put(ConstantLanguages.NL, new Locale(ConstantLanguages.NL, "nl"));

    }};

    @SuppressWarnings("deprecation")
    public static void changeAppLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        LogUtils.d("changeAppLanguage", "The currentLanguage------->" + newLanguage);
        // app locale
        if(newLanguage != null && newLanguage.length() > 0) {
            if(newLanguage.contains("_")){
                newLanguage = newLanguage.substring(0, 2).toLowerCase();
            }

        }else{
            String defaultLanguage = Locale.getDefault().getLanguage();
            if(StringUtils.equalsIgnoreCase("NL", defaultLanguage)){
                newLanguage = "EN_GB";
            }else if(StringUtils.equalsIgnoreCase("FR", defaultLanguage)){
                newLanguage = "FR_BE";
            }else if(StringUtils.equalsIgnoreCase("DE", defaultLanguage)){
                newLanguage = "DE_BE";
            }else{
                newLanguage = "EN_GB";
            }
           /* SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString(context.getString(R.string.app_language_pref_key), newLanguage);
            editor.commit();*/
            SettingsPref.saveCurrentLanguagesKey(context, newLanguage);
            if(newLanguage != null && newLanguage.length() > 0) {
                if(newLanguage.contains("_")){
                    newLanguage = newLanguage.substring(0, 2).toLowerCase();
                }
            }
        }
        //Log.d("changeAppLanguage", "locale------->" + newLanguage);
        Locale locale = getLocaleByLanguage(newLanguage);
        LogUtils.d("changeAppLanguage", "locale------->" + locale.getLanguage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }


    private static boolean isSupportLanguage(String language) {
        return mAllLanguages.containsKey(language);
    }

    public static String getSupportLanguage(String language) {
        if (isSupportLanguage(language)) {
            return language;
        }

        return ConstantLanguages.ENGLISH;
    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在{@link #mAllLanguages}，返回本机语言，如果本机语言不是语言集合中的一种{@link #mAllLanguages}，返回英语
     *
     * @param language language
     * @return
     */
    public static Locale getLocaleByLanguage(String language) {
        if (isSupportLanguage(language)) {
            return mAllLanguages.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : mAllLanguages.keySet()) {
                if (TextUtils.equals(mAllLanguages.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.ENGLISH;
    }


    public static Context attachBaseContext(Context context, String language) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           /* language = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(context.getString(R.string.app_language_pref_key), "");*/
            language = SettingsPref.getCurrentLanguagesKey(context);
            //Log.d("attachBaseContext", "The language------->" + language);
            if(language != null && language.length() > 0) {
                if(language.contains("_")){
                    language = language.substring(0, 2).toLowerCase();
                }
            }
            return updateResources(context, language);
        } else {
            if(NMBSApplication.getInstance().getSettingService() != null){
                changeAppLanguage(context,  NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            }

            return context;
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = AppLanguageUtils.getLocaleByLanguage(language);
        //Log.d("Locale", "The language------->" + locale.getLanguage());
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));

        /*SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.app_language_pref_key), locale.getLanguage());
        editor.commit();*/
        SettingsPref.saveCurrentLanguagesKey(context, locale.getLanguage());
        return context.createConfigurationContext(configuration);
    }
}
