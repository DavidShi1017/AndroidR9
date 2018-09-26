package com.nmbs.services.impl;

import android.content.Intent;

/**
 * IntentService and broadcast 
 *@author:Alice
 */
public class ServiceConstant {
	
	//IntentService sent intent.putExtra key.
	//PARAM_IN_MSG is call service sent key , PARAM_OUT_MSG receive response sent broadcast intent key.
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_IN_LANGUAGE = "Language";
	public static final String PARAM_IN_FLAG = "dossierOptionFlag";
	public static final String PARAM_OUT_MSG = "omsg";
	public static final String PARAM_OUT_ERROR = "error";
	public static final String PARAM_OUT_ERROR_MESSAGE = "error_message";
	//Static fields defining the INTENT parameters on which the receiver is listening on
	public static final String INTENT_ACTION_LOGIN_RESPONSE = "com.nmbs.intent.action.login.response";
	public static final String INTENT_ACTION_OFFER_RESPONSE = "com.nmbs.intent.action.offer.response";
	public static final String INTENT_ACTION_PERSON_RESPONSE = "com.nmbs.intent.action.person.response";
	public static final String INTENT_ACTION_REGISTER_RESPONSE = "com.nmbs.intent.action.register.response";
	public static final String INTENT_ACTION_NOTIFICATION_RESPONSE = "com.nmbs.intent.action.notification.response";
	public static final String INTENT_ACTION_MASTER_RESPONSE = "com.nmbs.intent.action.master.response";
	public static final String INTENT_ACTION_DOSSIER_RESPONSE = "com.nmbs.intent.action.dossier.response";
	public static final String INTENT_ACTION_DOSSIER_DELETE_RESPONSE = "com.nmbs.intent.action.dossier.delete.response";
	public static final String INTENT_ACTION_PAYMENT_RESPONSE = "com.nmbs.intent.action.payment.response";
	public static final String INTENT_ACTION_ABORT_PAYMENT_RESPONSE = "com.nmbs.intent.action.abort.payment.response";
	public static final String INTENT_ACTION_CITY_DETAIL_RESPONSE = "com.nmbs.intent.action.city_detail.response";
	public static final String INTENT_ACTION_DOSSIER_AFTERSALE_RESPONSE = "com.nmbs.intent.action.dossier.aftersale.response";
	public static final String INTENT_ACTION_DOSSIER_REFRESH_CONFIRMATION = "com.nmbs.intent.action.refresh.confirmation.response";
	public static final String INTENT_ACTION_DOSSIER_DETAIL_RESPONSE = "com.nmbs.intent.action.dossier.detail.response";
	
	public static final String INTENT_ACTION_MASTER_ERROR = "com.nmbs.intent.action.master.error";
	public static final String INTENT_ACTION_NOTIFICATION_ERROR = "com.nmbs.intent.action.notification.error";
	public static final String INTENT_ACTION_REGISTER_ERROR = "com.nmbs.intent.action.register.error";
	public static final String INTENT_ACTION_LOGIN_ERROR = "com.nmbs.intent.action.login.error";
	public static final String INTENT_ACTION_OFFER_ERROR = "com.nmbs.intent.action.offer.error";
	public static final String INTENT_ACTION_PERSON_ERROR = "com.nmbs.intent.action.person.error";
	public static final String INTENT_ACTION_DOSSIER_ERROR = "com.nmbs.intent.action.dossier.error";
	public static final String INTENT_ACTION_DOSSIER_DELETE_ERROR = "com.nmbs.intent.action.dossier.delete.error";
	public static final String INTENT_ACTION_ABORT_PAYMENT_ERROR = "com.nmbs.intent.action.abort.payment.error";
	public static final String INTENT_ACTION_DOSSIER_REFRESH_CONFIRMATION_ERROR = "com.nmbs.intent.action.refresh.confirmation.error";
	public static final String INTENT_ACTION_DOSSIER_AFTERSALE_ERROR = "com.nmbs.intent.action.dossier.aftersale.error";
	public static final String INTENT_ACTION_DOSSIER_REFRESH_PAYMENT_ERROR = "com.nmbs.intent.action.refresh.error";
	public static final String INTENT_ACTION_DOSSIER_DETAIL_ERROR = "com.nmbs.intent.action.dossier.detail.error";
	// Station board
	public static final String INTENT_ACTION_STATION_BOARD_RESPONSE = "com.nmbs.intent.action.station.board.response";
	public static final String INTENT_ACTION_STATION_BOARD_ERROR = "com.nmbs.intent.action.station.board.error";
	public static final String PARAM_IN_MSG_STATION_BOARD_RCODE = "com.nmbs.param.station.board.rcode";
	public static final String PARAM_IN_MSG_STATION_BOARD_TIME_PREFERENCE = "com.nmbs.param.station.board.time.preference";
	public static final String PARAM_IN_MSG_STATION_BOARD_DATE = "com.nmbs.param.station.board.date";

