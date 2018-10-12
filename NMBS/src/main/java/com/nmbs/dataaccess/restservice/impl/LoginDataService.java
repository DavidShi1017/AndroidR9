package com.nmbs.dataaccess.restservice.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.nmbs.R;
import com.nmbs.dataaccess.converters.CheckOptionConverter;
import com.nmbs.dataaccess.converters.CheckPwdConverter;
import com.nmbs.dataaccess.converters.ForgotPwdInfoConverter;
import com.nmbs.dataaccess.converters.LoginConverter;
import com.nmbs.dataaccess.converters.ProfileInfoConverter;
import com.nmbs.dataaccess.converters.ResendInfoConverter;
import com.nmbs.dataaccess.database.CheckOptionBaseService;
import com.nmbs.dataaccess.database.LoginDataBaseService;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CheckOption;
import com.nmbs.model.ForgotPwdInfoResponse;
import com.nmbs.model.LastUpdateTimestampPassword;
import com.nmbs.model.LoginResponse;
import com.nmbs.model.LogonInfo;
import com.nmbs.model.ProfileInfoResponse;
import com.nmbs.model.ResendInfoResponse;
import com.nmbs.receivers.CheckOptionReceiver;
import com.nmbs.services.impl.LoginService;

import com.nmbs.util.HTTPRestServiceCaller;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;


public class LoginDataService {

    public LoginResponse executeLogon(Context context, String language, String email, String pwd, String loginProvider, String token) throws Exception{
        LogUtils.d("LoginDataService", "executeLogon------->");
        String errorMsg = "";
        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "";
        String urlString = context.getString(R.string.server_url_logon);
        String response = "";
        if(LoginService.LOGIN_PROVIDER_CRIS.equalsIgnoreCase(loginProvider)){
            postJsonString = "{\"Email\":\""+ email +"\",\"Password\":\""+ pwd + "\"}";
            urlString = context.getString(R.string.server_url_logon);
            LogUtils.d("LoginDataService", "executeLogon----postJsonString--->" + postJsonString + "----language---->" + language);
            response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                    HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                    HTTPRestServiceCaller.API_VERSION_VALUE_7);
        }else {
            postJsonString = "{\"LogonType\":\""+ loginProvider +"\",\"Token\":\""+ token + "\"}";
            urlString = context.getString(R.string.server_url_logon_social);
            LogUtils.d("LoginDataService", "executeLogon----postJsonString--->" + postJsonString + "----language---->" + language);
            response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                    HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                    HTTPRestServiceCaller.API_VERSION_VALUE_7);
        }

