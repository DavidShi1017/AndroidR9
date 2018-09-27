package com.cfl.services.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfl.R;
import com.cfl.model.ReductionCard;


import com.cfl.dataaccess.converters.OfferQueryConverter;
import com.cfl.dataaccess.database.OfferQueryDataBaseService;

import com.cfl.model.AdditionalConnectionsParameter;
import com.cfl.model.CombinationMatrixRow;
import com.cfl.model.Connection;
import com.cfl.model.Currency;
import com.cfl.model.Leg;
import com.cfl.model.Price;

import com.cfl.model.AllLastOfferQuery;
import com.cfl.model.Offer;
import com.cfl.model.OfferItem;
import com.cfl.model.OfferQuery;
import com.cfl.model.OfferResponse;
import com.cfl.model.PartyMember;
import com.cfl.model.OfferQuery.ComforClass;
import com.cfl.model.OfferQuery.TravelType;
import com.cfl.model.OfferQuery.OfferRequestFeedbackTypes;
import com.cfl.model.PartyMember.PersonType;

import com.cfl.model.TariffPassenger;
import com.cfl.model.TravelRequest.TimePreference;
import com.cfl.services.IMasterService;
import com.cfl.services.IOfferService;
import com.cfl.services.ISettingService;
import com.cfl.util.DateUtils;
import com.cfl.util.FileManager;

import com.cfl.util.ObjectToJsonUtils;

import com.cfl.util.Utils;


import android.content.Context;
import android.content.SharedPreferences;




/**
 * Control Offer Data and View communicate.
 */
public class OfferService implements IOfferService {
	
	//private final static String TAG = OfferService.class.getSimpleName();
	private Context applicationContext;
	private OfferQuery offerQuery;

	public final static String FTP_CARD = "ftp_card"; 
	public final static String FTP_CARD_EUROSTAR = "Eurostar"; 
	public final static String FTP_CARD_THALYS = "Thalys"; 
	public static boolean hasLastOfferQuery;
	private boolean addOptionalReservationsForDeparture;
	private boolean addOptionalReservationsForReturn;
	private OfferResponse offerResponse;
	public static String GUID;
	private static final String EUR = "EUR";
	public final static String LAST_BOOKING_STARTED_TIME = "last_booking_started_time";  
	private AllLastOfferQuery allLastOfferQuery;
	
	
	
	public AllLastOfferQuery getAllLastOfferQuery() {
		
		if (this.allLastOfferQuery == null) {
			this.allLastOfferQuery = new AllLastOfferQuery();
		}
		return allLastOfferQuery;
	}

	public void setAllLastOfferQuery(AllLastOfferQuery allLastOfferQuery) {
		this.allLastOfferQuery = allLastOfferQuery;
	}

	public boolean isAddOptionalReservationsForDeparture() {
		return addOptionalReservationsForDeparture;
	}

	public void setAddOptionalReservationsForDeparture(boolean addOptionalReservationsForDeparture) {
		this.addOptionalReservationsForDeparture = addOptionalReservationsForDeparture;
	}

	public boolean isAddOptionalReservationsForReturn() {
		return addOptionalReservationsForReturn;
	}

	public void setAddOptionalReservationsForReturn(
			boolean addOptionalReservationsForReturn) {
		this.addOptionalReservationsForReturn = addOptionalReservationsForReturn;
	}

	public OfferService(Context context) {
		this.applicationContext = context;
	}

	public OfferResponse getOfferResponse() {
		return offerResponse;
	}

	public void setOfferResponse(OfferResponse offerResponse) {
		this.offerResponse = offerResponse;
	}


	public void clearMyState(){
		//myState = null;
	}

	/**
	 * Get OfferQuery from OfferService.
	 * 
	 * @param None
	 * @return OfferQuery
	 */
	public OfferQuery getOfferQuery() {
		
		
		if (this.offerQuery == null) {
			this.offerQuery = new OfferQuery();
		}
		return this.offerQuery;
	}

	
	
