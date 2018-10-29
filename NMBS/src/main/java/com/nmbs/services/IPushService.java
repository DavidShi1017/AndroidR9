package com.nmbs.services;

import com.nmbs.exceptions.RequestFail;
import com.nmbs.model.CreateSubscriptionParameter;
import com.nmbs.model.DossierTravelSegment;
import com.nmbs.model.HafasUser;
import com.nmbs.model.Subscription;
import com.nmbs.model.SubscriptionResponse;
import com.nmbs.model.SubscriptionResponseResult;

import java.util.Date;
import java.util.List;

/**
 * Created by Richard on 4/1/16.
 */
public interface IPushService {
    public void saveRegistrationId(String registrationId);
    public String getRegistrationId();
    public HafasUser getUser();
    public String updateAccount(HafasUser hafasUser) throws RequestFail;
    public String createAccount(HafasUser hafasUser) throws RequestFail;
    public boolean saveHafasUserInfo(HafasUser hafasUser);
    public String createSubScription(CreateSubscriptionParameter subScription) throws RequestFail;
    public List<SubscriptionResponse> createMultipleSubScription(List<CreateSubscriptionParameter> subscriptionParameterList) throws RequestFail;
    public boolean saveSubscriptionInLocal(Subscription subscription);
    public boolean deleteSubscription(String userId,String subScriptionId) throws RequestFail;
    public SubscriptionResponseResult deleteAllSubscription(String userId, List<Subscription> subscriptions) throws RequestFail;
    public boolean deleteSubscriptionInLocal(String subscriptionId);
    public boolean getSubscriptionFromLocal(String reconctx);
    public boolean isSubscriptionExistLocal(String reconctx, Date date);
    public boolean isLinkedDnr(String dnr);
    public boolean isDossierPushEnabled(String dnr);
    public Subscription getSubscriptionFromLocal(String reconctx, Date date);
    public List<Subscription> readAllSubscriptions();
    public List<Subscription> readAllSubscriptionsByDnr(String dossierId);
    public boolean getSubscriptionsByUserId(String userId) throws Exception;
    public Subscription getSubscriptionById(String id);
    public boolean clearSubscriptionDnrById(String id);
    public String retryCreateUser(String language) throws RequestFail;
    public boolean isSubscriptionExistLocal(String originStationRcode, String oestinationStationRcode , Date departure);
    public Subscription readSubscriptionByConnection(String connectionId);
    public void addAllLocalNotification(List<DossierTravelSegment> dossierTravelSegments);
    public void addAllLocalNotificationFromDatabase();
    public void deleteAllLocalNotification();
    public int getPushId(Date date);
    public long getPushTime(Date date);
    public void createLocalNotification(long time, int id);
    public List<Subscription> readAllSubscriptionsNoDnr();
    public List<Subscription> readAllSubscriptionsWithDnr();
    public void deleteLocalNotificationFromDatabase(DossierTravelSegment dossierTravelSegment);
}
