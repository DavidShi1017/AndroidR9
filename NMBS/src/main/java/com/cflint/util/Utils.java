package com.cflint.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.cflint.R;
import android.app.ActivityManager;

import com.cflint.activities.MessageWizardActivity;
import com.cflint.activities.WebViewActivity;
import com.cflint.activities.WebViewOverlayActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;
import com.cflint.model.GeneralSetting;
import com.cflint.preferences.SettingsPref;
import com.cflint.services.IClickToCallService;
import com.cflint.services.impl.LoginService;
import com.cflint.services.impl.TestService;

import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * offer tools method for the project.
 */
public class Utils {

	private final static String TAG = Utils.class.getSimpleName();
	/**
	 * Check the pattern of the email address.
	 */
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	
	/**
	 * Check the pattern of the password.
	 */
	public final static Pattern PASSWORD_PATTERN = Pattern
	.compile( "((((?=.*\\d)(?=.*[a-z]))|((?=.*[a-z])(?=.*[A-Z]))|((?=.*\\d)(?=.*[A-Z]))).{6,20})");

	/**
	 * parse a string to int
	 * 
	 * @param str
	 * @return
	 */
	public static int parseStringToInt(String str) {
		if (str == null) {
			return 0;
		}
		if (!"".equals(str)) {
			return Integer.parseInt(str);
		}
		return 0;
	}

	/**
	 * Get a type of long, the return value is used NEW a Date.
	 * 
	 * @param dateStr
	 * @return
	 */
	public static long getDateByLong(String dateStr) {
		if (dateStr.length() != 0) {
			int start = dateStr.indexOf("(") + 1;
			int end = dateStr.indexOf("+");
			return Long.parseLong(dateStr.substring(start, end));
		}
		return 0;
	}
	


//	/**
//	 * Format HH:MM:SS to HH:MM
//	 * 
//	 * @param timeStr
//	 * @return
//	 */
//	public static String FormatHHMMByHHMMSS(String timeStr) {
//		if (timeStr.length() != 0) {
//			int end = timeStr.lastIndexOf(":");
//			return timeStr.substring(0, end);
//		}
//		return timeStr;
//	}

	/**
	 * Keep two digits behind the decimal point
	 * 
	 * @param doubleNum
	 * @return string
	 */
	public static String doubleFormat(double doubleNum) {
		Locale.setDefault(Resources.getSystem().getConfiguration().locale);
		DecimalFormat df = new DecimalFormat("#,###.00"); // #.00
		if(doubleNum == 0){
			return "0.00";
		}
		return df.format(doubleNum);
	}

	/**
	 * Clear the List.
	 * @param list
	 */
	public static void clearList(List<?> list) {
		if (list != null) {
			list.clear();
		}
	}

	/**
	 * Make a random, the length is 8.
	 * 
	 * @return
	 */
	public static int makeRandom() {

		String allChar = "0123456789";

		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		int randomNumber = Integer.valueOf(sb.toString());
		return randomNumber;
	}

