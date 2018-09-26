package com.nmbs.util;




import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nmbs.model.Account;
import com.nmbs.model.AdditionalConnectionsParameter;
import com.nmbs.model.AdditionalScheduleQueryParameter;
import com.nmbs.model.AllLastOfferQuery;
import com.nmbs.model.ClickToCallAftersalesParameter;
import com.nmbs.model.ClickToCallContext;
import com.nmbs.model.ClickToCallParameter;
import com.nmbs.model.CustomerParameter;
import com.nmbs.model.DeliveryMethod;
import com.nmbs.model.DeliveryMethodParameter;
import com.nmbs.model.DossierParameter;
import com.nmbs.model.DossiersUpToDateParmeters;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.PartyMember;
import com.nmbs.model.Promo;
import com.nmbs.model.PromoParameter;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.model.ScheduleDetailRefreshModel;
import com.nmbs.model.ScheduleQuery;
import com.nmbs.model.StationBoardCollection;
import com.nmbs.model.StationBoardLastQuery;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.TrainSelectionParameter;
import com.nmbs.model.TravelRequest;
import com.nmbs.serializers.AccountJsonSerializer;
import com.nmbs.serializers.AdditionalConnectionsParameterSerializer;
import com.nmbs.serializers.AllLastOfferQueryDeserializer;
import com.nmbs.serializers.ClickToCallContextAftersalesSerializer;
import com.nmbs.serializers.ClickToCallContextSerializer;
import com.nmbs.serializers.OfferQueryJsonSerializer;
import com.nmbs.serializers.PartyMemberJsonSerializer;
import com.nmbs.serializers.PromoCodeJsonSerializer;
import com.nmbs.serializers.TrainSelectionParameterOfDossierParameterSerializer;
import com.nmbs.serializers.TrainSelectionParameterSerializer;
import com.nmbs.serializers.TravelRequestJsonSerializer;
import com.nmbs.serializers.UpdateCustomerAndPaymentMethodOfDossierParameterSerializer;
import com.nmbs.serializers.UpdateCustomerSerializer;
import com.nmbs.serializers.UpdateIsuranceAndDeliveryMethodMailWaySerializer;
import com.nmbs.serializers.UpdateIsuranceAndDeliveryMethodOfDossierParameterSerializer;
import com.nmbs.serializers.UpdateIsuranceAndDeliveryMethodPrintWaySerializer;
import com.nmbs.serializers.UpdateIsuranceAndDeliveryMethodStationWaySerializer;
import com.nmbs.serializers.UpdateIsuranceAndDeliveryMethodTicketWaySerializer;
import com.nmbs.services.impl.DossierService;

/**
 * It is a utility classes for Convert a JSON object to JSON String.
 *
 */
public class ObjectToJsonUtils {

	//private static final String TAG = ObjectToJsonUtils.class.getSimpleName();
	/**
	 * Convert account to Json String.
	 * @param account
	 * @return String
	 */
	public static String getPostAccountStr(Account account){		
	     String datePattern = "dd/MM/yyyy";
	     GsonBuilder builder = new GsonBuilder();   
	     builder.setDateFormat(datePattern);   
	     builder.serializeNulls();
	     builder.registerTypeAdapter(Account.class, new AccountJsonSerializer());
	     Gson gson = builder.create();  
	     String postAccount = gson.toJson(account);	     
	     //Log.d(TAG, postAccount);	     
	  return postAccount;		
	}
	
	/**
	 * Convert offerQuery to Json String.
	 * @param offerQuery
	 * @return String
	 */ 
	public static String getPostOfferQueryStr(OfferQuery offerQuery, AllLastOfferQuery allLastOfferQuery, boolean isSavingLastOfferQuery){		

	     String datePattern = "yyyy-MM-dd HH:mm:ss";
	     GsonBuilder builder = new GsonBuilder();   
	     builder.setDateFormat(datePattern);   
	     builder.serializeNulls();
	     builder.excludeFieldsWithoutExposeAnnotation();
	     if (isSavingLastOfferQuery) {
	    	 builder.registerTypeAdapter(AllLastOfferQuery.class, new AllLastOfferQueryDeserializer());
	     }
	     
	     builder.registerTypeAdapter(OfferQuery.class, new OfferQueryJsonSerializer(isSavingLastOfferQuery));
	     
	     builder.registerTypeAdapter(PartyMember.class,new PartyMemberJsonSerializer());
	     builder.registerTypeAdapter(TravelRequest.class,new TravelRequestJsonSerializer());
	     
	      
	     /*if(offerQuery.getReturnQueryParameters().getDateTime() == null){
	    	 
	    	 offerQuery.getReturnQueryParameters().setDateTime(DateUtils.getNextDay(offerQuery.getDepartureQueryParameters().getDateTime()));
	     }*/
	    /* if(offerQuery.getReturnQueryParameters().getTimePreference() == null){
	    	 offerQuery.getReturnQueryParameters().setTimePreference(TimePreference.DEPARTURE);
	     }*/
	   
	     
	     Gson gson = builder.create(); 
	     String postQuery = "";
	     if (isSavingLastOfferQuery) {
	    	 postQuery = gson.toJson(allLastOfferQuery);	     
	     }else {
	    	 postQuery = gson.toJson(offerQuery);	     
	     }
	     
	     
	     //postQuery = postQuery.replace("null","[]");
	     //Log.d(TAG, postQuery);	
		
	  return postQuery;		
	}
	
