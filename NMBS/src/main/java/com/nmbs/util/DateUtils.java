package com.nmbs.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.nmbs.R;
import com.nmbs.application.NMBSApplication;
import com.nmbs.model.Station;
import com.nmbs.services.impl.SettingService;

import android.content.Context;
import android.util.Log;


/**
 * This class Deal with Date.
 */
public class DateUtils {

	public static final String DATE_FORMAT_MONTH_AD = "dd MMM yyyy - HH:mm";
	public static final String formatDateEn = "dd MMMM yyyy - HH:mm a";
	public static final String formatDateNl = "dd MMMM yyyy - HH:mm";
	/**
	 * Format a date to string that is according with user's date format
	 * 
	 * @param context
	 * @param date
	 * @return String
	 */
	public static String dateToString(Context context, Date date) {
		String dateStr = "";
		/*Context newContext;
		
		try {
			newContext = context.createPackageContext("com.android.launcher",Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			newContext = context;
		}	*/

		if (date != null) {
			java.text.DateFormat dateFormat = android.text.format.DateFormat
					.getDateFormat(context);
			dateStr = dateFormat.format(date);
		}
				
		return dateStr;
	}

	/**
	 * Format a time to string that is according with user's time format
	 * @param context
	 * @param date
	 * @return String
	 */
	public static String timeToString(Context context, Date dateTime) {

		String timeStr = "";
		if (dateTime != null) {
			
			java.text.DateFormat dateFormat = android.text.format.DateFormat
					.getDateFormat(context.getApplicationContext());

			dateFormat = android.text.format.DateFormat.getTimeFormat(context
					.getApplicationContext());
			timeStr = dateFormat.format(dateTime);
		}

		return timeStr;
	}

