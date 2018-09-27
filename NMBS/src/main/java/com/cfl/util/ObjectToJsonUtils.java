package com.cfl.util;




import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cfl.model.Account;
import com.cfl.model.AdditionalConnectionsParameter;
import com.cfl.model.AdditionalScheduleQueryParameter;
import com.cfl.model.AllLastOfferQuery;
import com.cfl.model.ClickToCallAftersalesParameter;
import com.cfl.model.ClickToCallContext;
import com.cfl.model.ClickToCallParameter;
import com.cfl.model.CustomerParameter;
import com.cfl.model.DeliveryMethod;
import com.cfl.model.DeliveryMethodParameter;
import com.cfl.model.DossierParameter;
import com.cfl.model.DossiersUpToDateParmeters;
import com.cfl.model.OfferQuery;
import com.cfl.model.PartyMember;
import com.cfl.model.Promo;
import com.cfl.model.PromoParameter;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.ScheduleDetailRefreshModel;
import com.cfl.model.ScheduleQuery;
import com.cfl.model.StationBoardCollection;
import com.cfl.model.StationBoardLastQuery;
import com.cfl.model.StationBoardQuery;
import com.cfl.model.TrainSelectionParameter;
import com.cfl.model.TravelRequest;
import com.cfl.serializers.AccountJsonSerializer;
import com.cfl.serializers.AdditionalConnectionsParameterSerializer;
import com.cfl.serializers.AllLastOfferQueryDeserializer;
import com.cfl.serializers.ClickToCallContextAftersalesSerializer;
import com.cfl.serializers.ClickToCallContextSerializer;
import com.cfl.serializers.OfferQueryJsonSerializer;
import com.cfl.serializers.PartyMemberJsonSerializer;
import com.cfl.serializers.PromoCodeJsonSerializer;
import com.cfl.serializers.TrainSelectionParameterOfDossierParameterSerializer;
import com.cfl.serializers.TrainSelectionParameterSerializer;
import com.cfl.serializers.TravelRequestJsonSerializer;
import com.cfl.serializers.UpdateCustomerAndPaymentMethodOfDossierParameterSerializer;
import com.cfl.serializers.UpdateCustomerSerializer;
import com.cfl.serializers.UpdateIsuranceAndDeliveryMethodMailWaySerializer;
import com.cfl.serializers.UpdateIsuranceAndDeliveryMethodOfDossierParameterSerializer;
import com.cfl.serializers.UpdateIsuranceAndDeliveryMethodPrintWaySerializer;
import com.cfl.serializers.UpdateIsuranceAndDeliveryMethodStationWaySerializer;
import com.cfl.serializers.UpdateIsuranceAndDeliveryMethodTicketWaySerializer;
import com.cfl.services.impl.DossierService;

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
