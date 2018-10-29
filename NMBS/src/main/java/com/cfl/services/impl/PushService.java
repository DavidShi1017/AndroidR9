package com.cfl.services.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import com.cfl.log.LogUtils;
import com.cfl.receivers.LocalNotificationWakefulBroadcastReceiver;
import com.google.firebase.iid.FirebaseInstanceId;
import com.cfl.application.NMBSApplication;
import com.cfl.async.GetSubScriptionListAsyncTask;
import com.cfl.dataaccess.database.HafasUserDataBaseService;
import com.cfl.dataaccess.database.SubscriptionDataBaseService;
import com.cfl.dataaccess.database.TravelSegmentDatabaseService;
import com.cfl.exceptions.RequestFail;
import com.cfl.model.CreateSubscriptionParameter;
import com.cfl.model.DossierTravelSegment;
import com.cfl.model.HafasUser;
import com.cfl.model.LocalNotification;
import com.cfl.model.Subscription;
import com.cfl.model.SubscriptionResponse;
import com.cfl.model.SubscriptionResponseResult;
import com.cfl.services.IPushService;
import com.cfl.util.AlarmReceiver;
import com.cfl.util.DateUtils;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.Utils;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Richard on 4/1/16.
 */
public class PushService implements IPushService {
    private final static String REGISTRATION_ID_SHAREDPREFERENCES = "RegistrationId_SharePreference";
    private final static String REGISTRATION_ID_SHAREDPREFERENCES_KEY = "RegistrationId";
    public final static int USER_ERROR = 123456;
    HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
    private Context applicationContext;
    private static final String TAG = "PushService";
    public PushService(Context context){
        this.applicationContext = context;
    }

    @Override
    public HafasUser getUser(){
        if(NMBSApplication.getInstance().getTestService().isEmptyUser()){
            return null;
        }
        HafasUserDataBaseService hafasUserDataBaseService = new HafasUserDataBaseService(applicationContext);
        HafasUser hafasUser = hafasUserDataBaseService.readHafasUser();

        return hafasUser;
    }

    @Override
    public void saveRegistrationId(String registrationId) {
        SharedPreferences messageInfo = applicationContext.getSharedPreferences(REGISTRATION_ID_SHAREDPREFERENCES, 0);
        messageInfo.edit().putString(REGISTRATION_ID_SHAREDPREFERENCES_KEY, registrationId).commit();
    }

    @Override
    public String getRegistrationId() {
        SharedPreferences messageInfo = applicationContext.getSharedPreferences(REGISTRATION_ID_SHAREDPREFERENCES, 0);
        String registrationId = messageInfo.getString(REGISTRATION_ID_SHAREDPREFERENCES_KEY, "");
        return registrationId;
    }