	/**
	 * Format a string to date
	 * 
	 * @param context
	 * @param dateStr
	 * @return
	 */
	public static Date stringToDate(Context context, String dateStr) {
/*		Context newContext;

		try {
			newContext = context.createPackageContext("com.android.launcher",Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e1) {
			newContext = context;
			
		}*/
		Date date = null;
		if (dateStr != null) {
			try {
				java.text.DateFormat dateFormat = android.text.format.DateFormat
						.getDateFormat(context);
				date = dateFormat.parse(dateStr);

			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return date;
	}

	/**
	 * Format a string to date
	 * 
	 * @param context
	 * @param dateStr
	 * @return
	 */
	public static Date stringToDateTime(String dateStr) {
		Date date = null;
		if (dateStr != null) {
			try {
				dateStr = dateStr.replace('T', ' ');
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				if (!StringUtils.isEmpty(dateStr)) {
					date = simpleDateFormat.parse(dateStr);
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return date;
	}
	
	public static Date stringToDate(String dateStr) {
		Date date = null;
		if (dateStr != null) {
			try {
				dateStr = dateStr.replace('T', ' ');
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd");
				if (!StringUtils.isEmpty(dateStr)) {
					date = simpleDateFormat.parse(dateStr);
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return date;
	}
	/**
	 * get Application local time
	 * 
	 * @param context
	 * @param date
	 * @return
	 */
	public static String getTime(Context context, Date date) {
		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context.getApplicationContext());
		// String dateStr = dateFormat.format(date);
		dateFormat = android.text.format.DateFormat.getTimeFormat(context
				.getApplicationContext());
		String timeStr = dateFormat.format(date);
		return timeStr;
	}

	public static String FormatToHrDate(Date date, Context context) {

		String dateStr = "";
		if (date != null) {
			Log.e("FormatToHrDate", "Hour..." + date.getHours());
			if(date.getHours() > 0){
				SimpleDateFormat sdfDateToStr = new SimpleDateFormat("H:mm");
				dateStr = sdfDateToStr.format(date);
				dateStr = dateStr.replace(":", context.getString(R.string.departuredetail_view_hours));
			}else{
				SimpleDateFormat sdfDateToStr = new SimpleDateFormat("mm");
				dateStr = sdfDateToStr.format(date);
				dateStr = dateStr + context.getString(R.string.departuredetail_view_minutes);
			}
		}
		return dateStr;
	}

	/**
	 * Format date to HH:MM
	 * 
	 * @param date
	 * @return
	 */
	public static String FormatToHHMMFromDate(Date date) {
		/*if (date != null) {
			int hour = 0;
			int minute = 0;
			
			hour = date.getHours();
			minute = date.getMinutes();
			System.out.println("hour==" + hour);
			System.out.println("minute==" + minute);
			String hourStr = null;
			String minuteStr = null;
			if (hour < 10) {
				hourStr = "0" + hour;
			}
			if (hour == 0) {
				hourStr = "0" + hour;
			}
			if (hour >= 10) {
				hourStr = String.valueOf(hour);
			}

			if (minute < 10) {
				minuteStr = "0" + minute;
			}
			if (minute == 0) {
				minuteStr = "0" + minute;
			}
			if (minute >= 10) {
				minuteStr = String.valueOf(minute);
			}
			return hourStr + ":" + minuteStr;
		} else {
			return "";
		}*/
		String dateStr = "";
		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(
					"HH:mm");
			dateStr = sdfDateToStr.format(date);
		}
		return dateStr;
	}

	/**
	 * Get a string from date, (e.x. 19/06)
	 * 
	 * @param date
	 * @return String
	 */
	public static String getMonthAndDay(Date date) {
		String dateStr;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
		dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * String to long time.
	 * 
	 * @param str
	 * @return long
	 * @throws ParseException
	 */
	public static long getTimeFormString(String str) throws ParseException {
		//System.out.println("str=====" + str);
		Date date = null;
		String dateString = null;
		int day = 0;
		int hours = 0;
		if (str.contains(".")) {
			day = Integer.valueOf(str.substring(0, str.indexOf('.')));
			hours = day * 24;
			dateString = str.substring(str.indexOf('.') + 1);
			//System.out.println("hours======" + hours);
			//System.out.println("dateString=====" + dateString);
		}else {
			dateString = str;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		//System.out.println("str=====" + str);
		date = sdf.parse(dateString);
		//System.out.println("hours======" + (date.getHours() + hours));
		long time = (date.getHours() + hours) * 3600 + date.getMinutes() * 60
				+ date.getSeconds();
		return time * 1000;
	}

	/**
	 * long time to String for show.
	 * 
	 * @param time
	 * @return String
	 */
	public static String getStringTime(long time, Context context) {
		
		String str = "";
		time = time / 1000;
		int h = (int) (time / 3600);
		int m = (int) (time % 3600 / 60);
		if (h == 0) {
			if (m != 0) {
				str = "0" + context.getString(R.string.departuredetail_view_hours) + String.valueOf(m);
			}
		} else {
			if (m == 0) {
				str = String.valueOf(h) + context.getString(R.string.departuredetail_view_hours) +"00";
			} else if (m < 10) {
				str = String.valueOf(h) + context.getString(R.string.departuredetail_view_hours) +"0" + String.valueOf(m);
			} else {
				str = String.valueOf(h) +  context.getString(R.string.departuredetail_view_hours) + String.valueOf(m);
			}
		}
		
		return str;
	}

	/**
	 * Increase one day to a date object.
	 * @param date
	 * @return TimeInMillis
	 */
	public static long getTimeInMillisThatAddAmountToDayOfMonth(Date date){
		//startSearch();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
	}
	
	/**
	 * get String date to show
	 * @param date
	 * @return String
	 */
	public static String getDay(Date date) {
		
		String weekString = new SimpleDateFormat("E").format(date)+" ";
		String dateString = Integer.toString(date.getDate())+" ";
		String monthString = new SimpleDateFormat("MMM").format(date)+" ";
		String yearString = new SimpleDateFormat("yy").format(date);
		weekString = weekString + dateString + monthString +yearString;
		
		return weekString;
	}
	/**
	 * get String date to show, the format is 'yyyy-mm-dd HH:mm:ss' 
	 * @param date
	 * @return String
	 */
	public static String dateTimeToString(Date date) {

		String dateStr = "";

		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			dateStr = sdfDateToStr.format(date);
		}

		return dateStr;
	}


	public static String dateTimeToString(Date date, String format) {

		String dateStr = "";

		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(format);
			dateStr = sdfDateToStr.format(date);
		}

		/*String dateStr = "";

		if (date != null) {
			dateStr = android.text.format.DateFormat.format(format, date).toString();
			*//*android.text.format.DateFormat sdfDateToStr = new DateFormat(format, Locale.getDefault());
			dateStr = sdfDateToStr.format(date);*//*
		}*/
/*		Log.d("Date", "dateStr===" + date);
		Log.d("Date", "dateStr===" + dateStr);*/
		return dateStr;
	}

	public static Date stringToDateTime(String dateStr, String format) {
		Date date = null;
		if (dateStr != null) {
			try {
				dateStr = dateStr.replace('T', ' ');
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
				if (!StringUtils.isEmpty(dateStr)) {
					date = simpleDateFormat.parse(dateStr);
				}

			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return date;
	}

	
	/**
	 * get String date to show, the format is 'yyyy-mm-dd' 
	 * @param date
	 * @return String
	 */
	public static String dateToString(Date date) {

		String dateStr = "";

		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(
					"yyyy-MM-dd");
			dateStr = sdfDateToStr.format(date);
		}

		return dateStr;
	}

	
	/**
	 * get String date to show, the format is 'yyyymmdd' 
	 * @param date
	 * @return String
	 */
	public static String dateToStringNoMiddleLine(Date date) {

		String dateStr = "";

		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(
					"yyyyMMdd");
			dateStr = sdfDateToStr.format(date);
		}

		return dateStr;
	}
	
	/**
	 * get String date to show, the format is 'HH:mm:ss' 
	 * @param date
	 * @return String
	 */
	public static String timeToString(Date date) {

		String dateStr = "";

		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(
					"HH:mm:ss");
			dateStr = sdfDateToStr.format(date);
		}

		return dateStr;
	}
	public static String dateToStringNoSpace(Date date) {

		String dateStr = "";

		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat(
					"yyyyMMddHHmmss");
			dateStr = sdfDateToStr.format(date);
		}

		return dateStr;
	}
	
	public static String getDayOfToday(){
		Date date = Calendar.getInstance().getTime();		
		String dateStr = "";
		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat("dd");
			dateStr = sdfDateToStr.format(date);
		}
		return dateStr;
	}
	
	public static String getYearMonthOfToday(){
		Date date = Calendar.getInstance().getTime();		
		String dateStr = "";
		if (date != null) {
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat("yyyy-MM");
			dateStr = sdfDateToStr.format(date);
		}
		return dateStr;
	}

	public static String getFewLaterDayOfToday(int fewLaterDay) {
		String dateStr = "";
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(calendar.getTime());			
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + fewLaterDay); //add 1 day
			SimpleDateFormat sdfDateToStr = new SimpleDateFormat("dd");
			dateStr = sdfDateToStr.format(calendar.getTime());
			return dateStr;
		} catch (Exception e) {
			return "";
		}
		
	}
	public static Date getFewLaterDay(Date date, int fewLaterDay) {
		
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);			
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + fewLaterDay); //add 1 day
			return calendar.getTime();
			
		} catch (Exception e) {
			return null;
		}
		
	}
	public static Date getAfterManyHour(Date date, int howManyHour) {
		
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);			
			calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + howManyHour); //add 1 day
			return calendar.getTime();
			
		} catch (Exception e) {
			return null;
		}
		
	}
	public static Date getAfterManyMin(Date date, int howManyHour) {

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + howManyHour); //add 1 day
			return calendar.getTime();

		} catch (Exception e) {
			return null;
		}

	}
	public static Date getTheDayBeforeYesterday(int dossierAftersalesLifetime){

		try {
			Calendar calendar = Calendar.getInstance();
			//calendar.setTime(date);		
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - dossierAftersalesLifetime); //add 1 day
			return calendar.getTime();
			
		} catch (Exception e) {
			return null;
		}
	}
	 
	public static Date getOneHourLaterTime(Date date) {
				
		Calendar ca = Calendar.getInstance(); 

		ca.setTime(date);

		int num = ca.get(Calendar.HOUR_OF_DAY) + 1;// Add one hour
		ca.set(Calendar.HOUR_OF_DAY, num);
		return ca.getTime();
		
	}      
	public static String getDateWithTimeZone(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(date);		  
	}
	
	public static int getMinutes(String duration){
		SimpleDateFormat formatter = new SimpleDateFormat(
                "HH:mm:ss");
		int minutes = 0;
		
		try {
			Date date = formatter.parse(duration);
			minutes = date.getMinutes();
			
		} catch (ParseException e) {
			e.printStackTrace();
			return minutes;
		}
		return minutes;
	}
	public static int getHours(String duration){
		SimpleDateFormat formatter = new SimpleDateFormat(
                "HH:mm:ss");
		int houres = 0;
		try {
			Date date = formatter.parse(duration);
			houres = date.getHours();
			
		} catch (ParseException e) {
			e.printStackTrace();
			return houres;
		}
		return houres;
	}
	
	public static String msToMinuteAndSecond(long second){
		if (second == 0) {
			return "";
		}
		java.text.DateFormat formatter = new SimpleDateFormat("mm:ss");
		long ms = 1000;
		long now = second * ms;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now);
		return formatter.format(calendar.getTime());
	}
	
	public static String getMinuteAndSecond(Date date){
		java.text.DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(date.getTime());
	}
	
	public static Date getSecondBeforeTime(Date date, int second) {
		
		Calendar ca = Calendar.getInstance(); 

		ca.setTime(date);

		int num = ca.get(Calendar.SECOND) - second;// Add one hour
		ca.set(Calendar.SECOND, num);
		return ca.getTime();
		
	} 
	
	public static String getHour(long duration){
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

		int hour = 0;
		int minutes = 0;
		try {
			Date date = formatter.parse(String.valueOf(duration));
			hour = date.getHours();
			minutes = date.getMinutes();
		} catch (ParseException e) {
			e.printStackTrace();

		}

		return hour + ":" + minutes;
	}
	
	public static Date getFullDate(String dateString){
		
		SimpleDateFormat sfd  = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.US) ;
		Date dateStore = null;
		try {
			dateStore = sfd.parse(dateString);


		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateStore;
	}

	public static String getDageByLong(long duration){

		Date date = new Date(duration);

		return dateToString(date);
	}
	
	public static Date stringToTime(String dateString){
		
		SimpleDateFormat sfd  = new SimpleDateFormat("HH:mm:ss") ;
		Date dateStore = null;
		try {
			dateStore = sfd.parse(dateString);


		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateStore;
	}

	public static Date getDate(Context context, Date date){
;
		String dateString = dateToString(context, date);
		Date newDate = stringToDate(context, dateString);
		return newDate;
	}

	public static String getRightFormat(){
		String language = NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey();
		String format = "";
		Log.e("language", "language..." + language);
		if (SettingService.LANGUAGE_EN.contains(language)) {
			format = formatDateEn;
		}else if(SettingService.LANGUAGE_FR.contains(language)){
			format = formatDateNl;
		}else if(SettingService.LANGUAGE_NL.contains(language)){
			format = formatDateNl;
		}else {
			format = formatDateEn;
		}
		return format;
	}
}
