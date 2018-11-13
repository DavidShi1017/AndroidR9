package com.cflint.services.impl;

import android.content.Context;
import android.util.Log;

import com.cflint.R;

import com.cflint.application.NMBSApplication;
import com.cflint.dataaccess.converters.CheckAppUpdateConverter;
import com.cflint.exceptions.BookingTimeOutError;
import com.cflint.exceptions.ConnectionError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RequestFail;
import com.cflint.exceptions.TimeOutError;
import com.cflint.model.CheckAppUpdate;
import com.cflint.preferences.SettingsPref;
import com.cflint.services.ICheckUpdateService;
import com.cflint.util.HTTPRestServiceCaller;
import com.cflint.util.SharedPreferencesUtils;
import com.cflint.util.Utils;

import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckUpdateService implements ICheckUpdateService {
	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	CheckAppUpdateConverter checkAppUpdateConverter = new CheckAppUpdateConverter();
	public final static String CHECKAPPLASTTIME = "checkAppLastTime";
	private Context applicationContext;
	private CheckAppUpdate checkAppUpdate;
	private boolean isCheckAppManually;
	private boolean isReady = false;
	public static final String PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG = "checkForUpdateNotNowFlag";
	public static final String PREFS_CHECK_FOR_UPDATE_NOT_NOW_TIME = "checkForUpdateNotNowTime";
	public static final String PREFS_CHECK_FOR_UPDATE_NOT_NOW_VERSION = "checkForUpdateNotNowVersion";
	public CheckUpdateService(Context context){
		this.applicationContext = context;
	}	
	
	public void checkAppVersion(String language)
			throws InvalidJsonError, JSONException, TimeOutError,
			ParseException, RequestFail, IOException, ConnectionError,
			BookingTimeOutError {
		String stringHttpResponse = null;
		String versionName = "7.0";
		String deviceType = "Android";
/*		try {
			versionName = applicationContext.getPackageManager().getPackageInfo("com.nmbs", 0).versionName;
		} catch (NameNotFoundException e) {
			versionName="5.0";
			e.printStackTrace();
		}*/
		//Log.e("isCheckAppVersion", "checkAppVersion.....");
		String urlString = applicationContext.getString(R.string.server_url_check_app_version) + "?version="+versionName+"&platform="+deviceType;
		//Log.e("isCheckAppVersion", "urlString....." + urlString);
		try {
			saveLastCheckUpdateTime(applicationContext);
			stringHttpResponse = httpRestServiceCaller
					.executeHTTPRequest(applicationContext,"",urlString,language,HTTPRestServiceCaller.HTTP_GET_METHOD, 10000, false,"",HTTPRestServiceCaller.API_VERSION_VALUE_6);
			//InputStream is = applicationContext.getResources().openRawResource(R.raw.test);  
			//stringHttpResponse  = FileManager.getInstance().readFileWithInputStream(is);
		} catch (Exception e) {
			//Log.e("isCheckAppVersion", "error....." + e.getMessage());
			e.printStackTrace();
			throw new ConnectionError();
		}
		//Log.e("isCheckAppVersion", "checkAppVersion..stringHttpResponse..." + stringHttpResponse);
		this.checkAppUpdate = checkAppUpdateConverter.parse(stringHttpResponse);
	}
	
	public void setIsReady(boolean isReady){
		this.isReady = isReady;
	}
	
	public boolean getIsReady(){
		return this.isReady;
	}
	
	public void setCheckAppManually(boolean isManually){
		this.isCheckAppManually = isManually;
	}
	
	public CheckAppUpdate getCheckAppUpdate(){
		return this.checkAppUpdate;
	}
	
	public void setCheckAppUpdate(CheckAppUpdate checkAppUpdate){
		this.checkAppUpdate = checkAppUpdate;
	}
	
	public void saveLastCheckUpdateTime(Context context){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SharedPreferencesUtils.storeAppInfoSharedPreferences(CHECKAPPLASTTIME, df.format(new Date()), context);
	}
	
	public void deleteLastCheckUpdateTime(Context context){
		SharedPreferencesUtils.deleteAppInfoSharePreferences(CHECKAPPLASTTIME, context);
	}

	@Override
	public void saveCheckStutes(Context context) {
		SharedPreferencesUtils.storeSharedPreferences(PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG, true, context);
		SharedPreferencesUtils.storeAppInfoSharedPreferences(PREFS_CHECK_FOR_UPDATE_NOT_NOW_TIME,
						new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss")
								.format(new Date()),
						context);
		SharedPreferencesUtils.storeAppInfoSharedPreferences(PREFS_CHECK_FOR_UPDATE_NOT_NOW_VERSION,
				checkAppUpdate.getVersion(),
				context);

		SharedPreferencesUtils.storeSharedPreferences(PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG,
						true, context);

	}

	public String getLastCheckUpdateTime(){
		return SharedPreferencesUtils.getUnencryptSharedPreferencesByKey(CHECKAPPLASTTIME, applicationContext);
	}
	
	public boolean isExecuteCheckAppUpdate() {
		boolean flag = false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(this.isCheckAppManually){
			return true;
		}
		if(SettingsPref.isAutoUpdate(applicationContext)){
			flag = true;
		}else{
			flag = false;
			return flag;
		}
		
		if (!getLastCheckUpdateTime().equals("")) {
			if(NMBSApplication.getInstance().getTestService().isCheckAppVersion()){
				long min = Utils.getDistanceMin(getLastCheckUpdateTime(), df.format(new Date()));
				//Log.e("isCheckAppVersion", "isExecuteCheckAppUpdate::::min::::" + min);
				if(min > 5){
					return true;
				}else {
					return false;
				}
			}
			long hour = Utils.getDistanceHours(getLastCheckUpdateTime(), df.format(new Date()));
			if (hour < 24||hour == 24) {
				flag = false;
				return flag;
			} else {
				flag = true;
			}
		} else {
			flag = true;
		}
		return flag;
	}
	
	public boolean isShowCheckAppUpdateInfo(){
		boolean flag = false;
		//Log.e("isCheckAppVersion", "this.checkAppUpdate::::" + checkAppUpdate);
		if(this.checkAppUpdate != null){
			Log.e("isCheckAppVersion", "checkAppUpdate.isUpToDate()::::" + checkAppUpdate.isUpToDate());
			if(this.checkAppUpdate.isUpToDate()){
				return false;
			}
/*			Log.e("isCheckAppVersion", "SharedPreferencesUtils::::" +
					SharedPreferencesUtils.getSharedPreferencesByKey(PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG, false, applicationContext));*/

			if(SharedPreferencesUtils.getSharedPreferencesByKey(PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG, false, applicationContext)){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String previousVersion = SharedPreferencesUtils.getUnencryptSharedPreferencesByKey(PREFS_CHECK_FOR_UPDATE_NOT_NOW_VERSION, applicationContext);
				if(previousVersion.equals("")){
					return true;
				}else{
					if(Float.valueOf(this.checkAppUpdate.getVersion()) > Float.valueOf(previousVersion)){
						return true;
					}
				}
				if(NMBSApplication.getInstance().getTestService().isCheckAppVersion()){
					long min = Utils.getDistanceMin(getLastCheckUpdateTime(), df.format(new Date()));
					//Log.e("isCheckAppVersion", "isCheckAppVersion::::min::::" + min);
					if(min > 5){
						return true;
					}else {
						return false;
					}
				}

				long hour = Utils.getDistanceHours(SharedPreferencesUtils.getUnencryptSharedPreferencesByKey(PREFS_CHECK_FOR_UPDATE_NOT_NOW_TIME, applicationContext),
						df.format(new Date()));
				if(hour < 336 || hour == 336){
					flag = false;
					return flag;
				}else{
					flag = true;
				}
			}else{
				return true;
			}
		}
		return flag;
	}
	

}