	public void setOfferQuery(OfferQuery offerQuery) {
		this.offerQuery = offerQuery;
	}

	/**
	 * Clear OfferQuery when the application is destroyed.
	 * 
	 * @param None
	 */
	public void clearOfferQuery() {
		this.offerQuery = null;
	}

	/**
	 * Call OfferService's SearchOffer method and get OfferResponse
	 * asynchronously.
	 * 
	 * @param: offerRequest
	 * @return: AsyncOfferResponse
	 */
	public AsyncOfferResponse searchOffer(OfferQuery offerQuery, ISettingService settingService) {
		if (offerQuery.validate().equals(OfferRequestFeedbackTypes.CORRECT)) {
			AsyncOfferResponse aresponse = new AsyncOfferResponse();
			aresponse.registerReceiver(applicationContext);
			// Offload processing to IntentService
			OfferIntentService.startService(applicationContext, offerQuery, settingService.getCurrentLanguagesKey());

			// Return the async response who will receive the final return
			return aresponse;
		} else {
			return null;
		}

	}

	/**
	 * Call OfferService's searchTrains method and get previous and next trains
	 * data asynchronously.
	 * 
	 * @param: AdditionalConnectionsParameter
	 * @return: AsyncOfferResponse
	 */
	public AsyncOfferResponse searchTrains(
			AdditionalConnectionsParameter additionalConnectionsParameter,  ISettingService settingService) {
		AsyncOfferResponse aresponse = new AsyncOfferResponse();
		aresponse.registerReceiver(applicationContext);
		// Offload processing to IntentService
		OfferTrainsIntentService.startService(applicationContext,offerQuery, 
				additionalConnectionsParameter,  settingService.getCurrentLanguagesKey());

		// Return the async response who will receive the final return
		return aresponse;

	}