	/**
	 * Convert AdditionalConnectionsParameter to Json String.
	 * @return String
	 */
	public static String getPostAdditionalConnectionsParameterStr(AdditionalConnectionsParameter additionalConnectionsParameter){		
	     GsonBuilder builder = new GsonBuilder();   
	     builder.registerTypeAdapter(AdditionalConnectionsParameter.class, new AdditionalConnectionsParameterSerializer());
	     Gson gson = builder.create();  
	     String postAdditionalConnectionsParameter = gson.toJson(additionalConnectionsParameter);	     
	     //Log.d(TAG, postAdditionalConnectionsParameter);	     
	  return postAdditionalConnectionsParameter;		
	}

	/**
	 * Convert AdditionalConnectionsParameter to Json String.
	 * @return String
	 */
	public static String getPostAdditionalScheduleQueryParameterStr(AdditionalScheduleQueryParameter additionalScheduleQueryParameter){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String postAdditionalScheduleQueryParameter = gson.toJson(additionalScheduleQueryParameter);
		return postAdditionalScheduleQueryParameter;
	}
	
	/**
	 * Convert TrainSelectionParameter to Json String.
	 * @return String
	 */
	public static String getPostTrainSelectionParameterStr(DossierParameter dossierParameter){
		String postTrainSelectionParameterStr = null;
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(DossierParameter.class, new TrainSelectionParameterOfDossierParameterSerializer());
		builder.registerTypeAdapter(TrainSelectionParameter.class, new TrainSelectionParameterSerializer());
		Gson gson = builder.create();
		postTrainSelectionParameterStr = gson.toJson(dossierParameter);
		//Log.i(TAG, postTrainSelectionParameterStr);	
		return postTrainSelectionParameterStr;
	}
	
	public static String getPostUpdateInsuranceAndDeliveryMethodParameterStr(DossierParameter dossierParameter){
		String postTrainSelectionParameterStr = null;
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(DossierParameter.class, new UpdateIsuranceAndDeliveryMethodOfDossierParameterSerializer());
		if(DeliveryMethod.DELIVERYMETHOD_STATION.equals(DossierService.selectedDeliveryMethod)){
			builder.registerTypeAdapter(DeliveryMethodParameter.class, new UpdateIsuranceAndDeliveryMethodStationWaySerializer());
		}else if(DeliveryMethod.DELIVERYMETHOD_PRINT.equals(DossierService.selectedDeliveryMethod)){
			
			builder.registerTypeAdapter(DeliveryMethodParameter.class, new UpdateIsuranceAndDeliveryMethodPrintWaySerializer());					
		}else if(DeliveryMethod.DELIVERYMETHOD_MAIL.equals(DossierService.selectedDeliveryMethod)){
			builder.registerTypeAdapter(DeliveryMethodParameter.class, new UpdateIsuranceAndDeliveryMethodMailWaySerializer());								
		}else {
			builder.registerTypeAdapter(DeliveryMethodParameter.class, new UpdateIsuranceAndDeliveryMethodTicketWaySerializer());								
		}
		
		Gson gson = builder.create();
		postTrainSelectionParameterStr = gson.toJson(dossierParameter);
		//Log.i(TAG, "UpdateInsuranceAndDeliveryMethod JSON String: " + postTrainSelectionParameterStr);	
		return postTrainSelectionParameterStr;
	}
	
	public static String getPostUpdateCustomerAndPaymentMethodParameterStr(DossierParameter dossierParameter){
		String postTrainSelectionParameterStr = null;
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(DossierParameter.class, new UpdateCustomerAndPaymentMethodOfDossierParameterSerializer());
		builder.registerTypeAdapter(CustomerParameter.class, new UpdateCustomerSerializer());
		Gson gson = builder.create();
		postTrainSelectionParameterStr = gson.toJson(dossierParameter);
		//Log.i(TAG, "getPostUpdateCustomerAndPaymentMethodParameterStr JSON String: " + postTrainSelectionParameterStr);	
		return postTrainSelectionParameterStr;
	}
	