/*        String response = "{ \n" +
                "   \"LogonInfo\":{ \n" +
                "      \"CustomerId\":\"1055584\",\n" +
                "      \"FirstName\":\"Wimm\",\n" +
                "      \"Email\":\"meerschmanw@delaware.be\",\n" +
                "      \"PhoneNumber\":\"+32471256389\",\n" +
                "      \"State\":\"Success\",\n" +
                "      \"StateDescription\":\"#Logon_Success_Description#\",\n" +
                "      \"PersonId\":\"123456\",\n" +
                "      \"LastUpdateTimestampPassword\":\"2018-05-11T17:54:21+02:00\"\n" +
                "   },\n" +
                "   \"Messages\":[ \n" +
                "   ],\n" +
                "   \"DebugMessages\":[ \n" +
                "   ]\n" +
                "}";*/

        LogUtils.d("LoginDataService", "executeLogon----response str--->" + response);

        LoginConverter converter = new LoginConverter();
        LoginResponse loginResponse = converter.parseResponse(response);
        if(loginResponse != null && loginResponse.getLogonInfo() != null){
            LogUtils.d("LoginService", "login---state---->" + loginResponse.getLogonInfo().getState());
            if(!"Success".equalsIgnoreCase(loginResponse.getLogonInfo().getState())){
                LogUtils.d("LoginService", "login---state---Description--->" + loginResponse.getLogonInfo().getStateDescription());
                errorMsg = loginResponse.getLogonInfo().getStateDescription();
            }else{
                // Save login info
                if(loginResponse.getLogonInfo().getPhoneNumber() != null && !loginResponse.getLogonInfo().getPhoneNumber().isEmpty()){
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(loginResponse.getLogonInfo().getPhoneNumber(), "");
                    int countryCode = numberProto.getCountryCode();
                    long phoneNumber = numberProto.getNationalNumber();
                    loginResponse.getLogonInfo().setCode(String.valueOf(countryCode));
                    loginResponse.getLogonInfo().setPhoneNumber(String.valueOf(phoneNumber));
                }
                loginResponse.getLogonInfo().setLoginProvider(loginProvider);
                LoginDataBaseService dataBaseService = new LoginDataBaseService(context);
                dataBaseService.deleteLogonInfo();
                dataBaseService.insertLogonInfo(loginResponse.getLogonInfo());
            }
        }

        return loginResponse;
    }


    public ResendInfoResponse executeResend(Context context, String language, String customerId, String hash) throws Exception{
        LogUtils.d("LoginDataService", "executeResend------->");
        String errorMsg = "";
        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "{\"CustomerId\":\"" + customerId + "\",\"Hash\":\"" + hash + "\"}";
        String urlString = context.getString(R.string.server_url_resend);
        LogUtils.d("LoginDataService", "executeResend----postJsonString--->" + postJsonString + "----language---->" + language);
        String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                HTTPRestServiceCaller.API_VERSION_VALUE_7);
        /*String response = "{\"ResendInfo\":{\"Success\":true},\"Messages\":[],\"DebugMessages\":[]}";*/

        LogUtils.d("LoginDataService", "executeResend----response str--->" + response);

        ResendInfoConverter converter = new ResendInfoConverter();
        ResendInfoResponse resendInfoResponse = converter.parseResponse(response);

        return resendInfoResponse;
    }

    public ForgotPwdInfoResponse executeForgotPwd(Context context, String language, String email) throws Exception{
        LogUtils.d("LoginDataService", "executeResend------->");
        String errorMsg = "";
        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "{\"Email\":\"" + email + "\"}";
        String urlString = context.getString(R.string.server_url_forgot_pwd);
        LogUtils.d("LoginDataService", "executeForgotPwd----postJsonString--->" + postJsonString + "----language---->" + language);
        String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                HTTPRestServiceCaller.API_VERSION_VALUE_7);
        //String response = "{\"Info\":{\"Success\":false,\"FailureMessage\":\"#MF_App_PasswordCouldNotBeResetProfileBlocked#\",\"ResetLogin\":false},\"Messages\":[],\"DebugMessages\":[]}";

        LogUtils.d("LoginDataService", "executeForgotPwd----response str--->" + response);

        ForgotPwdInfoConverter converter = new ForgotPwdInfoConverter();
        ForgotPwdInfoResponse forgotPwdInfoResponse = converter.parseResponse(response);

        return forgotPwdInfoResponse;
    }


    public void executeProfileInfoSync(Context context, String language, String customerId, String control) throws Exception{
        LogUtils.d("LoginDataService", "executeProfileInfoSync------->");
        String errorMsg = "";
        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "{\"Id\":\"" + customerId + "\",\"Control\":\"" + control + "\"}";
        String urlString = context.getString(R.string.server_url_profile_info);
        LogUtils.d("LoginDataService", "executeProfileInfoSync----postJsonString--->" + postJsonString + "----language---->" + language);
        String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                HTTPRestServiceCaller.API_VERSION_VALUE_7);
        //String response = "{\"ProfileInfo\":{\"FirstName\":\"sss\",\"Email\":\"bla@bla.com\",\"MobilePhone\":\"+32471256389\"},\"Messages\":[],\"DebugMessages\":[]}";

        LogUtils.d("LoginDataService", "executeProfileInfoSync----response str--->" + response);

        ProfileInfoConverter converter = new ProfileInfoConverter();
        ProfileInfoResponse profileInfoResponse = converter.parseResponse(response);
        if(profileInfoResponse != null && profileInfoResponse.getProfileInfo() != null){
            LoginDataBaseService loginDataBaseService = new LoginDataBaseService(context);
            LogonInfo logonInfo = loginDataBaseService.getLogonInfo();
            if(logonInfo != null){
                loginDataBaseService.deleteLogonInfo();
                String firstName = "";
                String email = "";
                if(profileInfoResponse.getProfileInfo().getMobilePhone() != null){
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(profileInfoResponse.getProfileInfo().getMobilePhone(), "");
                    int countryCode = numberProto.getCountryCode();
                    long phoneNumber = numberProto.getNationalNumber();
                    logonInfo.setCode(String.valueOf(countryCode));
                    logonInfo.setPhoneNumber(String.valueOf(phoneNumber));
                }
                if(profileInfoResponse.getProfileInfo().getFirstName() != null){
                    firstName = profileInfoResponse.getProfileInfo().getFirstName();
                }
                if(profileInfoResponse.getProfileInfo().getFirstName() != null){
                    email = profileInfoResponse.getProfileInfo().getEmail();
                }
                LogonInfo logonInfo1Update = new LogonInfo(logonInfo.getCustomerId(), firstName, logonInfo.getEmail(), logonInfo.getCode(),
                        logonInfo.getPhoneNumber(), logonInfo.getState(), logonInfo.getStateDescription(), logonInfo.getPersonId(),
                        logonInfo.getLastUpdateTimestampPassword(), logonInfo.getLoginProvider());
                loginDataBaseService.insertLogonInfo(logonInfo1Update);
            }
        }

    }

    public void executeCheckOption(Context context, String language, String customerId, String control) throws Exception{
        LogUtils.d("LoginDataService", "executeCheckOption------->");

        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "{\"CustomerId\":\"" + customerId + "\",\"Control\":\"" + control + "\"}";
        String urlString = context.getString(R.string.server_url_check_option);
        LogUtils.d("LoginDataService", "executeCheckOption----postJsonString--->" + postJsonString + "----language---->" + language);
        String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                HTTPRestServiceCaller.HTTP_POST_METHOD, 10000, false, "",
                HTTPRestServiceCaller.API_VERSION_VALUE_7);
        /*String response = "{\n" +
                "  \"Count\": 10,\n" +
                "  \"Expiration\": \"2018-07-14T14:45:00\",\n" +
                "  \"CallSuccessful\": true,\n" +
                "  \"Messages\": []\n" +
                "}";*/

        LogUtils.d("LoginDataService", "executeCheckOption----response str--->" + response);

        CheckOptionConverter converter = new CheckOptionConverter();
        CheckOption parseResponse = converter.parseResponse(response);
        if(parseResponse != null && parseResponse.isCallSuccessful()){
            CheckOptionBaseService checkOptionBaseService = new CheckOptionBaseService(context);
            //CheckOption checkOption = checkOptionBaseService.getCheckOption();
            if(parseResponse.getCount() > 0){
                //FileManager.getInstance().createExternalStoragePrivateFileFromString(context, "CheckOption",  "CheckOption.json", stringHttpResponse);
                checkOptionBaseService.insertCheckOption(parseResponse);
                if(parseResponse.getExpiration().after(new Date())){
                    createCheckOptionReceiver(context, getReceiverTime(parseResponse.getExpiration()), 123456);
                }
            }else {
                parseResponse.setExpiration(null);
                checkOptionBaseService.insertCheckOption(parseResponse);
                cancelCheckOptionReceiver(context, 123456);
            }
            LogUtils.d("LoginDataService", "executeCheckOption----Count--->" + parseResponse.getCount());
            LogUtils.d("LoginDataService", "executeCheckOption----Date--->" + parseResponse.getExpiration());
        }

    }

    public LastUpdateTimestampPassword executeCheckPwd(Context context, String language, String personId, LogonInfo logonInfo) throws Exception{
        LogUtils.d("LoginDataService", "executeCheckPwd------->");

        HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
        String postJsonString = "{\"PersonId\":\"" + personId + "\"}";
        String urlString = context.getString(R.string.server_url_check_pwd);
        LogUtils.d("LoginDataService", "executeCheckPwd----postJsonString--->" + postJsonString + "----language---->" + language);
        String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, language,
                HTTPRestServiceCaller.HTTP_POST_METHOD, 3000, false, "",
                HTTPRestServiceCaller.API_VERSION_VALUE_7);
        /*String response = "{\n" +
                "  \"LastUpdateTimestampPassword\":\"2018-07-17T17:54:21+02:00\",\n" +
                "  \"Messages\": []\n" +
                "}";*/

        LogUtils.d("LoginDataService", "executeCheckPwd----response str--->" + response);

        CheckPwdConverter converter = new CheckPwdConverter();
        LastUpdateTimestampPassword parseResponse = converter.parseResponse(response);

        return parseResponse;
    }

    public void saveCheckOption(Context context, int count, Date expiration){
        CheckOption checkOption = new CheckOption(count, expiration);
        CheckOptionBaseService baseService = new CheckOptionBaseService(context);
        baseService.insertCheckOption(checkOption);
        if(checkOption.getExpiration().after(new Date())){
            createCheckOptionReceiver(context, getReceiverTime(checkOption.getExpiration()), 123456);
        }
    }

    public void cancelCheckOption(Context context, CheckOption checkOption){
        checkOption.setExpiration(null);
        CheckOptionBaseService baseService = new CheckOptionBaseService(context);
        baseService.insertCheckOption(checkOption);
        cancelCheckOptionReceiver(context, 123456);
    }

    private void createCheckOptionReceiver(Context context, long time, int id){
        LogUtils.e("CheckOptionReceiver", "createCheckOptionReceiver...");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, CheckOptionReceiver.class);
        notificationIntent.putExtra("RequestCode", 00);
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, broadcast);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, broadcast);
        }
    }

    private void cancelCheckOptionReceiver(Context context, int id){
        LogUtils.e("cancel", "id..." + id);
        Intent intent = new Intent(context, CheckOptionReceiver.class);
        //intent.setData(Uri.parse("content://calendar/calendar_alerts/1"));
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // And cancel the alarm.
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    private long getReceiverTime(Date date){

        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        Log.e("CheckOptionReceiver", "getPushTime..." + cal.getTime());
        return cal.getTimeInMillis();
    }

    public LogonInfo getLogonInfo(Context context){
        LoginDataBaseService dataBaseService = new LoginDataBaseService(context);
        LogonInfo logonInfo = dataBaseService.getLogonInfo();
        if(logonInfo != null){
            LogUtils.d("LoginDataService", "getLogonInfo----LogonInfo is not null--->" + logonInfo.getFirstName());
        }
        return logonInfo;
    }
}