	/**
	 * set the default value of OfferRequest
	 * 
	 * @param offerQuery
	 */
	public void setDefaultOfferQuery(OfferQuery offerQuery, Context context, String userPreferedCurrencyCode) {
		//Log.d(TAG, "setDefaultOfferQueryFromLastSelection..........." + userPreferedCurrencyCode );
		// long timeInMillis = Calendar.getInstance().getTimeInMillis();
		// Date departureDate = new Date(timeInMillis);
		// Date returnDate = new Date(timeInMillis + 86400000); //returnDate is
		// 1 Day later then detparureDate.
		// offerQuery.getDepartureQueryParameters().setDateTime(departureDate);
		offerQuery.getDepartureQueryParameters().setDateTime(null);
		offerQuery.getDepartureQueryParameters().setTimePreference(
				TimePreference.DEPARTURE);
		offerQuery.setTravelType(TravelType.RETURN);
		// offerQuery.getReturnQueryParameters().setDateTime(returnDate);
		offerQuery.getReturnQueryParameters().setDateTime(null);
		offerQuery.getReturnQueryParameters().setTimePreference(
				TimePreference.DEPARTURE);
		offerQuery.setComforClass(ComforClass.SECOND);
		offerQuery.getTravelPartyMembers().clear();
		//offerQuery.gettravelParty().add(new PartyMember(PersonType.ADULT));
		PartyMember partyMember = new PartyMember(PersonType.ADULT);
		partyMember.setPartMemberSortorderField(8);
		partyMember.setPartyMemberCount(1);
		List<ReductionCard> reductionCards = new ArrayList<ReductionCard>();
		reductionCards.add(new ReductionCard("",""));
		//reductionCards.add(new ReductionCard("123","123"));
		//reductionCards.add(new ReductionCard("456","456"));
		partyMember.setReductionCards(reductionCards);
		//partyMember.setReductionCards(new ReductionCard());
		offerQuery.getTravelPartyMembers().add(partyMember);
		//offerQuery.addGroupPartyMember(partyMember);
		// offerQuery.setOriginStationName("Paris");
		/*if (offerQuery.getOriginStationRCode() == null) {
			offerQuery.setOriginStationRCode(context
					.getString(R.string.planner_view_select_station));
		}
		
		// offerQuery.setDestinationStationName("Brussels");
		if(offerQuery.getDestinationStationRCode() == null){
			offerQuery.setDestinationStationRCode(context
					.getString(R.string.planner_view_select_station));
		}*/
		
		offerQuery.setPreferredCurrency(userPreferedCurrencyCode);
		offerQuery.setTicketLanguage(null);
	}
	/**
	 * set the value of OfferQuery, the data are from last selection.
	 * 
	 * @param lastOfferQuery
	 */
	public void setDefaultOfferQueryFromLastSelection(OfferQuery offerQuery, String userPreferedCurrencyCode) {

		//Log.d(TAG, "setDefaultOfferQueryFromLastSelection..........." + userPreferedCurrencyCode );
		OfferQuery lastOfferQuery = null;
		
			//Log.d(TAG, "OfferQuery.json.....");
		String jsonString;
		try {
			OfferQueryDataBaseService dataBaseService = new OfferQueryDataBaseService(applicationContext);
			jsonString = dataBaseService.readOfferQuery("OfferQuery");
			//jsonString = FileManager.getInstance().readExternalStoragePrivateFile(applicationContext, null, "OfferQuery.json");
			
			lastOfferQuery = new OfferQueryConverter().parseOfferQuery(jsonString);
			offerQuery.setOriginStationDestinationName(lastOfferQuery.getOriginStationDestinationName());
			offerQuery.setOriginStationRCode(lastOfferQuery.getOriginStationRCode());
			offerQuery.setOriginStationName(lastOfferQuery.getOriginStationName());
			offerQuery.setOriginStationSynonymeName(lastOfferQuery.getOriginStationSynonymeName());
			
			offerQuery.setDestinationStationDestinationName(lastOfferQuery.getDestinationStationDestinationName());
			offerQuery.setDestinationStationRCode(lastOfferQuery.getDestinationStationRCode());
			offerQuery.setDestinationStationName(lastOfferQuery.getDestinationStationName());
			offerQuery.setDestinationStationSynonymeName(lastOfferQuery.getDestinationStationSynonymeName());
			
			offerQuery.setTravelType(lastOfferQuery.getTravelType());
			//Log.d(TAG, "OfferQuery.json....." + lastOfferQuery.getTravelType());
			
			// offerQuery.getReturnQueryParameters().setDateTime(returnDate);
			//Log.d(TAG, "offerQuery.getOriginStationDestinationName====" + offerQuery.getOriginStationDestinationName());
			//Log.d(TAG, "offerQuery.getOriginStationRCode====" + offerQuery.getOriginStationRCode());
			//Log.d(TAG, "offerQuery.getOriginStationName====" + offerQuery.getOriginStationName());
			//Log.d(TAG, "offerQuery.getDestinationStationDestinationName====" + offerQuery.getDestinationStationDestinationName());
			//Log.d(TAG, "offerQuery.getDestinationStationRCode====" + offerQuery.getDestinationStationRCode());
			//Log.d(TAG, "offerQuery.getDestinationStationName====" + offerQuery.getDestinationStationName());
			if(lastOfferQuery.getComforClass() == null){
				offerQuery.setComforClass(ComforClass.SECOND);
			}else{
				offerQuery.setComforClass(lastOfferQuery.getComforClass());
			}				
			
			offerQuery.setTravelPartyMember(lastOfferQuery.getTravelPartyMembers());
			
			offerQuery.setListCorporateCards(lastOfferQuery.getListCorporateCards());
			offerQuery.setPreferredCurrency(userPreferedCurrencyCode);
			
			if (lastOfferQuery.getGreenPointsNumber() != null && !StringUtils.equalsIgnoreCase("", lastOfferQuery.getGreenPointsNumber())) {
				offerQuery.setGreenPointsNumber(lastOfferQuery.getGreenPointsNumber());
				offerQuery.setHasGreenPointsNumber(true);
			}
			offerQuery.setTicketLanguage(lastOfferQuery.getTicketLanguage());
			
		} catch (Exception e) {
			e.printStackTrace();
			//setDefaultOfferQuery(offerQuery, applicationContext, userPreferedCurrencyCode);
		}
			//Log.d(TAG, "OfferQuery.json.....");
		
	}
	

	
	 public void saveOfferQueryToLastOfferQuery() {
		 getAllLastOfferQuery();
		 if (allLastOfferQuery != null && allLastOfferQuery.getLastOfferQueries() != null) {
			 allLastOfferQuery.getLastOfferQueries().add(offerQuery);
			 
		 }
		
		  String postJsonString = ObjectToJsonUtils.getPostOfferQueryStr(offerQuery, allLastOfferQuery, false);
		  
		  // FileManager.getInstance().writeToSdCardFromString("/OfferQuery.json",
		  // postJsonString);
		  OfferQueryDataBaseService dataBaseService = new OfferQueryDataBaseService(applicationContext);
		  dataBaseService.insertOfferQuery("OfferQuery", postJsonString);
		  //FileManager.getInstance().createExternalStoragePrivateFileFromString(applicationContext, null, "OfferQuery.json", postJsonString);
		 }
	public void deleteLastOfferQuery(){
		FileManager.getInstance().deleteExternalStoragePrivateFile(applicationContext, null, "OfferQuery.json");
		OfferQueryDataBaseService dataBaseService = new OfferQueryDataBaseService(applicationContext);
		dataBaseService.deleteLastQueryById("OfferQuery");
	}
	/**
	 * Use a OfferData find its NotCombinableIds.
	 * 
	 * @param offerData
	 * @return
	 */
	public List<String> getFoundNotCombinableIds(Offer offerData,
			OfferResponse offerResponse) {
		List<String> foundNotCombinableIds = new ArrayList<String>();
		if (offerResponse != null && offerResponse.getCombinationRestrictions() != null) {
			List<CombinationMatrixRow> combinationMatrixRowList = offerResponse
					.getCombinationRestrictions().getCombinationMatrixRow();

			Iterator<CombinationMatrixRow> iterator = combinationMatrixRowList
					.iterator();

			String offerInfoId = offerData.getOfferInfoId();

			while (iterator.hasNext()) {

				CombinationMatrixRow combinationMatrixRow = iterator.next();
				if (combinationMatrixRow.getNotCombinableIds().containsKey(
						offerInfoId)) {
					foundNotCombinableIds = combinationMatrixRow
							.getNotCombinableIds().get(offerInfoId);
					break;
				}
			}
		}

		return foundNotCombinableIds;
	}