	//Schedule query
	public static final String INTENT_ACTION_SCHEDULE_QUERY_RESPONSE = "com.nmbs.intent.action.schedule.query.response";
	public static final String INTENT_ACTION_SCHEDULE_QUERY_ERROR = "com.nmbs.intent.action.schedule.query.error";
	public static final String PARAM_IN_MSG_SCHEDULE_QUERY_RCODE = "com.nmbs.param.schedule.query.rcode";
	public static final String PARAM_IN_MSG_SCHEDULE_QUERY_TIME_PREFERENCE = "com.nmbs.param.schedule.query.time.preference";
	public static final String PARAM_IN_MSG_SCHEDULE_QUERY_DATE = "com.nmbs.param.schedule.query.date";
	public static final String SCHEDULE_DETAIL_SERVICE_ACTION = "com.nmbs.intent.action.schedule.detail.service";
		
	// Station detail
	public static final String INTENT_ACTION_STATION_DETAIL_RESPONSE = "com.nmbs.intent.action.station.detail.response";
	public static final String INTENT_ACTION_STATION_DETAIL_ERROR = "com.nmbs.intent.action.station.detail.error";
	public static final String PARAM_IN_MSG_STATION_DETAIL_CODE = "com.nmbs.param.station.detail.code";

	
	// Corporate Card
	public static final String INTENT_ACTION_CORPORATE_CARD_RESPONSE = "com.nmbs.intent.action.corporate.card.response";
	public static final String INTENT_ACTION_CORPORATE_CARD_ERROR = "com.nmbs.intent.action.corporate.card.error";
	public static final String PARAM_IN_MSG_CORPORATE_CARD_GREENPONIT_NUMBER = "com.nmbs.param.corporate.card.greenpoint.number";
	
	public static final String INTENT_ACTION_CITY_DETAIL_ERROR = "com.nmbs.intent.action.city_detail.error";	
	public static final String INTENT_CATEGORY_RESPONSE = Intent.CATEGORY_DEFAULT;
	
	
	//Click to call
	public static final String INTENT_ACTION_CLICK_TO_CALL_RESPONSE = "com.nmbs.intent.action.click.to.call.response";
	public static final String INTENT_ACTION_CLICK_TO_CALL_ERROR = "com.nmbs.intent.action.click.to.call.error";
	
	
	// Station detail
	public static final String INTENT_ACTION_DOSSIER_PROMO_CODE_RESPONSE = "com.nmbs.intent.action.dossier.promo.code.response";
	public static final String INTENT_ACTION_DOSSIER_PROMO_CODE_ERROR = "com.nmbs.intent.action.dossier.promo.code.error";
	public static final String PARAM_IN_MSG_DOSSIER_PROMO_CODE = "com.nmbs.param.dossier.promo.code";
	
	// handler parameters.
	public static final int MESSAGE_WHAT_OK = 1;
	public static final int MESSAGE_WHAT_ERROR = 2;
	public static final int MESSAGE_WHAT_PAYMENT_OK = 3;
	public static final int MESSAGE_WHAT_DELETE_OK = 4;
	
	public static final int MESSAGE_WHAT_DELETE_ERROR = 5;
	public static final int MESSAGE_WHAT_ABORT_PAYMENT_OK = 6;
	public static final int MESSAGE_WHAT_ABORT_PAYMENT_ERROR = 7;
	public static final int MESSAGE_WHAT_REFRESH_CONFIRMATION_OK = 8;
	public static final int MESSAGE_REFRESH_CONFIRMATION_ERROR = 9;
	public static final int MESSAGE_REFRESH_PAYMENT_ERROR = 10;
	//LoginService
	public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
	public static final String LOGIN_STATUS = "LOGIN_STATUS";
	
	//OfferService
	public static final String SEARCH_FINISHED = "SEARCH_FINISHED";//search finish
	
	public static final String HAS_CONNECTION = "HasConnection";
	public static final String IS_MASTDATA_WORKING = "isMastDataWorking";
	
    //message
	public static final String INTENT_ACTION_MESSAGE_ERROR = "com.nmbs.intent.action.message.error";
	public static final String INTENT_ACTION_MESSAGE_RESPONSE = "com.nmbs.intent.action.message.response";
	
	public static final String UPDATE_VERSION_ACTION = "com.nmbs.intent.action.update.version";
	public static final String CREATE_VERSION_ACTION = "com.nmbs.intent.action.create.version";
	public static final String CLICK_TO_CALL_SERVICE_ACTION = "com.nmbs.intent.action.click_to_cal.service";
	public static final String MESSAGE_SERVICE_ACTION = "com.nmbs.intent.action.message.service";
	public static final String STATION_INFO_SERVICE_ACTION = "com.nmbs.intent.action.stationInfo.service";
	public static final String MASTERDATA_SERVICE_ACTION = "com.nmbs.intent.action.masterdata.service";
	public static final String REALTIME_SERVICE_ACTION = "com.nmbs.intent.action.realtime.service";
	public static final String DOSSIER_SERVICE_ACTION = "com.nmbs.intent.action.dossier.service";

	//Push Notification
	public static final String PUSH_CREATE_SUBSCRIPTION_ACTION = "com.nmbs.intent.action.push.create.subscription.service";
	public static final String PUSH_DELETE_SUBSCRIPTION_ACTION = "com.nmbs.intent.action.push.delete.subscription.service";
	public static final String PUSH_GET_SUBSCRIPTION_LIST_ACTION = "com.nmbs.intent.action.push.get.subscription.service";

	//Alert Activity
	public static final String PUSH_ALERT_VIEW_GET_SUBSCRIPTION_LIST_ACTION = "com.nmbs.intent.action.push.alert.view.get.subscription.service";

}