    public SubscriptionResponseResult deleteAllSubscription(String userId, List<Subscription> subscriptions) throws RequestFail{
        StringBuffer deleteDataStr = new StringBuffer("<SubscriptionRequestList xmlns=\"hafas_abo_v1\">");
        for(Subscription subscription:subscriptions){
            deleteDataStr.append("    <deleteSubscriptionRequest  id=\""+subscription.getSubscriptionId()+"\" userId=\""+userId+"\" subscriptionId=\""+subscription.getSubscriptionId()+"\"> </deleteSubscriptionRequest> ");
        }
        deleteDataStr.append("</SubscriptionRequestList>");

        String responseData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), deleteDataStr.toString());
        //Log.i(TAG, responseData);
        XmlPullParser parser = Xml.newPullParser();
        String responseResult = "";
        SubscriptionResponseResult subscriptionResponseResult = null;
        List<SubscriptionResponse> subscriptionResponseList = new ArrayList<SubscriptionResponse>();
        try {
            parser.setInput(new StringReader(responseData));
            int event = parser.getEventType();
            SubscriptionResponse subscriptionResponse = null;
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("SubscriptionResponseList")) {
                            subscriptionResponseResult = new SubscriptionResponseResult();
                        }
                        if(subscriptionResponse == null&&name.equalsIgnoreCase("resultCode")&&subscriptionResponseResult!=null){
                            if(parser.nextText().equals("OK")){
                                subscriptionResponseResult.setSuccess(true);
                            }else{
                                subscriptionResponseResult.setSuccess(false);
                            }
                        }

                        if (name.equalsIgnoreCase("deleteSubscriptionResponse")) {
                            subscriptionResponse = new SubscriptionResponse();
                            subscriptionResponse.setSubscriptionId(parser.getAttributeValue(null, "id"));
                        } else {
                            if(subscriptionResponse != null){
                                if (name.equalsIgnoreCase("subscriptionId")) {
                                    subscriptionResponse.setSubscriptionId(parser.nextText());// 如果后面是Text元素,即返回它的值
                                } else if (name.equalsIgnoreCase("resultCode")) {
                                    if(parser.nextText().equals("OK")){
                                        subscriptionResponse.setSuccess(true);
                                    }else{
                                        subscriptionResponse.setSuccess(false);
                                    }
                                } else if(name.equalsIgnoreCase("externalError")){
                                    if(parser.nextText().equals("NO_SUCH_SUBSCRIPTION")){
                                        subscriptionResponse.setErrorType(SubscriptionResponse.ErrorType.NOSUCHSUBSCRIPTION);
                                    }
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("deleteSubscriptionResponse")&& subscriptionResponse != null) {
                            subscriptionResponseList.add(subscriptionResponse);
                            subscriptionResponse = null;
                        }
                        if (parser.getName().equalsIgnoreCase("SubscriptionResponseList")&&subscriptionResponseResult != null) {
                            subscriptionResponseResult.setSubscriptionResponses(subscriptionResponseList);
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscriptionResponseResult;
    }

    private String sendUserToServer(HafasUser hafasUser) throws RequestFail{
        LogUtils.i("Push", "sendUserToServer.... " );
        String postUserData = "<SubscriptionRequestList\n" +
                "    xmlns=\"hafas_abo_v1\">\n" +
                "    <createUserRequest language=\""+hafasUser.getLanguage()+"\" id=\"1\" >\n" +
                "        <hysteresis>\n" +
                "            <minDeviationInterval>" + hafasUser.getMinDeviationInterval() + "</minDeviationInterval>\n" +
                "                <minDeviationFollowing>" + hafasUser.getMinDeviationFollowing() + "</minDeviationFollowing>\n" +
                "                <notificationStart>" + hafasUser.getNotificationStart() + "</notificationStart>\n" +
                "        </hysteresis>\n" +
                "        <channels>\n" +
                "            <channel>\n" +
                "                <type>ANDROID</type>\n" +
                "                <address>"+hafasUser.getRegisterId()+"</address>\n" +
                "            </channel>\n" +
                "        </channels>\n" +
                "    </createUserRequest>\n" +
                "</SubscriptionRequestList>";
        LogUtils.i(TAG, "responseUserData...." + postUserData);
        String responseUserData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), postUserData);
        LogUtils.i(TAG, "responseUserData...." + responseUserData);
        XmlPullParser parser = Xml.newPullParser();
        String userId = "";
        try{
            parser.setInput(new StringReader(responseUserData));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("createUserResponse".equals(parser.getName())) {
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String key = parser.getAttributeName(i);
                                if ("userId".equals(key)) {
                                    userId = parser.getAttributeValue(i);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //Log.i(TAG, "++++= " + userId);
        if(!userId.equals("")){
            HafasUser tempHafasUser = new HafasUser(userId,hafasUser.getRegisterId(),hafasUser.getLanguage());
            this.saveHafasUserInfo(tempHafasUser);
        }
        return userId;
    }

    public String updateAccount(HafasUser hafasUser) throws RequestFail{
        String postUserData = "<SubscriptionRequestList\n" +
                "    xmlns=\"hafas_abo_v1\">\n" +
                "    <updateUserRequest language=\""+hafasUser.getLanguage()+"\" id=\"1\"  userId=\""+hafasUser.getUserId()+"\">\n" +
                "        <hysteresis>\n" +
                "            <minDeviationInterval>" + hafasUser.getMinDeviationInterval() + "</minDeviationInterval>\n" +
                "                <minDeviationFollowing>" + hafasUser.getMinDeviationFollowing() + "</minDeviationFollowing>\n" +
                "                <notificationStart>" + hafasUser.getNotificationStart() + "</notificationStart>\n" +
                "        </hysteresis>\n" +
                "        <channels>\n" +
                "            <channel>\n" +
                "                <type>ANDROID</type>\n" +
                "                <address>"+hafasUser.getRegisterId()+"</address>\n" +
                "            </channel>\n" +
                "        </channels>\n" +
                "    </updateUserRequest>\n" +
                "</SubscriptionRequestList>";
        Log.e(TAG, postUserData);
        String responseUserData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), postUserData);
        Log.e(TAG, responseUserData);
        XmlPullParser parser = Xml.newPullParser();
        String responseResult = "";
        try{
            parser.setInput(new StringReader(responseUserData));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("resultCode".equals(parser.getName())) {
                            event = parser.next();//让解析器指向name属性的值
                            // 得到name标签的属性值，并设置beauty的name
                            responseResult = parser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if("OK".equals(responseResult)){
            HafasUser tmpHafasUser = new HafasUser(hafasUser.getUserId(),hafasUser.getRegisterId(),hafasUser.getLanguage());
            this.deleteHafasUserInfo(hafasUser.getUserId());
            this.saveHafasUserInfo(tmpHafasUser);
        }
        return hafasUser.getUserId();
    }

    @Override
    public String createAccount(HafasUser hafasUser) throws RequestFail{
        LogUtils.i("Push", "createAccount.... ");

        HafasUser user = this.getUser();
        if(user != null&&!user.getUserId().equals("")){
            if(!hafasUser.getLanguage().equals(user.getLanguage())||!hafasUser.getRegisterId().equals(user.getRegisterId())){
                LogUtils.i("Push", "updateAccount.... ");
                return updateAccount(hafasUser);
            }else{
                LogUtils.i("Push", "user.getUserId().... " + user.getUserId());
                return user.getUserId();
            }
        }else{
            if(hafasUser != null && hafasUser.getRegisterId() != null)
                return sendUserToServer(hafasUser);
            else
                return "";
        }
    }

    @Override
    public String createSubScription(CreateSubscriptionParameter subScription) throws RequestFail{

        Log.e(TAG, "createSubScription............");
        String postData = "<SubscriptionRequestList\n" +
                "    xmlns=\"hafas_abo_v1\">\n" +
                "    <createSubscriptionRequest id=\"1\" userId=\""+subScription.getUserId()+"\">\n" +
                "        <serviceSubscription>\n" +
                "            <reconCtx>"+subScription.getReconCtx()+"</reconCtx>\n" +
                "            <serviceDays>\n" +
                "                <beginDate>"+subScription.getStartDate()+"</beginDate>\n" +
                "                <endDate>"+subScription.getEndDate()+"</endDate>\n" +
                "                <selected>"+subScription.getSelected()+"</selected>\n" +
                "            </serviceDays>\n" +
                "            <hysteresis>\n" +
                "                <minDeviationInterval>" + subScription.getMinDeviationInterval() + "</minDeviationInterval>\n" +
                "                <minDeviationFollowing>" + subScription.getMinDeviationFollowing() + "</minDeviationFollowing>\n" +
                "                <notificationStart>" + subScription.getNotificationStart() + "</notificationStart>\n" +
                "            </hysteresis>\n" +
                "        </serviceSubscription>\n" +
                "        <channels>\n" +
                "            <channel>\n" +
                "                <type>ANDROID</type>\n" +
                "                <address>"+subScription.getAddress()+"</address>\n" +
                "            </channel>\n" +
                "        </channels>\n" +
                "    </createSubscriptionRequest>\n" +
                "</SubscriptionRequestList>";
        //System.out.println(postData);
        Log.e(TAG, postData);
        String responseData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), postData);
        Log.e(TAG, responseData);
        XmlPullParser parser = Xml.newPullParser();
        String subscriptionId = "";
        try{
            parser.setInput(new StringReader(responseData));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("subscriptionId".equals(parser.getName())) {
                            event = parser.next();//让解析器指向name属性的值
                            // 得到name标签的属性值，并设置beauty的name
                            subscriptionId = parser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return subscriptionId;
    }

    public boolean saveHafasUserInfo(HafasUser hafasUser){
        HafasUserDataBaseService hafasUserDataBaseService = new HafasUserDataBaseService(applicationContext);
        return hafasUserDataBaseService.insertHafasUser(hafasUser);
    }

    public boolean deleteHafasUserInfo(String userId){
        HafasUserDataBaseService hafasUserDataBaseService = new HafasUserDataBaseService(applicationContext);
        return hafasUserDataBaseService.deleteHafasUser(userId);
    }

    public boolean saveSubscriptionInLocal(Subscription subscription){

        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        if(subscription!= null){
            Subscription subscriptionRepeated = subscriptionDataBaseService.readSubscriptionByAll(subscription);
            if(subscriptionRepeated == null){
                return subscriptionDataBaseService.insertSubscription(subscription);
            }
        }
            return false;

    }
    public boolean getSubscriptionFromLocal(String departure){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.readSubscriptionByReconctx(departure);
    }

    public boolean isLinkedDnr(String dnr){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.isLinkedWithDnr(dnr);
    }

    public boolean isSubscriptionExistLocal(String reconctx, Date departure){
        try {
            if(reconctx != null){
                reconctx = Utils.sha1(reconctx);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String date = DateUtils.getDateWithTimeZone(departure);
//        Log.d("Connection", "date..." + date);
                SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.readSubscriptionByReconctxAndDate(reconctx, date);
    }

    public boolean isSubscriptionExistLocal(String originStationRcode, String oestinationStationRcode , Date departure){

        String date = DateUtils.getDateWithTimeZone(departure);
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.readSubscriptionByCodeAndDate(originStationRcode, oestinationStationRcode, date);
    }

    public Subscription getSubscriptionFromLocal(String reconctx, Date departure){
        try {
            reconctx = Utils.sha1(reconctx);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String date = DateUtils.getDateWithTimeZone(departure);
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.readSubscriptionByReconctxAndDepartureDate(reconctx, date);
    }

    public List<Subscription> readAllSubscriptionsByDnr(String dossierId){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        List<Subscription> subscriptions = subscriptionDataBaseService.readSubscriptionByDnr(dossierId);
        return subscriptions;
    }
    public Subscription readSubscriptionByConnection(String connectionId){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        Subscription subscription = subscriptionDataBaseService.readSubscriptionByConnectionId(connectionId);
        return subscription;
    }
    public boolean isDossierPushEnabled(String dnr){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.isDossierPushEnabled(dnr);
    }

    public boolean deleteSubscription(String userId,String subScriptionId) throws RequestFail{
        String postData = "<SubscriptionRequestList xmlns=\"hafas_abo_v1\"> \n" +
                "<deleteSubscriptionRequest id=\"1\" subscriptionId=\""+subScriptionId+"\" userId=\""+userId+"\">\n" +
                "</deleteSubscriptionRequest> \n" +
                "</SubscriptionRequestList>";
        //System.out.println(postData);
        String responseData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), postData);
        //Log.i(TAG, responseData);
        XmlPullParser parser = Xml.newPullParser();
        String responseCode = "";
        try{
            parser.setInput(new StringReader(responseData));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("resultCode".equals(parser.getName())) {
                            event = parser.next();//让解析器指向name属性的值
                            // 得到name标签的属性值，并设置beauty的name
                            responseCode = parser.getText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(responseCode.equals("OK")){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteSubscriptionInLocal(String subscriptionId){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        //Log.e(TAG, "deleteSubscriptionInLocal....");
        return subscriptionDataBaseService.deleteSubscription(subscriptionId);
    }

    public List<Subscription> readAllSubscriptions(){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.readSubscriptions();
    }
    public List<Subscription> readAllSubscriptionsWithDnr(){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        List<Subscription> subscriptionList = subscriptionDataBaseService.readSubscriptions();
        List<Subscription> subscriptionsWithDnr = new ArrayList<>();
        for(Subscription subscription : subscriptionList) {
            if (!subscription.getDnr().equals("")) {
                subscriptionsWithDnr.add(subscription);
            }
        }
        Collections.sort(subscriptionsWithDnr, new Comparator<Subscription>() {
            public int compare(Subscription o1, Subscription o2) {
                Date tempDate1 = DateUtils.stringToDateTime(o1.getDeparture());
                Date tempDate2 = DateUtils.stringToDateTime(o2.getDeparture());
                if (tempDate1.getTime() - tempDate2.getTime() > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return subscriptionsWithDnr;
    }
    public List<Subscription> readAllSubscriptionsNoDnr(){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        List<Subscription> subscriptionList = subscriptionDataBaseService.readSubscriptions();
        List<Subscription> subscriptionsNoDnr = new ArrayList<>();
        for(Subscription subscription : subscriptionList) {
            if (subscription.getDnr().equals("")) {
                subscriptionsNoDnr.add(subscription);
            }
        }
        Collections.sort(subscriptionsNoDnr, new Comparator<Subscription>() {
            public int compare(Subscription o1, Subscription o2) {
                Date tempDate1 = DateUtils.stringToDateTime(o1.getDeparture());
                Date tempDate2 = DateUtils.stringToDateTime(o2.getDeparture());
                if (tempDate1.getTime() - tempDate2.getTime() > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return subscriptionsNoDnr;
    }

    public boolean getSubscriptionsByUserId(String userId) throws Exception {

        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        List<Subscription> subscriptionList = new ArrayList<Subscription>();
        String postData = "<SubscriptionRequestList xmlns=\"hafas_abo_v1\"> \n" +
                "<getSubscriptionListRequest id=\"1\" userId=\""+userId+"\">\n" +
                "</getSubscriptionListRequest> \n" +
                "</SubscriptionRequestList>";
        //System.out.println(postData);
        String responseData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), postData);
        //Log.i(TAG, responseData);
        XmlPullParser parser = Xml.newPullParser();
        Subscription subscription = null;
        String responseCode = "";
        try{
            parser.setInput(new StringReader(responseData));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("subscription")) {
                            subscription = new Subscription();
                        } else if (parser.getName().equals("subscriptionId")) {
                            event = parser.next();
                            subscription.setSubscriptionId(parser.getText());
                        } else if (parser.getName().equals("subscriptionStatus")) {
                            event = parser.next();
                            subscription.setSubscriptionStatus(parser.getText());
                        }else if(parser.getName().equals("language")){
                            event = parser.next();
                            subscription.setLanguage(parser.getText());
                        }else if(parser.getName().equals("reconCtx")){
                            subscription.setReconCtx(parser.getText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("subscription")) { // 判断结束标签元素是否是book
                            subscriptionList.add(subscription); // 将book添加到books集合
                            subscription = null;
                        }
                        break;
                }
                event = parser.next();
            }
            List<Subscription> localSubscriptionList = subscriptionDataBaseService.readSubscriptions();
            for(Subscription localSubscription:localSubscriptionList){
                boolean isRetain = false;
                //Log.e(TAG, "getSubscriptionsByUserId....localSubscription id..." + localSubscription.getSubscriptionId());
                for(Subscription tmpSubscription:subscriptionList){
                    //Log.e(TAG, "getSubscriptionsByUserId....tmpSubscription id..." + tmpSubscription.getSubscriptionId());
                    //Log.e(TAG, "getSubscriptionsByUserId....tmpSubscription Status..." + tmpSubscription.getSubscriptionStatus());
                    if(localSubscription.getSubscriptionId().equals(tmpSubscription.getSubscriptionId())
                            && ("ACTIVE".equals(tmpSubscription.getSubscriptionStatus()) ||"MONITORING".equals(tmpSubscription.getSubscriptionStatus()))){
                        isRetain = true;
                    }
                }
                if(!isRetain){
                    //Log.e(TAG, "getSubscriptionsByUserId....");
                    subscriptionDataBaseService.deleteSubscription(localSubscription.getSubscriptionId());
                }
            }

        }catch (Exception e){
            throw new Exception();
        }

        if(responseCode.equals("OK")){
            return true;
        }else{
            return false;
        }
    }

    public List<SubscriptionResponse> createMultipleSubScription(List<CreateSubscriptionParameter> subscriptionParameterList) throws RequestFail {
        StringBuffer deleteDataStr = new StringBuffer("<SubscriptionRequestList xmlns=\"hafas_abo_v1\">\n");
        for (CreateSubscriptionParameter subscriptionParameter : subscriptionParameterList) {
            deleteDataStr.append("    <createSubscriptionRequest id=\""+subscriptionParameter.getId()+"\" userId=\"" + subscriptionParameter.getUserId() + "\">\n" +
                    "        <serviceSubscription>\n" +
                    "            <reconCtx>" + subscriptionParameter.getReconCtx() + "</reconCtx>\n" +
                    "            <serviceDays>\n" +
                    "                <beginDate>" + subscriptionParameter.getStartDate() + "</beginDate>\n" +
                    "                <endDate>" + subscriptionParameter.getEndDate() + "</endDate>\n" +
                    "                <selected>" + subscriptionParameter.getSelected() + "</selected>\n" +
                    "            </serviceDays>\n" +
                    "            <hysteresis>\n" +
                    "                <minDeviationInterval>" + subscriptionParameter.getMinDeviationInterval() + "</minDeviationInterval>\n" +
                    "                <minDeviationFollowing>" + subscriptionParameter.getMinDeviationFollowing() + "</minDeviationFollowing>\n" +
                    "                <notificationStart>" + subscriptionParameter.getNotificationStart() + "</notificationStart>\n" +
                    "            </hysteresis>\n" +
                    "        </serviceSubscription>\n" +
                    "        <channels>\n" +
                    "            <channel>\n" +
                    "                <type>ANDROID</type>\n" +
                    "                <address>" + subscriptionParameter.getAddress() + "</address>\n" +
                    "            </channel>\n" +
                    "        </channels>\n" +
                    "    </createSubscriptionRequest>\n");
        }
        deleteDataStr.append("</SubscriptionRequestList>");
        //System.out.println(deleteDataStr.toString());
        String responseData = httpRestServiceCaller.executePushHttpRequest(applicationContext, NMBSApplication.getInstance().getHafasUrl(), deleteDataStr.toString());
        Log.i(TAG, responseData);
        XmlPullParser parser = Xml.newPullParser();
        List<SubscriptionResponse> subscriptionResponseList = new ArrayList<SubscriptionResponse>();
        try {
            parser.setInput(new StringReader(responseData));
            int event = parser.getEventType();
            SubscriptionResponse subscriptionResponse = null;
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("createSubscriptionResponse")) {
                            subscriptionResponse = new SubscriptionResponse();
                            subscriptionResponse.setId(parser.getAttributeValue(null, "id"));
                        } else {
                            if(subscriptionResponse != null){
                                if (name.equalsIgnoreCase("subscriptionId")) {
                                    subscriptionResponse.setSubscriptionId(parser.nextText());// 如果后面是Text元素,即返回它的值
                                } else if (name.equalsIgnoreCase("resultCode")) {
                                    if(parser.nextText().equals("OK")){
                                        subscriptionResponse.setSuccess(true);
                                    }else{
                                        subscriptionResponse.setSuccess(false);
                                    }

                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("createSubscriptionResponse")&& subscriptionResponse != null) {
                            subscriptionResponseList.add(subscriptionResponse);
                            subscriptionResponse = null;
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    return subscriptionResponseList;
    }
    public Subscription getSubscriptionById(String id){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.readSubscriptionById(id);
    }
    public boolean clearSubscriptionDnrById(String id){
        SubscriptionDataBaseService subscriptionDataBaseService = new SubscriptionDataBaseService(applicationContext);
        return subscriptionDataBaseService.clearSubscriptionDnrById(id);
    }

    public String retryCreateUser(String language) throws RequestFail{
        String userId = "";
        /*String notificationTime = SettingsPref.getStartNotifiTime(applicationContext);
        if(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
            notificationTime = notificationTime.replace("min.","").trim();
        }else{
            notificationTime = "5";
        }

        String minDelay = SettingsPref.getDelayNotifiTime(applicationContext);
        if(!"".equals(notificationTime)&&notificationTime.indexOf("min")>0){
            minDelay = notificationTime.replace("min.","").trim();
        }else{
            minDelay = "5";
        }*/
        int start = NMBSApplication.getInstance().getSettingService().getStartNotifiTimeIntger();
        int delay = NMBSApplication.getInstance().getSettingService().getDelayNotifiTimeIntger();
        try {
            /*InstanceID instanceID = InstanceID.getInstance(applicationContext);
            String token = instanceID.getToken(applicationContext.getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);*/
            String token = FirebaseInstanceId.getInstance().getToken();
            LogUtils.i("Push", "GCM Registration Token: " + token);
            saveRegistrationId(token);
            userId = createAccount(new HafasUser("", token, language, delay, delay, start));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*if(NMBSApplication.getInstance().getTestService().isEmptyUser()){
            return "";
        }*/
        return userId;
    }

    public void createLocalNotification(long time, int id){
        Log.e("LocalNotification", "createLocalNotification...");
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(this.applicationContext, LocalNotificationWakefulBroadcastReceiver.class);
        //notificationIntent.setData(Uri.parse("content://calendar/calendar_alerts/1"));

        notificationIntent.addCategory("android.intent.category.DEFAULT");

 /*       Intent intent = new Intent(this.applicationContext, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this.applicationContext, id, intent, 0);*/
        PendingIntent broadcast = PendingIntent.getBroadcast(this.applicationContext, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, broadcast);
            /*AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), alarmIntentRTC);
            alarmManagerRTC.setAlarmClock(info, alarmIntentRTC);*/

        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, broadcast);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, broadcast);
        }

    }

    private void cancelLocalNotification(int id){
        Log.e("cancel", "id..." + id);
        Intent intent = new Intent(this.applicationContext, AlarmReceiver.class);
        //intent.setData(Uri.parse("content://calendar/calendar_alerts/1"));
        PendingIntent sender = PendingIntent.getBroadcast(this.applicationContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) this.applicationContext.getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    public void addAllLocalNotification(List<DossierTravelSegment> dossierTravelSegments){
        Log.e("LocalNotification", "addAllLocalNotification...");
        TravelSegmentDatabaseService travelSegmentDatabaseService = new TravelSegmentDatabaseService(applicationContext);
        SettingService settingService = NMBSApplication.getInstance().getSettingService();
        if(settingService.isTravelReminders()){
            Log.e("LocalNotification", "isTravelReminders...");
            for(int i=0;i<dossierTravelSegments.size();i++){
                if(dossierTravelSegments.get(i).getDepartureDate() != null){
                    Log.e("LocalNotification", "DepartureDate() != null...");
                    String DateStr = DateUtils.dateTimeToString(dossierTravelSegments.get(i).getDepartureDateTime());
                    Log.e("LocalNotification", "DateStr..." + DateStr);
                    LocalNotification localNotification = travelSegmentDatabaseService.queryTravelSegmentByDate(applicationContext, DateStr);
                    Date currentDate=new Date();//取时间

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(currentDate);
                    /*calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);*/
                    currentDate=calendar.getTime();

                    Log.e("LocalNotification", "getDepartureDate().getTime(): " + dossierTravelSegments.get(i).getDepartureDate());
                    Log.e("LocalNotification", "currentDate().getTime(): " + currentDate.getTime());
                    if(dossierTravelSegments.get(i).getDepartureDateTime().getTime() > currentDate.getTime()
                            && currentDate.getTime() <= getPushTime(dossierTravelSegments.get(i).getDepartureDate())){
                        Log.e("LocalNotification", "getDepartureDate().getTime()>currentDate.getTime()...");
                        //java.util.Random r = new java.util.Random();
                        int id = getPushId(dossierTravelSegments.get(i).getDepartureDate());
                        Log.e("LocalNotification", "id..." + id);
                        if(localNotification ==  null){
                            travelSegmentDatabaseService.insertTravelSegment(dossierTravelSegments.get(i), id, false);
                            createLocalNotification(getPushTime(dossierTravelSegments.get(i).getDepartureDate()), id);
                        }else{
                            if(localNotification.isCancel()){
                                travelSegmentDatabaseService.updateTravelSegmentByDate(applicationContext,DateStr,false);
                                createLocalNotification(getPushTime(dossierTravelSegments.get(i).getDepartureDate()), id);
                            }
                        }
                    }else{
                        if(localNotification !=  null){
                            travelSegmentDatabaseService.deleteTravelSegment(dossierTravelSegments.get(i));
                        }
                    }
                }

            }
        }else{
            for(int i=0;i<dossierTravelSegments.size();i++){
                String DateStr = DateUtils.dateTimeToString(dossierTravelSegments.get(i).getDepartureDateTime());
                LocalNotification localNotification = travelSegmentDatabaseService.queryTravelSegmentByDate(applicationContext,DateStr);
                if(localNotification == null){
                    java.util.Random r = new java.util.Random();
                    int id = r.nextInt();
                    travelSegmentDatabaseService.insertTravelSegment(dossierTravelSegments.get(i), id, true);
                }
            }
        }
    }
    public int getPushId(Date date){
        int id = 0;
        if(date != null){
            id = date.getYear() + date.getMonth() + date.getDay();
        }
        return id;
    }
    public long getPushTime(Date date){

        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY,16);
        cal.set(Calendar.MINUTE,0);
        Log.e("LocalNotification", "getPushTime..." + cal.getTime());
        return cal.getTimeInMillis();
    }

    public void addAllLocalNotificationFromDatabase(){
       /* TravelSegmentDatabaseService travelSegmentDatabaseService = new TravelSegmentDatabaseService(applicationContext);
        List<LocalNotification> localNotifications = travelSegmentDatabaseService.getAllTravelSegment(applicationContext);
        String currentDate = DateUtils.dateToString(new Date());
        for(LocalNotification localNotification:localNotifications){
            String tempDateStr = DateUtils.dateToString(localNotification.getDepartureDate());
            if(currentDate.equals(tempDateStr) || new Date().getTime() > localNotification.getDepartureDate().getTime()){
                travelSegmentDatabaseService.deleteTravelSegment(DateUtils.dateToString(localNotification.getDepartureDate()));
            }else if(localNotification.isCancel()){
                travelSegmentDatabaseService.updateTravelSegmentByDate(applicationContext,tempDateStr,false);
                createLocalNotification(getPushTime(localNotification.getDepartureDate()),localNotification.getNotificationId());
            }
        }*/
    }

    public void deleteAllLocalNotification(){
        /*TravelSegmentDatabaseService travelSegmentDatabaseService = new TravelSegmentDatabaseService(applicationContext);
        List<LocalNotification> localNotifications = travelSegmentDatabaseService.getAllTravelSegment(applicationContext);
        String currentDate = DateUtils.dateToString(new Date());
        for(LocalNotification localNotification:localNotifications){
            String tempDateStr = DateUtils.dateToString(localNotification.getDepartureDate());
            if(currentDate.equals(tempDateStr)||new Date().getTime()>localNotification.getDepartureDate().getTime()){
                travelSegmentDatabaseService.deleteTravelSegment(DateUtils.dateToString(localNotification.getDepartureDate()));
            }else if(!localNotification.isCancel()){
                travelSegmentDatabaseService.updateTravelSegmentByDate(applicationContext,tempDateStr,true);
                cancelLocalNotification(localNotification.getNotificationId());
            }
        }*/
    }

    public void deleteLocalNotificationFromDatabase(DossierTravelSegment dossierTravelSegment){

        TravelSegmentDatabaseService travelSegmentDatabaseService = new TravelSegmentDatabaseService(applicationContext);
        travelSegmentDatabaseService.deleteTravelSegment(dossierTravelSegment);
    }

}