	/**
	 * Use OfferData get available return connections.
	 * 
	 * @param offerData
	 * @return
	 */
	public List<Connection> getAvailableReturnConnectionList(Offer offerData,
			OfferResponse offerResponse) {
		if (offerData == null) {
			return null;
		}

		List<String> foundNotCombinableIds = getFoundNotCombinableIds(
				offerData, offerResponse);

		List<Connection> availableReturnConnectionList = new ArrayList<Connection>();
		List<Offer> availableOffer = null;

		for (int i = 0; i < offerResponse.getTravelList().get(1)
				.getConnections().size(); i++) {

			Connection connectionData = offerResponse.getTravelList().get(1)
					.getConnections().get(i);
			List<Offer> offerDatas = connectionData.getOfferData();

			int offerDateSize = offerDatas.size();
			int notCombinableIdCoutn = 0;
			availableOffer = new ArrayList<Offer>();
			for (int j = 0; j < offerDatas.size(); j++) {
				boolean hasNotCombinableIds = false;
				Offer OfferData = offerDatas.get(j);

				Iterator<String> iteratorNotCombinableIds = foundNotCombinableIds
						.iterator();

				while (iteratorNotCombinableIds.hasNext()) {
					String NotCombinableId = iteratorNotCombinableIds.next();

					if (OfferData != null) {
						if (OfferData.getOfferInfoId().equals(NotCombinableId)) {
							hasNotCombinableIds = true;

							notCombinableIdCoutn++;
							continue;
						}
					}
				}
				if (hasNotCombinableIds == false) {
					availableOffer.add(OfferData);
				}
			}
			if (offerDateSize != notCombinableIdCoutn) {
				Connection connection = new Connection(connectionData
						.getDeparture(), connectionData.getArrivalTime(), connectionData.getDuration(),
						connectionData.getNumberOfTransfers(), availableOffer,
						connectionData.getLegData(), connectionData.getHafasMessages());
				availableReturnConnectionList.add(connection);
				connection = null;
			}
		}

		return availableReturnConnectionList;
	}
	
	
	