	/**
	 * Convert ClickToCallParameter to Json String.
	 * @param clickToCallParameter
	 * @return String
	 */
	public static String getPostClickToCallParameterStr(ClickToCallParameter clickToCallParameter){		
	     String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
	     GsonBuilder builder = new GsonBuilder();   
	     builder.setDateFormat(datePattern);   
	     builder.serializeNulls();
	    //builder.registerTypeAdapter(ClickToCallParameter.class, new OfferQueryJsonSerializer());
	     builder.registerTypeAdapter(ClickToCallContext.class,new ClickToCallContextSerializer());
	     builder.registerTypeAdapter(TravelRequest.class,new TravelRequestJsonSerializer());
	     builder.registerTypeAdapter(PartyMember.class,new PartyMemberJsonSerializer());
	     //builder.registerTypeAdapter(ReductionCard.class,new ReductionCardtJsonSerializer());
	     Gson gson = builder.create();  

	     String postClickToCallParameter = gson.toJson(clickToCallParameter);	     
	     postClickToCallParameter = postClickToCallParameter.replace("null", "[]");
	     //Log.d(TAG, postClickToCallParameter);	
		
	  return postClickToCallParameter;		
	}

	public static String getPostClickToCallAftersalesParameterStr(ClickToCallAftersalesParameter clickToCallAftersalesParameter){
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		builder.registerTypeAdapter(ClickToCallAftersalesParameter.class, new ClickToCallContextAftersalesSerializer());
		//builder.registerTypeAdapter(ClickToCallContext.class,new ClickToCallContextSerializer());
		//builder.registerTypeAdapter(TravelRequest.class,new TravelRequestJsonSerializer());
		//builder.registerTypeAdapter(PartyMember.class,new PartyMemberJsonSerializer());
		//builder.registerTypeAdapter(ReductionCard.class,new ReductionCardtJsonSerializer());
		Gson gson = builder.create();

		String postClickToCallParameter = gson.toJson(clickToCallAftersalesParameter);
		//postClickToCallParameter = postClickToCallParameter.replace("null", "[]");
		Log.d("AftersalesParameter", "ClickToCallAftersalesParameter...." + postClickToCallParameter);

		return postClickToCallParameter;
	}
	
	public static String getPostPromoCodeJson(PromoParameter promoParameter){
		 GsonBuilder builder = new GsonBuilder();   
	      
	     
	     builder.registerTypeAdapter(Promo.class, new PromoCodeJsonSerializer());
	     Gson gson = builder.create();  
	     String postJson = gson.toJson(promoParameter);	     
	     return postJson;
	}
	
	public static String getStationBoardQueryStr(StationBoardQuery stationBoardQuery){
		
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		Gson gson = builder.create();

		String postStationBoardQuery = gson.toJson(stationBoardQuery);

		return postStationBoardQuery;
	}

	public static String getScheduleQueryStr(ScheduleQuery scheduleQuery){

		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		Gson gson = builder.create();
		String postScheduleQuery = gson.toJson(scheduleQuery);
		return postScheduleQuery;
	}

	public static String getScheduleDetailRefreshStr(ScheduleDetailRefreshModel scheduleDetailRefreshModel){

		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		Gson gson = builder.create();
		String postScheduleDetailRefreshStr = gson.toJson(scheduleDetailRefreshModel);
		return postScheduleDetailRefreshStr;
	}

	public static String getStationBoardLastQueryStr(StationBoardLastQuery stationBoardQuery){

		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		Gson gson = builder.create();

		String postStationBoardQuery = gson.toJson(stationBoardQuery);

		return postStationBoardQuery;
	}

	public static String getLastStationBoardQueryStr(StationBoardQuery stationBoardQuery){

		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		Gson gson = builder.create();

		String postStationBoardQuery = gson.toJson(stationBoardQuery);

		return postStationBoardQuery;
	}
	
	public static String getStationBoardStr(StationBoardCollection stationBoardCollection){
		
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		//builder.serializeNulls();
		builder.excludeFieldsWithoutExposeAnnotation();
		
		Gson gson = builder.create();

		String postStationBoardQuery = gson.toJson(stationBoardCollection);
		postStationBoardQuery = postStationBoardQuery.replace("null","");
		return postStationBoardQuery;
	}

	public static String getPostRealTimeInfoQueryStr(RealTimeInfoRequestParameter realTimeInfoRequestParameter){
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		Gson gson = builder.create();
		String postQuery = "";
		postQuery = gson.toJson(realTimeInfoRequestParameter);
		System.out.println("++++++++++++" + postQuery);
		return postQuery;
	}

	public static String getDossiersUpToDateParmeters(DossiersUpToDateParmeters dossiersUpToDateParmeters){

		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(datePattern);
		builder.serializeNulls();
		Gson gson = builder.create();

		String josnDossiersUpToDateParmeters = gson.toJson(dossiersUpToDateParmeters);

		return josnDossiersUpToDateParmeters;
	}
}