	/**
	 * Check email pattern
	 * 
	 * @param email
	 * @return false means it is not OK.
	 */
	public static boolean checkEmailPattern(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
	/**
	 * Check password pattern
	 * 
	 * @param
	 * @return
	 */
	public static boolean checkPasswordPattern(String pass) {
		final Pattern PASSWORD_PATTERN_AGAIN = Pattern.compile("^[a-zA-Z0-9]{6,20}$");
		if (PASSWORD_PATTERN.matcher(pass).matches()) {
			return PASSWORD_PATTERN_AGAIN.matcher(pass).matches();
		}
		return false;
	}
	  /**
     * Surrounds the html with the html head and body tags and sets the padding style on the body tag to 0
     * 
     * This is a helper method to create html to be used in a WebView with no padding
     * @param htmlText
     * @return
     */
    public static  String buildZeroPaddingValidHTML(String htmlText){
		return "<html><head></	head><body style=\"margin: 0; padding: 0\">" + htmlText + "</body></html>";
	}
    
	public static String madeSHAData(String message) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (java.security.NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		md.update(message.getBytes());
		// return new BigInteger(md.digest()).toString();
		byte byteData[] = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		
		return hexString.toString();
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			if(listItem != null){
				listItem.measure(0, 0);
				System.out.println("listItem.getMeasuredHeight() : " + listItem.getMeasuredHeight());
				totalHeight += listItem.getMeasuredHeight();
			}
			
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		System.out.println("params.height : " + params.height);
		((ViewGroup.MarginLayoutParams)params).setMargins(10, 10, 10, 10);
		listView.setLayoutParams(params);
	}
	


	public static String getTrainNameByTrainType(Context context, String trainType) {
			
		if (TrainTypeConstant.TRAIN_TGD.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_TGV.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_RHT.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_TGV_FR_DE.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_TGV_DE_FR.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_LYRIA.equalsIgnoreCase(trainType)) {
			
			return context.getString(R.string.traincategory_tgv);
			
			
		} else if (TrainTypeConstant.TRAIN_IC.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_BENELUX.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_INTERCITY.equalsIgnoreCase(trainType)) {
			
			return context.getString(R.string.traincategory_ic);
			
		} else if (TrainTypeConstant.TRAIN_THA.equalsIgnoreCase(trainType)) {
			
			return context.getString(R.string.traincategory_tha);
			
		} else if (TrainTypeConstant.TRAIN_ICE.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_IXB.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_IXK.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_RHI.equalsIgnoreCase(trainType)) {
			
			return context.getString(R.string.traincategory_ice);
			
		} else if(StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EST)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUROSTAR)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUR)){
			
			return context.getString(R.string.traincategory_est);
		
		} else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_IR)) {
			
			return context.getString(R.string.traincategory_ir);
			
		}else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_RE)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_R)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_RB)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_REX)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_R84)) {
			
			return context.getString(R.string.traincategory_re);
		}else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EC)) {
			
			return context.getString(R.string.traincategory_ec);
		}else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_INT)) {
			
			return context.getString(R.string.traincategory_int);
		}else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_CNL)) {
			
			return context.getString(R.string.traincategory_cnl);
		}
		return "";
	}
	
	public static int getAndroidSDKVersion() { 
		   int version = 0; 
		   version = android.os.Build.VERSION.SDK_INT; 
		   return version; 
	}
	public static int getDeviceDensity(Activity activity){
		DisplayMetrics dm = new DisplayMetrics(); 
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}

	
	public static long getDistanceHours(String str1, String str2){
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date one;
	        Date two;
	        long hours=0;
	        try {
	            one = df.parse(str1);
	            two = df.parse(str2);
	            long time1 = one.getTime();
	            long time2 = two.getTime();
	            long diff ;
	            if(time1<time2) {
	                diff = time2 - time1;
	            } else {
	                diff = time1 - time2;
	            }
	            hours = diff / (1000 * 60 * 60);
	            //hours = diff / (1000 * 60);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return hours;
	    }

	public static long getDistanceMin(String str1, String str2){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long min = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			min = diff / (1000 * 60);
			//hours = diff / (1000 * 60);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return min;
	}
	

	
    public static byte[] getBytes(InputStream is)  {  
	       ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
	      byte[] buffer = new byte[1024];
	      int len = -1;  
	      try {
			while ((len = is.read(buffer)) != -1) {  
			       outstream.write(buffer, 0, len);  
			   }  
			    outstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	         return outstream.toByteArray();  
	   } 
	
    public static String getAppVersion(Context context){
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo("com.cflint", 0).versionName;
		} catch (NameNotFoundException e) {
			versionName="3.0";
			e.printStackTrace();
		}
		return versionName;
    }
    
    public static boolean isBigDecimal(String value) {
    	try {
    		double n = Double.parseDouble(value);
    		int temp;
    		double i;
    		temp = (int) n;
    		i = n - temp;
    		if (i == 0) {
    			return false;
    		} else {
    			return true;
    		}
		} catch (Exception e) {
			return true;
		}
         
	}

	public static String arrayToString(String [] strs){
		String str = "";
		for (int i = 0; i < strs.length; i ++){
			if (i == strs.length - 1) {
				str += strs[i];
			} else {
				str += strs[i] + ", ";
			}
		}
		return str;
	}

	public static String[] stringToArray(String str){

		int count = 0;
		int total = 0;
		for(int i = 0; i < str.toCharArray().length; i ++){
			if(String.valueOf(str.toCharArray()[i]).equals(",")){
				total ++;
			}
		}
		String s = "";
		String [] array = new String[total + 1];
		for(int i = 0; i < str.toCharArray().length; i ++){
			if(!String.valueOf(str.toCharArray()[i]).equals(",")){
				s += str.toCharArray()[i];
				array[count] = s;
			}else {
				count ++;
				s = "";
			}
		}
		return array;
	}

	public static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		if(input == null){
			input = "";
		}
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		if(sb != null){
			return sb.toString();
		}else{
			return null;
		}
	}

	public static String getUrl(String url){
		String countryCode = "";
		String phoneNumber = "";

		boolean isChecked = NMBSApplication.getInstance().getSettingService().is3rdTrack();
		if(NMBSApplication.getInstance().getLoginService().getLogonInfo() != null){
			countryCode = "&phoneNumberCountryCode=" + NMBSApplication.getInstance().getLoginService().getLogonInfo().getCode();
			phoneNumber = "&phoneNumberSubscriberNumber=" + NMBSApplication.getInstance().getLoginService().getLogonInfo().getPhoneNumber();
		}
		if(url != null){
			if(url.contains("?")){
				url += "&blockSmartAppBanner=true" + countryCode + phoneNumber + "&app=Android&webview=true&thirdPartyCookies=" + String.valueOf(isChecked);
			}else{
				url += "?blockSmartAppBanner=true" + countryCode + phoneNumber + "&app=Android&webview=true&thirdPartyCookies=" + String.valueOf(isChecked);
			}
		}
		GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
		try {
			URL uri = new URL(url);
			String domain = uri.getHost();
            LogUtils.e(TAG, "domain------>" + domain);
            LogUtils.e(TAG, "getDomain------>" + generalSetting.getDomain());
			if(generalSetting != null && generalSetting.getDomain() != null){
				if(domain.contains(generalSetting.getDomain())){
					url = getUrlForLogin(url, generalSetting);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		LogUtils.d(TAG, "url----------->" + url);
		return url;
	}

	public static String getUrlForLogin(String urlStr, GeneralSetting generalSetting){
		String autoLogin = "true";
		String custId = "";
		String autoLoginControl = "";
		LoginService loginService = NMBSApplication.getInstance().getLoginService();

		if(loginService.isLogon()){
			if(loginService != null && loginService.getLogonInfo() != null){
				custId = loginService.getLogonInfo().getCustomerId();
			}

			String str = generalSetting.getAutoLogonSalt();
			autoLoginControl = custId + DecryptUtils.decryptData(str);
			try {
				autoLoginControl = Utils.sha1(autoLoginControl);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			if(urlStr != null){
				if(urlStr.contains("?")){
					urlStr += "&loginProvider" + loginService.getLogonInfo().getLoginProvider() + "&AutoLoginFlow=" + autoLogin + "&AutoLoginCustId=" + custId + "&AutoLoginHash=" + autoLoginControl+ "&AutoLoginKeepMe=true";
				}else{
					urlStr += "&loginProvider" + loginService.getLogonInfo().getLoginProvider() + "?AutoLoginFlow=" + autoLogin + "&AutoLoginCustId=" + custId + "&AutoLoginHash=" + autoLoginControl+ "&AutoLoginKeepMe=true";
				}
			}
		}else{
			if(urlStr.contains("?")){
				urlStr += "&appLoggedOut=true";
			}else{
				urlStr += "?appLoggedOut=true";
			}
		}

		//LogUtils.d(TAG, "url===" + urlStr);
		return urlStr;
	}

	public static void openProwser(Context context, String url, IClickToCallService clickToCallService){
		String countryCode = "";
		String phonNumber = "";
		boolean isChecked = NMBSApplication.getInstance().getSettingService().is3rdTrack();
		if(NMBSApplication.getInstance().getLoginService().getLogonInfo() != null){
			countryCode = "&phoneNumberCountryCode=" + NMBSApplication.getInstance().getLoginService().getLogonInfo().getCode();
			phonNumber = "&phoneNumberSubscriberNumber=" + NMBSApplication.getInstance().getLoginService().getLogonInfo().getPhoneNumber();
		}
		if(url != null){
			if(url.contains("?")){
				url += "&blockSmartAppBanner=true" + countryCode + phonNumber + "&app=Android&webview=true&&thirdPartyCookies=" + String.valueOf(isChecked);
			}else{
				url += "?blockSmartAppBanner=true" + countryCode + phonNumber + "&app=Android&webview=true&&thirdPartyCookies=" + String.valueOf(isChecked);
			}

			LogUtils.d(TAG, "url===" + url);
			Uri uri = Uri.parse(url);
			Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}

	}

	public static void openWebView(Activity activity, String url, int flow, String dnr, boolean isWebView){
		if(NetworkUtils.isOnline(activity)) {
			if(dnr != null && !dnr.isEmpty()){
				activity.startActivity(WebViewActivity.createIntent(activity, Utils.getUrl(url), flow, dnr));
			}else{
				if (isWebView) {
					activity.startActivity(WebViewActivity.createIntent(activity, Utils.getUrl(url), flow, dnr));
				} else {
					activity.startActivity(WebViewOverlayActivity.createIntent(activity, Utils.getUrl( url)));
				}
			}
		}
	}

	public static boolean isAppAlive(Context context, String packageName){
		ActivityManager activityManager =
				(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfos
				= activityManager.getRunningAppProcesses();
		for(int i = 0; i < processInfos.size(); i++){
			if(processInfos.get(i).processName.equals(packageName)){
				LogUtils.i("NotificationLaunch",
						String.format("the %s is running, isAppAlive return true", packageName));
				return true;
			}
		}
		LogUtils.i("NotificationLaunch",
				String.format("the %s is not running, isAppAlive return false", packageName));
		return false;
	}

	public static void sendEmail(Context context){
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/*UAT --> dieter.hautekeete@b-rail.be
		Production --> internet-int@b-rail.be*/
		if(TestService.isTestMode){
			data.setData(Uri.parse("mailto:anthony.cauwelier@b-rail.be"));
		}else{
			data.setData(Uri.parse("mailto:hometicketing@cfl.lu"));
		}

		data.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_email_subject));
		String appVersion = SettingsPref.getSettingsVersion(context) + "\n";
		String osType = "Android" + "\n";
		String osVersion = android.os.Build.VERSION.RELEASE + "\n";
		String deviceType = android.os.Build.BRAND + " " + android.os.Build.MODEL;

		String body = appVersion + osType + osVersion + deviceType;
		data.putExtra(Intent.EXTRA_TEXT, body);
		context.startActivity(data);
	}

	public static void sendEmail(Context context, String emailStr){
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/*UAT --> dieter.hautekeete@b-rail.be
		Production --> internet-int@b-rail.be*/
		if(TestService.isTestMode){
			data.setData(Uri.parse("mailto:david.shi@delaware.pro"));
		}else{
			data.setData(Uri.parse("mailto:hometicketing@cfl.lu"));
		}

		data.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_email_subject));
		String appVersion = SettingsPref.getSettingsVersion(context) + "\n";
		String osType = "Android" + "\n";
		String osVersion = android.os.Build.VERSION.RELEASE + "\n";
		String deviceType = android.os.Build.BRAND + " " + android.os.Build.MODEL;

		String body = appVersion + osType + osVersion + deviceType;
		data.putExtra(Intent.EXTRA_TEXT, emailStr);
		context.startActivity(data);
	}

	/**
	 * 保存全局异常信息
	 * @param exceptionString 全局异常信息
	 */
	public static void saveGlobalExceptionInfo(String exceptionString) {
		File docDir = null;
		LogUtils.e("filepath", "filepath...saveGlobalExceptionInfo");
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(sdcardExist) {
			docDir = NMBSApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ;
		} else {
			docDir = NMBSApplication.getContext().getDir(Environment.DIRECTORY_DOCUMENTS, Context.MODE_PRIVATE) ;
		}

		String filepath = null ;
		FileOutputStream fileOutStream = null;
		try {
			filepath = docDir.getAbsolutePath() + "/error.log";
			LogUtils.e("filepath", "filepath..." + filepath);
			File file = new File(filepath);
			if(!docDir.exists()) {
				docDir.mkdirs();
			}
			if (!file.exists()) { // 创建不存在的文件
				file.createNewFile();
			}

			fileOutStream = new FileOutputStream(file);
			if (null != fileOutStream) {
				fileOutStream.write(exceptionString.getBytes());
				fileOutStream.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			filepath = null ;
		} finally {
			try {
				fileOutStream.close() ;
				fileOutStream = null ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isTablet(Context context) {
		return context.getResources().getBoolean(R.bool.isTablet);
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}


	public static void setToolBarStyle(Activity activity){
		Window window = activity.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
		View statusBarView = new View(window.getContext());
		int statusBarHeight = getStatusBarHeight(window.getContext());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
		params.gravity = Gravity.TOP;
		statusBarView.setLayoutParams(params);
		statusBarView.setBackgroundColor(activity.getResources().getColor(R.color.background_activity_title));
		decorViewGroup.addView(statusBarView);
	}

	private static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = res.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	public static String getUrlValue(String url, String key) {
		String value = "";
		try {
			Uri uri = Uri.parse(url);
			value = uri.getQueryParameter(key);
		}catch (Exception e){
			return value;
		}

		return value;
	}

	public static String getUrlScheme(String url) {

		Uri uri = Uri.parse(url);
		String host = uri.getScheme();
		return host;
	}

	public static String getPhoneCode(String mobilePhone){
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		Phonenumber.PhoneNumber numberProto = null;
		try {
			numberProto = phoneUtil.parse(mobilePhone, "");
		} catch (NumberParseException e) {
			e.printStackTrace();
		}
		int countryCode = numberProto.getCountryCode();
		return String.valueOf(countryCode);
	}

	public static String getPhoneNumber(String mobilePhone){
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		Phonenumber.PhoneNumber numberProto = null;
		try {
			numberProto = phoneUtil.parse(mobilePhone, "");
		} catch (NumberParseException e) {
			e.printStackTrace();
		}
		long phoneNumber = numberProto.getNationalNumber();
		return String.valueOf(phoneNumber);
	}


}