	public double getOfferPrice(ISettingService settingService, IMasterService masterService, Offer offer){
		
		Currency currency = getCurrentCurrency(settingService, masterService);
		double price = 0;
		String currencyString = EUR;
		if (currency != null) {
			currencyString = currency.getCurrencyCode();
		}
		if(EUR.equalsIgnoreCase(currencyString)){
			price = offer.getPrice().getAmount();
		}else{
			price = offer.getPriceInPreferredCurrency().getAmount();
		}
		return price;
	}
	
	public double getTariffPrice(ISettingService settingService, IMasterService masterService, 
			TariffPassenger tariffPassenger){
		
		Currency currency = getCurrentCurrency(settingService, masterService);
		double price = 0;

		String currencyString = EUR;
		if (currency != null) {
			currencyString = currency.getCurrencyCode();
		}
		//Log.d(TAG, "currencyString..........." + currencyString );
		if(EUR.equalsIgnoreCase(currencyString)){
			if(tariffPassenger != null && tariffPassenger.getPrice() != null){
				price = tariffPassenger.getPrice().getAmount();				
			}else{
				return -1 ;
			}
			
		}else{
			if(tariffPassenger != null && tariffPassenger.getPrice() != null){
				price = tariffPassenger.getPriceInPreferredCurrency().getAmount();
			}else{
				return -1 ;
			}			
		}
		return price;
	}
	
	
	public String getTariffPriceAndCurrencySymbol(ISettingService settingService, IMasterService masterService, 
			TariffPassenger tariffPassenger){
		Currency currency = getCurrentCurrency(settingService, masterService);
		String currencySymbol = getCurrencySymbol(currency);
		String currencyString = getCurrencyCodeString(currency);
		String priceString = "";					
		Price price;				
			
		if(EUR.equalsIgnoreCase(currencyString)){
			price = tariffPassenger.getPrice();
		}else {
			price = tariffPassenger.getPriceInPreferredCurrency();
		}
		
		if (price != null && price.getAmount() > 0) {
			priceString = String.valueOf(price.getAmount());
			return currencySymbol + " " + priceString;
		}		
		return null;
	}
	
	public String getOfferItemPriceAndCurrencySymbol(ISettingService settingService, IMasterService masterService, 
			OfferItem OfferItem){
		Currency currency = getCurrentCurrency(settingService, masterService);
		String currencySymbol = getCurrencySymbol(currency);
		String currencyString = getCurrencyCodeString(currency);
		String priceString = "";					
		Price price;				
			
		if(EUR.equalsIgnoreCase(currencyString)){
			price = OfferItem.getPrice();
		}else {
			price = OfferItem.getPriceInPreferredCurrency();
		}
		
		if (price != null && price.getAmount() > 0) {
			priceString = String.valueOf(price.getAmount());
			return currencySymbol + " " + priceString;
		}		
		return null;
	}
	
	private String getCurrencyCodeString(Currency currency){
		String currencyString = EUR;
		if (currency != null) {
			currencyString = currency.getCurrencyCode();
		}	
		return currencyString;
	}
	private String getCurrencySymbol(Currency currency){
		String currencySymbol = "";
		if (currency != null) {
			currencySymbol = currency.getCurrencySymbol();
		}
		return currencySymbol;
	}

	public ReductionCard getAppliedReductionCard(TariffPassenger tariffPassenger){
		ReductionCard reductionCard = null;
		
		if(tariffPassenger != null && tariffPassenger.getAppliedReductionCards() != null 
				&& tariffPassenger.getAppliedReductionCards().size() > 0){
			
			reductionCard = tariffPassenger.getAppliedReductionCards().get(0);
			
		}
		if (reductionCard != null) {
			
		}
		
		return reductionCard;
	}
	
	private Currency getCurrentCurrency(ISettingService settingService, IMasterService masterService){
		Currency currency = null;
		//currency = settingService.getUserPreferedCurrency( masterService);
		return currency;
	}
	public boolean hasReservation(ISettingService settingService, IMasterService masterService, Offer offer){
		Currency currency = getCurrentCurrency(settingService, masterService);
		String currencyString = EUR;
		if (currency != null) {
			currencyString = currency.getCurrencyCode();
		}
		if(offer != null){
			if(EUR.equalsIgnoreCase(currencyString)){
				if(offer.getPrice() != null && offer.getPriceWithOptionalReservations() != null){
					if(offer.getPrice().getAmount() != offer.getPriceWithOptionalReservations().getAmount()){
						return true;
					}else{
						return false;
					}	
				}
						
			}else{
				if(offer.getPriceInPreferredCurrency() != null && offer.getPriceWithOptionalReservationsInPreferredCurrency() != null){
					if(offer.getPriceInPreferredCurrency().getAmount() != offer.getPriceWithOptionalReservationsInPreferredCurrency().getAmount()){
						return true;
					}else{
						return false;
					}
				}
							
			}
		}
		
		return false;
	}
	public String getReservationPrice(IOfferService offerService, ISettingService settingService, IMasterService masterService, Offer offer){
		Currency currency = getCurrentCurrency(settingService, masterService);
		double price = 0;
		String currencyString = EUR;
		int passengerCount = 1;
		if(offerService != null && offerService.getOfferQuery() != null){
			passengerCount = offerService.getOfferQuery().getPassengerCount();
		}
		if (currency != null) {
			currencyString = currency.getCurrencyCode();
		}
		if(EUR.equalsIgnoreCase(currencyString)){
			
			price = offer.getPriceWithOptionalReservations().getAmount() - offer.getPrice().getAmount();
						
		}else{
			price = offer.getPriceWithOptionalReservationsInPreferredCurrency().getAmount() - offer.getPriceInPreferredCurrency().getAmount();
		}
		price = price / passengerCount;
		String priceString = Utils.doubleFormat(price);
		return priceString;
	}

	public String getOfferSymbol(ISettingService settingService, IMasterService masterService){
		
		Currency currency = null;
		//currency = settingService.getUserPreferedCurrency( masterService);
		String symbol = "€";
		if (currency != null) {
			symbol = currency.getCurrencySymbol();
		}			
		return symbol;
	}
	
	public List<Offer> getOffers(Connection selsetedDepartureConnection, OfferQuery offerQuery){
		List<Offer> offers = new ArrayList<Offer>();
		if(selsetedDepartureConnection != null){
			//Log.i(TAG, "offers...getOfferData=====" + selsetedDepartureConnection.getOfferData());	
			if(selsetedDepartureConnection.getOfferData() != null){
				
				for (int i = 0; i < selsetedDepartureConnection.getOfferData().size(); i++) {
					
					Offer offer = selsetedDepartureConnection.getOfferData().get(i);
					//Log.i(TAG, "offers...offer=====" + offer.getComforClass());	
					if (offer.getComforClass() == offerQuery.getComforClass()) {
						//Log.i(TAG, "getOfferQuery().getComforClass()...offer=====" + offerQuery.getComforClass());	
						offers.add(offer);
					}
				}
			}
		}
		//Log.i(TAG, "getOffers...offers...offer=====" +offers);	
		return offers;
	}

	public AsyncCorporateCardResponse searchGreenPoints(String greenpointNumber, String languageBeforSetting) {
		AsyncCorporateCardResponse aresponse = new AsyncCorporateCardResponse();
		aresponse.registerReceiver(applicationContext);
		// Offload processing to IntentService
		CorporateCardIntentService.startService(applicationContext,
				greenpointNumber,  languageBeforSetting);

		// Return the async response who will receive the final return
		return aresponse;
	}

	public void saveFtpCardForUser() {
		SharedPreferences settings = applicationContext.getSharedPreferences(FTP_CARD, 0);
		String eurostarFtpCard = offerQuery.getTravelPartyMembers().get(0).getEurostarFtpCard();
		String thalysFtpCard = offerQuery.getTravelPartyMembers().get(0).getThalysFtpCard();

		settings.edit().putString(FTP_CARD_EUROSTAR, eurostarFtpCard).commit();
		settings.edit().putString(FTP_CARD_THALYS, thalysFtpCard).commit();
	}
	
	

	public String getEurostarFtpCardForUser() {
		SharedPreferences settings = applicationContext.getSharedPreferences(FTP_CARD, 0);
		String ftpCard = settings.getString(FTP_CARD_EUROSTAR, "");
		/*if (!StringUtils.isEmpty(ftpCard)) {
			ftpCard = ftpCard.substring(6);
		}*/
		return ftpCard;
	}

	public String getThalysFtpCardForUser() {
		SharedPreferences settings = applicationContext.getSharedPreferences(FTP_CARD, 0);
		String ftpCard = settings.getString(FTP_CARD_THALYS, "");	
		/*if (!StringUtils.isEmpty(ftpCard)) {
			ftpCard = ftpCard.substring(6);
		}*/
		return ftpCard;
	}
	
	public boolean isBookingTimeOut(){
		
		Date lastRefreshTime = getBookingTime();
		Date date = new Date();		
		Calendar ca = Calendar.getInstance(); 

		ca.setTime(lastRefreshTime);
		int num = ca.get(Calendar.MINUTE) + 55;// Add one hour
		ca.set(Calendar.MINUTE, num);
		
		Date oneHourLaterTime = ca.getTime();
		
		return date.after(oneHourLaterTime);		
	}
	
	public void recordBookingTime(){
		SharedPreferences settings = applicationContext.getSharedPreferences(LAST_BOOKING_STARTED_TIME, 0);
		Date nowDate = new Date();
		
		String nowDateString = DateUtils.dateTimeToString(nowDate);
		//Log.d(TAG, "nowDateString ..." + nowDateString);
		
		settings.edit().putString("BookingStarted", nowDateString).commit();
	}
	
	private Date getBookingTime(){
		
		SharedPreferences settings = applicationContext.getSharedPreferences(LAST_BOOKING_STARTED_TIME, 0);
		String lastBookingTimeStr = settings.getString("BookingStarted", "");
		
		Date lastRefreshTime = null;
		if (StringUtils.isEmpty(lastBookingTimeStr)) {
			lastRefreshTime = new Date();
		}else {
			lastRefreshTime = DateUtils.stringToDateTime(lastBookingTimeStr);
		}
		
		return lastRefreshTime;
		
	}

	public boolean isHasWarning(Connection selsetedDepartureConnection, Connection selectedReturnConnection){
		if (selsetedDepartureConnection != null) {
			for (Leg leg : selsetedDepartureConnection.getLegData()) {
				if (leg.isHasWarning()) {
					return true;
				}
			}
		}

		if (selectedReturnConnection != null) {
			for (Leg leg : selectedReturnConnection.getLegData()) {
				if (leg.isHasWarning()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getTransferRes(int transferCount){
		
		if (transferCount > 1) {
			return applicationContext.getString(R.string.departurelist_view_transfers);
		}
		return applicationContext.getResources().getString(R.string.departurelist_view_transfer);
	}
	
}
