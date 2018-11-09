package com.nmbs.dataaccess.converters;

import java.text.ParseException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CombinationMatrix;
import com.nmbs.model.CombinationMatrixRow;
import com.nmbs.model.Connection;
import com.nmbs.model.CorporateContract;
import com.nmbs.model.HafasMessage;
import com.nmbs.model.Leg;
import com.nmbs.model.Offer;
import com.nmbs.model.OfferItem;
import com.nmbs.model.OfferLegItem;
import com.nmbs.model.OfferQuery;
import com.nmbs.model.OfferResponse;
import com.nmbs.model.PartyMember.PersonType;
import com.nmbs.model.Price;
import com.nmbs.model.ReductionCard;
import com.nmbs.model.TariffPassenger;
import com.nmbs.model.TrainInfo;
import com.nmbs.model.UserMessage;

import com.nmbs.model.RestResponse;
import com.nmbs.model.SeatingPreference;
import com.nmbs.model.Stop;
import com.nmbs.model.Tariff;
import com.nmbs.model.Travel;
import com.nmbs.model.OfferQuery.ComforClass;
import com.nmbs.model.OfferQuery.TravelType;
import com.nmbs.util.DateUtils;


/**
 * Parse JsonString to Object. 
 *
 */
public class OfferResponseConverter {
	
	/**
	 * @param String JsonFile , boolean isOneway
	 * @return value: OfferResponse
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 * @throws NoTicket 
	 * @Description Parse json to OfferResponse
	 */
	public OfferResponse parseOffer(String jsonString, OfferQuery.TravelType isOneway, ComforClass comforClass) throws JSONException, NumberFormatException, ParseException, NoTicket{
		
		OfferResponse offerResponse = null;
		CombinationMatrix combinationMatrixModel = null;
		UserMessage userMessage = null;
			//Log.d("parseJsonAndCreateModel", "Starting...");
			//Log.d("parseJsonAndCreateModel", "isOneway ?..." + isOneway);
			// Parse the text to the JSON parts
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray travelDataArray = null;
			JSONObject combinationMatrix = null;
			JSONObject userMessageJSONObject = null;
			JSONArray userMessageJSONArray = null;
			if (jsonObject.has("TravelData")) {
				if (isOneway == TravelType.ONEWAY) {
					travelDataArray = jsonObject.getJSONArray("TravelData");
				}	
				else {
					travelDataArray = jsonObject.getJSONArray("TravelData");
					if(!jsonObject.isNull("CombinationRestrictions")){
						combinationMatrix = jsonObject.getJSONObject("CombinationRestrictions");
						combinationMatrixModel = readCombinationRestrictionsFromJson(combinationMatrix);
					}
				}
			}
			
			JSONArray connectionsArray;
			JSONObject travelDataObject ;
			JSONObject connectionObject;
			
			JSONArray legsArray;
			
			JSONArray offersArray;
			JSONArray hafasMessagesArray;
			
			Travel travelDataModel = null;
			Connection connectionData;
			List <Connection> connectionDataList = null;
			List<Travel> travelDataList = new ArrayList<Travel>();
			List<UserMessage> userMessages = new ArrayList<UserMessage>();
			
			// TravelData Array
			if(travelDataArray != null){
				for (int i = 0; i < travelDataArray.length(); i++) {
					travelDataObject = travelDataArray.getJSONObject(i);
					connectionsArray = travelDataObject.getJSONArray("Connections");
					String direction = travelDataObject.getString("Direction");
					String travelId = travelDataObject.getString("TravelId");
					connectionDataList = new ArrayList<Connection>();
					// connections Array
					for (int j = 0; j < connectionsArray.length(); j++) {
						connectionObject = connectionsArray.getJSONObject(j);
						
						String departure = connectionObject.getString("Departure");
						String arrival = connectionObject.getString("Arrival");
						String duration = connectionObject.getString("Duration");
						String numberOfTransfers = connectionObject.getString("NumberOfTransfers");

						// legs Array
						legsArray = connectionObject.getJSONArray("Legs");
						List<Leg> legDataList = readLegsFromJson(legsArray);
						
						// offers Array
						offersArray = connectionObject.getJSONArray("Offers");
						List<Offer> offerDataList = readOffersFromJson(offersArray, comforClass);
						
						List<HafasMessage> hafasMessages = new ArrayList<HafasMessage>();
						if(!connectionObject.isNull("HafasMessages")){
							hafasMessagesArray = connectionObject.getJSONArray("HafasMessages");
							hafasMessages = readHafasMessages(hafasMessagesArray);
						}
						
						
						
						if (offerDataList != null) {
							/*connectionData = new Connection(DateUtils.stringToDateTime((departure)), DateUtils.stringToDateTime((arrival)), 
									DateUtils.getTimeFormString(duration), Integer.parseInt(numberOfTransfers), offerDataList, legDataList, hafasMessages);*/
							
							//connectionDataList.add(connectionData);
						}
					
					}
					travelDataModel = new Travel(connectionDataList, direction ,travelId);				
					travelDataList.add(travelDataModel);		
				}			
			}
			//Log.d("UserMessage", "UserMessage..." + jsonObject.has("UserMessage"));
			
			
			//////////////////////////TEST FOR USER MESSAGES/////////////////////////
			
		/*	userMessage = new UserMessage("introductionTitle111111", "introductionDescription1111111", "", "popupTitle1111111111", "popupDescription111111111", "", 
					"", false);
			
			//userMessages.add(userMessage);
			userMessage = new UserMessage("introductionTitle222222", "introductionDescription2222222", "", "popupTitle222222222", "popupDescription222222222", "", 
					"", false);
			
			//userMessages.add(userMessage);
			userMessage = new UserMessage("introductionTitle333333", "introductionDescription333333", "", "popupTitle333333333", "popupDescription33333333", "", 
					"", false);*/
			
			//userMessages.add(userMessage);
			
			if (jsonObject.has("UserMessages")) {
				
				
				userMessageJSONArray = jsonObject.getJSONArray("UserMessages");
				for (int i = 0; i < userMessageJSONArray.length(); i++) {
					
					userMessageJSONObject = userMessageJSONArray.getJSONObject(i);
					String introductionTitle = userMessageJSONObject.getString("IntroductionTitle");
					String introductionDescription = userMessageJSONObject.getString("IntroductionDescription");
					String logo = userMessageJSONObject.getString("Logo");
					String popupTitle = userMessageJSONObject.getString("PopupTitle");
					String popupDescription = userMessageJSONObject.getString("PopupDescription");
					String popupLowResolutionImage = userMessageJSONObject.getString("PopupLowResolutionImage");
					String popupHighResolutionImage = userMessageJSONObject.getString("PopupHighResolutionImage");
					
					//ShowMessagesDefaultCollapsed
					//Log.d("UserMessage", "introductionTitle..." + introductionTitle);
					//Log.d("UserMessage", "introductionDescription..." + introductionDescription);
					//Log.d("UserMessage", "logo..." + logo);
					//Log.d("UserMessage", "popupTitle..." + popupTitle);
					//Log.d("UserMessage", "popupDescription..." + popupDescription);
					//Log.d("UserMessage", "popupLowResolutionImage..." + popupLowResolutionImage);
					//Log.d("UserMessage", "popupHighResolutionImage..." + popupHighResolutionImage);
					//Log.d("UserMessage", "showDefaultCollapsed..." + showDefaultCollapsed);
					
					/*userMessage = new UserMessage(introductionTitle, introductionDescription, logo, popupTitle, popupDescription, popupLowResolutionImage, 
							popupHighResolutionImage);
					userMessages.add(userMessage);*/
				}
				
				
			}
			boolean showDefaultCollapsed = true;
			if(jsonObject.has("ShowMessagesDefaultCollapsed")){
				showDefaultCollapsed = jsonObject.getBoolean("ShowMessagesDefaultCollapsed");
			}
			String OriginStationName = "";
			String DestinationStationName = "";
			if(jsonObject.has("OriginStationName")){
				OriginStationName = jsonObject.getString("OriginStationName");
			}
			if(jsonObject.has("DestinationStationName")){
				DestinationStationName = jsonObject.getString("DestinationStationName");
			}
			//Log.d("UserMessage", "OriginStationName..." + OriginStationName);
			//Log.d("UserMessage", "DestinationStationName..." + DestinationStationName);
			
			offerResponse = new OfferResponse(travelDataList, combinationMatrixModel, userMessages, showDefaultCollapsed, OriginStationName, DestinationStationName);
			//Log.d("parseJsonAndCreateModel", "End...");
			
		if(travelDataList == null || travelDataList.size() == 0 || travelDataList.get(0) == null){
			//throw new NoTicket();
		}else if(travelDataList.get(0).getConnections() == null || travelDataList.get(0).getConnections().size() == 0) {
			//throw new NoTicket();
		}
		
		return offerResponse;
		
	}
		
	/**
	 *	readCombinationRestrictionsFromJson
	 * @param combinationRestrictionsObject
	 * @param nmbsModel
	 * @throws JSONException 
	 */
	private CombinationMatrix readCombinationRestrictionsFromJson(JSONObject combinationRestrictionsObject) throws JSONException {
		CombinationMatrix combinationMatrix = null;
			
			JSONArray rowsArray = combinationRestrictionsObject.getJSONArray("Rows");
			JSONObject rowObject ;						
			JSONArray notCombinableIdsArray;
			String notCombinableId = null;
			
			List<CombinationMatrixRow> rows = new ArrayList<CombinationMatrixRow>();
			
			CombinationMatrixRow combinationMatrixRow;
			Map<String,List<String>> notCombinableIdsMap ;
			
			for (int i = 0; i < rowsArray.length(); i++) {
				rowObject = rowsArray.getJSONObject(i);				
				String offerInfoId = rowObject.getString("OfferInfoId");
				
				notCombinableIdsArray = rowObject.getJSONArray("NotCombinableIds");		
				notCombinableIdsMap = new HashMap<String, List<String>>();
				List<String> notCombinableIdList = new ArrayList<String>();
				for (int j = 0; j < notCombinableIdsArray.length(); j++) {
					notCombinableId = notCombinableIdsArray.optString(j);		
					notCombinableIdList.add(notCombinableId);
							
				}		
				notCombinableIdsMap.put(offerInfoId,notCombinableIdList);		
				combinationMatrixRow = new CombinationMatrixRow(offerInfoId, notCombinableIdsMap);
				rows.add(combinationMatrixRow);
				combinationMatrix = new CombinationMatrix(rows);				
			}			
		
		
		return combinationMatrix;
	}
	
	private List<HafasMessage> readHafasMessages(JSONArray hafasMessageArray) throws JSONException{
		JSONObject hafasMessageObject;
		HafasMessage hafasMessageData;
		List<HafasMessage> hafasMessageDataList = new ArrayList<HafasMessage>();
		for (int i = 0; i < hafasMessageArray.length(); i++) {
			hafasMessageObject = hafasMessageArray.getJSONObject(i);
			String header = "";
			if(!hafasMessageObject.isNull("Header")){
				header = hafasMessageObject.getString("Header");
			}
			String lead = "";
			if(!hafasMessageObject.isNull("Lead")){
				lead = hafasMessageObject.getString("Lead");
			}
			String text = "";
			if(!hafasMessageObject.isNull("Text")){
				text = hafasMessageObject.getString("Text");
			}
			String url = "";
			if(!hafasMessageObject.isNull("URL")){
				url = hafasMessageObject.getString("URL");
			}
			String stationStart = "";
			if(!hafasMessageObject.isNull("StationStart")){
				stationStart = hafasMessageObject.getString("StationStart");
			}
			String stationEnd = "";
			if(!hafasMessageObject.isNull("StationEnd")){
				stationEnd = hafasMessageObject.getString("StationEnd");
			}
			/*hafasMessageData = new HafasMessage(header, lead, text, url, stationStart, stationEnd);
			hafasMessageDataList.add(hafasMessageData);*/
		}
		return hafasMessageDataList;
	}
		
	/**
	 * readLegsFromJson
	 * @param legsArray
	 * @throws JSONException
	 */
	private List<Leg> readLegsFromJson(JSONArray legsArray) throws JSONException {
		JSONObject legsObject;
		Leg legData;
		List<Leg> legDataList = new ArrayList<Leg>();
		
		for (int m = 0; m < legsArray.length(); m++) {
			legsObject = legsArray.getJSONObject(m);
			String id = legsObject.getString("Id");
			String hasWarning = legsObject.getString("HasWarning");
			String trainNr = "" ;
			if(!legsObject.isNull("TrainNr")){
				trainNr = legsObject.getString("TrainNr");
			}
			String trainType="";
			if(!legsObject.isNull("TrainType")){
			trainType = legsObject.getString("TrainType");
			}
			String isTrainLeg = legsObject.getString("IsTrainLeg");
			String originnName = legsObject.getString("OriginName");
			String originStationCode = legsObject.getString("OriginStationCode");
			String destinationName = legsObject.getString("DestinationName");
			String destinationStationCode = legsObject.getString("DestinationStationCode");
			String departureDateTime = legsObject.getString("DepartureDateTime");
			String arrivalDateTime = legsObject.getString("ArrivalDateTime");
			String duration = "";
			if (!legsObject.isNull("Duration")) {
				duration = legsObject.getString("Duration");
			}
			JSONArray stopArray = null;
			List<Stop> stopDataList =null;
			if (!legsObject.isNull("Stops")) {
			stopArray = legsObject.getJSONArray("Stops");
			stopDataList = readStopsFromJson(stopArray);
			}
			
			
			List<TrainInfo> trainInfos = new ArrayList<TrainInfo>();
			if (!legsObject.isNull("TrainInfos")) {
				JSONArray trainInfoArray = legsObject.getJSONArray("TrainInfos");
				trainInfos = readTrainInfosFromJson(trainInfoArray);
			}
			String legStatus = "";
			if (!legsObject.isNull("LegStatus")) {
				
				legStatus = legsObject.getString("LegStatus");
			}
			String realTimeDepartureDateTime = "";
			if (!legsObject.isNull("RealTimeDepartureDateTime")) {
				
				realTimeDepartureDateTime = legsObject.getString("RealTimeDepartureDateTime");
			}
			String realTimeArrivalDateTime = "";
			if (!legsObject.isNull("RealTimeArrivalDateTime")) {
				
				realTimeArrivalDateTime = legsObject.getString("RealTimeArrivalDateTime");
			}													
			String realTimeDepartureDelta = "";
			if (!legsObject.isNull("RealTimeDepartureDelta")) {

				realTimeDepartureDelta = legsObject.getString("RealTimeDepartureDelta");
			}
			String realTimeArrivalDelta  = "";
			if (!legsObject.isNull("RealTimeArrivalDelta")) {
				
				realTimeArrivalDelta = legsObject.getString("RealTimeArrivalDelta");
			}
			LogUtils.d("parseJsonAndCreateModel", "RealTimeDepartureDateTime is " + realTimeDepartureDateTime);
			LogUtils.d("parseJsonAndCreateModel", "RealTimeArrivalDateTime is " + realTimeArrivalDateTime);
			LogUtils.d("parseJsonAndCreateModel", "RealTimeDepartureDelta is " + realTimeDepartureDelta);
			LogUtils.d("parseJsonAndCreateModel", "RealTimeArrivalDelta is " + realTimeArrivalDelta);
			//create json model
			/*legData = new Leg(id, Boolean.valueOf(hasWarning)
					 , trainNr, trainType, Boolean.valueOf(isTrainLeg)
					 , originnName, originStationCode, destinationName, destinationStationCode
					 , DateUtils.stringToDateTime(departureDateTime), DateUtils.stringToDateTime(arrivalDateTime), stopDataList, duration
					 , trainInfos, legStatus, DateUtils.stringToDateTime(realTimeDepartureDateTime), DateUtils.stringToDateTime(realTimeArrivalDateTime)
					 , DateUtils.stringToTime(realTimeDepartureDelta), DateUtils.stringToTime(realTimeArrivalDelta));*/
			
			//legDataList.add(legData);
		}
		return legDataList;
	}
	
	private List<TrainInfo> readTrainInfosFromJson(JSONArray trainsArray) throws JSONException{
		
		List<TrainInfo> trainInfos = new ArrayList<TrainInfo>();
		
		JSONObject trainInfoObject;
		
		for (int i = 0; i < trainsArray.length(); i++) {
			trainInfoObject = trainsArray.getJSONObject(i);
			String key = "";
			if (!trainInfoObject.isNull("Key")) {
				key = trainInfoObject.getString("Key");
			}
			TrainInfo trainInfo = new TrainInfo(key);
			trainInfos.add(trainInfo);
		}
		return trainInfos;
	}
	
	private List<Stop> readStopsFromJson(JSONArray stopArray)throws JSONException{
		List<Stop> stopDataList = new ArrayList<Stop>();
		JSONObject stopObject;
		
		//Log.d("parseJsonAndCreateModel", "stopArray is " + stopArray.toString());
		for(int i = 0; i < stopArray.length(); i++){
			stopObject = stopArray.getJSONObject(i);
			String stationName = "";
			String stationCode = "";
			if (!stopObject.isNull("StationName")) {
				stationName = stopObject.getString("StationName");
			}
			if(!stopObject.isNull("StationCode")){
				stationCode = stopObject.getString("StationCode");
			}						
			
			String stopArrivalDateTime = "";
			if(!stopObject.isNull("ArrivalDateTime")){
				stopArrivalDateTime = stopObject.getString("ArrivalDateTime");
				//Log.d("parseJsonAndCreateModel", "stopArrivalDateTime is " + stopArrivalDateTime);
			}
			String stopDepartureDateTime = "";
			if(!stopObject.isNull("DepartureDateTime")){
				stopDepartureDateTime = stopObject.getString("DepartureDateTime");
			}												  
			//Log.d("parseJsonAndCreateModel", "stopDepartureDateTime is " + stopDepartureDateTime);
			
			Stop stop = new Stop(stationName, stationCode,  DateUtils.stringToDateTime(stopDepartureDateTime),  DateUtils.stringToDateTime(stopArrivalDateTime));
			stopDataList.add(stop);
			//Log.d("parseJsonAndCreateModel", "stationName is " + stationName);
		}
		return stopDataList;
	}
	/**
	 * readOffersFromJson
	 * @param offersArray
	 * @throws JSONException
	 */
	private List<Offer> readOffersFromJson(JSONArray offersArray, ComforClass comforClass) throws JSONException {
		JSONObject offersObject;
		//JSONObject priceObject;
		
		JSONArray offerItemsArray;
		
		Price price;
		Price priceInPreferredCurrency;
		Price priceWithOptionalReservations;
		Price priceWithOptionalReservationsInPreferredCurrency;
		
		Offer offerData;
		List<Offer> offerDataList = new ArrayList<Offer>();
		boolean hasEqualsComforClass = false;
		for (int m = 0; m < offersArray.length(); m++) {
			offersObject = offersArray.getJSONObject(m);
			String offerInfoId = offersObject.getString("OfferInfoId");
			String flexibility = "";
			
			if(offersObject.has("Flexibility")){
				flexibility = offersObject.getString("Flexibility");
			}
			
			boolean hasEbsLeg = offersObject.getBoolean("HasEbsLeg");
			boolean showPromoFlag = offersObject.getBoolean("ShowPromoFlag");
			boolean showCorporateFlag = offersObject.getBoolean("ShowCorporateFlag");
			boolean showReductionFlag = offersObject.getBoolean("ShowReductionFlag");
			int comfortClassTemp = offersObject.getInt("ComfortClass");
			ComforClass comforClassInOffer;
			if (comfortClassTemp==1) {
				comforClassInOffer = ComforClass.FIRST;
			}else {
				comforClassInOffer = ComforClass.SECOND;
			}
			if (comforClassInOffer == comforClass) {
				hasEqualsComforClass = true;
			}
			offerItemsArray = offersObject.getJSONArray("OfferItems");
			List<OfferItem> offerItems = readOfferItemsFromJson(offerItemsArray);
			
			/*priceObject = offersObject.getJSONObject("Price");
			String amount = priceObject.getString("Amount");

			String currencyCode = priceObject.getString("CurrencyCode");*/
			price = readPrice(offersObject, "Price");
			priceInPreferredCurrency = readPrice(offersObject, "PriceInPreferredCurrency");
			priceWithOptionalReservations = readPrice(offersObject, "PriceWithOptionalReservations");
			priceWithOptionalReservationsInPreferredCurrency = readPrice(offersObject, "PriceWithOptionalReservationsInPreferredCurrency");
			//create json model
			//price = new Price(Double.parseDouble(amount), currencyCode);
			
			
			List<CorporateContract> corporateContracts = new ArrayList<CorporateContract>();
			
			if (!offersObject.isNull("AppliedCorporateContracts")) {
				JSONArray appliedCorporateContractsArray = offersObject.getJSONArray("AppliedCorporateContracts");
				corporateContracts = readCorporateContractsFromJson(appliedCorporateContractsArray);
			}
			
			List<ReductionCard> reductionCards = new ArrayList<ReductionCard>();
			if (!offersObject.isNull("AppliedReductionCards")) {
				JSONArray appliedReductionCardssArray = offersObject.getJSONArray("AppliedReductionCards");
				reductionCards = readReductionCardsFromJson(appliedReductionCardssArray);
			}
			
			boolean showLowSeatAvailabilityFlag = false;
			
			if (!offersObject.isNull("ShowLowSeatAvailabilityFlag")) {
				showLowSeatAvailabilityFlag = offersObject.getBoolean("ShowLowSeatAvailabilityFlag");
			}
			 
			offerData = new Offer(offerInfoId, price, priceInPreferredCurrency, priceWithOptionalReservations
					, priceWithOptionalReservationsInPreferredCurrency
					, hasEbsLeg, showPromoFlag, showCorporateFlag, showReductionFlag
					, flexibility, offerItems,comforClassInOffer
					, corporateContracts, reductionCards, showLowSeatAvailabilityFlag);
			offerDataList.add(offerData);
		}
		if (hasEqualsComforClass) {
			return offerDataList;
		}else {
			return null;
		}
		
	}
	
	
	private List<CorporateContract> readCorporateContractsFromJson(JSONArray appliedCorporateContractsArray) throws JSONException{
		
		List<CorporateContract> appliedCorporateContracts = new ArrayList<CorporateContract>();
		JSONObject appliedCorporateContractJSONObject = null;
		for (int i = 0; i < appliedCorporateContractsArray.length(); i++) {
			appliedCorporateContractJSONObject = appliedCorporateContractsArray.getJSONObject(i);
			String code = appliedCorporateContractJSONObject.getString("Code");
			String name = appliedCorporateContractJSONObject.getString("Name");
			String number = appliedCorporateContractJSONObject.getString("Number");
			
			CorporateContract corporateContract = new CorporateContract(code, name, number);
			appliedCorporateContracts.add(corporateContract);
		}
		
		return appliedCorporateContracts;
	}
	
	private List<ReductionCard> readReductionCardsFromJson(JSONArray appliedReductionCardsArray) throws JSONException{
		
		List<ReductionCard> appliedReductionCards = new ArrayList<ReductionCard>();
		JSONObject appliedReductionCardJSONObject = null;
		for (int i = 0; i < appliedReductionCardsArray.length(); i++) {
			appliedReductionCardJSONObject = appliedReductionCardsArray.getJSONObject(i);
			String code = appliedReductionCardJSONObject.getString("Code");
			String name = appliedReductionCardJSONObject.getString("Name");
			
			
			ReductionCard reductionCard = new ReductionCard(name, code);
			appliedReductionCards.add(reductionCard);
		}
		
		return appliedReductionCards;
	}
	
	/**
	 * readPrice
	 * @param jsonObject
	 * @param context
	 * @return price
	 * @throws JSONException
	 */
	private Price readPrice(JSONObject jsonObject, String attributeName) throws JSONException{		
		JSONObject priceObject;
		Price price = null;
		if(!jsonObject.isNull(attributeName)){
		priceObject = jsonObject.getJSONObject(attributeName);
		String amount = priceObject.getString("Amount");
		String currencyCode = priceObject.getString("CurrencyCode");
		price = new Price(Double.parseDouble(amount), currencyCode);
		}
		
		return price;
	}

	
	
	/**
	 * readOfferItemsFromJson
	 * @param seatingPreferencesArray
	 * @throws JSONException
	 */
	private List<OfferItem> readOfferItemsFromJson(JSONArray offerItemsArray) throws JSONException {
		JSONObject offerItemsObject;
		OfferItem offerItemsData;
		Tariff tariffData;
		JSONArray tariffsArray;
		JSONObject tariffsObject;
		JSONArray flexInfoArray;
		JSONObject flexInfoObject;
		JSONObject priceObject;
		JSONArray tariffPassengersArray;
		JSONArray seatingPreferencesArray;
		
		JSONArray offerLegItemArray;
		JSONObject offerLegItemObject;
		JSONObject tariffPassengerObject;
		OfferLegItem offerLegItem;
		JSONArray appliedReductionCardArray;
		JSONObject appliedReductionCard;
		List<OfferItem> offerItemsDataList = new ArrayList<OfferItem>();
		List<Tariff> tariffsDataList = null;
		
		
		Map<String, String> flexInfoMap = null;
		List <TariffPassenger> tariffPassengers = null;
		List <ReductionCard> appliedReductionCards = null;
		for (int m = 0; m < offerItemsArray.length(); m++) {
			offerItemsObject = offerItemsArray.getJSONObject(m);
			tariffsArray = offerItemsObject.getJSONArray("Tariffs");
			tariffsDataList = new ArrayList<Tariff>();
			for (int i = 0; i < tariffsArray.length(); i++) {
				
				tariffsObject = tariffsArray.getJSONObject(i);
				boolean hasFlexInfo = tariffsObject.getBoolean("HasFlexInfo");
				String flexTitle = tariffsObject.getString("FlexTitle");
				flexInfoMap = new LinkedHashMap<String, String>();
				flexInfoArray = tariffsObject.getJSONArray("FlexInfo");
				for (int j = 0; j < flexInfoArray.length(); j++) {
					
					flexInfoObject = flexInfoArray.getJSONObject(j);
					String key = flexInfoObject.getString("Key");
					String value = flexInfoObject.getString("Value").replaceAll("\n","");
					//Log.d("FlexInfo", "Value..." + value);
					flexInfoMap.put(key, value);
				}
				String passengerTypeKey;
				Price price;
				Price priceInPreferredCurrency = null;
				TariffPassenger tariffPassenger = null;
				tariffPassengers = new ArrayList<TariffPassenger>();
				if (tariffsObject.has("TariffPassengers")) {
					tariffPassengersArray = tariffsObject.getJSONArray("TariffPassengers");
					for (int j = 0; j < tariffPassengersArray.length(); j++) {
						appliedReductionCards = new ArrayList<ReductionCard>();;
						tariffPassengerObject = tariffPassengersArray.getJSONObject(j);
						passengerTypeKey = tariffPassengerObject.getString("PassengerTypeKey");
						PersonType personType = personKeyToPersonType(passengerTypeKey);
						//Log.d("TariffPassengers", "passengerTypeKey..." + passengerTypeKey);
						appliedReductionCardArray = tariffPassengerObject.getJSONArray("AppliedReductionCards");
						for (int j2 = 0; j2 < appliedReductionCardArray.length(); j2++) {
							appliedReductionCard = appliedReductionCardArray.getJSONObject(j2);
							String code = appliedReductionCard.getString("Code");
							String name = appliedReductionCard.getString("Name");
							
							appliedReductionCards.add(new ReductionCard(name, code));
							//Log.d("TariffPassengers", "appliedReductionCards..." + appliedReductionCardArray.getString(j2));
						}
						
						if(tariffPassengerObject.has("Price")){
							priceObject = tariffPassengerObject.getJSONObject("Price");
							double amount = priceObject.getDouble("Amount");
							String currencyCode = priceObject.getString("CurrencyCode");
							//Log.d("TariffPassengers", "Price...amount..." + amount);
							//Log.d("TariffPassengers", "Price...currencyCode..." + currencyCode);
							price = new Price(amount, currencyCode);
						}else{
							price = null;
						}
						
						
						if(tariffPassengerObject.has("PriceInPreferredCurrency")){
							priceObject = tariffPassengerObject.getJSONObject("PriceInPreferredCurrency");
							double amount = priceObject.getDouble("Amount");
							String currencyCode = priceObject.getString("CurrencyCode");
							//Log.d("TariffPassengers", "PriceInPreferredCurrency...amount..." + amount);
							//Log.d("TariffPassengers", "PriceInPreferredCurrency...currencyCode..." + currencyCode);
							priceInPreferredCurrency = new Price(amount, currencyCode);
						}else{
							price = null;
						}
						tariffPassenger = new TariffPassenger(personType, appliedReductionCards, price, priceInPreferredCurrency);
						tariffPassengers.add(tariffPassenger);
					}
				}
				
				
				boolean isEbsTariff = tariffsObject.getBoolean("IsEbsTariff");
				tariffData = new Tariff(hasFlexInfo, flexTitle, tariffPassengers, flexInfoMap, isEbsTariff);
				tariffsDataList.add(tariffData);							
				
			}
			
			offerLegItemArray = offerItemsObject.getJSONArray("OfferLegItems");
			List<OfferLegItem> offerLegItems = new ArrayList<OfferLegItem>();
			for (int n = 0; n < offerLegItemArray.length(); n++) {
				
				offerLegItemObject = offerLegItemArray.getJSONObject(n);
				String legId = offerLegItemObject.getString("LegId");
				String reservationType = offerLegItemObject.getString("ReservationType");
				seatingPreferencesArray = offerLegItemObject.getJSONArray("SeatingPreferences");
				List<SeatingPreference> seatingPreferencesDataList = readSeatingPreferencesFromJson(seatingPreferencesArray);
				offerLegItem = new OfferLegItem(legId, reservationType, seatingPreferencesDataList);
				offerLegItems.add(offerLegItem);
			}
			
			
			Price price = readPrice(offerItemsObject, "Price");
			Price priceInPreferredCurrency = readPrice(offerItemsObject, "PriceInPreferredCurrency");
			
			String originName = offerItemsObject.getString("OriginName");
			String originStationCode = offerItemsObject.getString("OriginStationCode");
			String destinationName = offerItemsObject.getString("DestinationName");
			String destinationStationCode = offerItemsObject.getString("DestinationStationCode");
			
			JSONArray EBSMessageTypesArray = offerItemsObject.getJSONArray("EBSMessageTypes");
			List<String> EBSMessageTypes = new ArrayList<String>();
			for (int i = 0; i < EBSMessageTypesArray.length(); i++) {
				
				String EBSMessageType = EBSMessageTypesArray.getString(i);
				EBSMessageTypes.add(EBSMessageType);
			}
			
			
			//create json model
			offerItemsData = new OfferItem(tariffsDataList, offerLegItems, price, priceInPreferredCurrency
					, originName, originStationCode, destinationName, destinationStationCode, EBSMessageTypes);
			offerItemsDataList.add(offerItemsData);
		}
		return offerItemsDataList;
	}
	
	
	private PersonType personKeyToPersonType(String key){
		
		if (StringUtils.equalsIgnoreCase(key, "Kid0to3")) {
			return PersonType.KID0;
		}else if (StringUtils.equalsIgnoreCase(key, "Kid4to5")) {
			return PersonType.KID4;
		}else if (StringUtils.equalsIgnoreCase(key, "Kid6to11")) {
			return PersonType.KID6;
		}else if (StringUtils.equalsIgnoreCase(key, "Youth12to14")) {
			return PersonType.KID12;
		}else if (StringUtils.equalsIgnoreCase(key, "Youth15to17")) {
			return PersonType.KID15;
		}else if (StringUtils.equalsIgnoreCase(key, "Youth18to25")) {
			return PersonType.YOUTH;
		}else if (StringUtils.equalsIgnoreCase(key, "Adult")) {
			return PersonType.ADULT;
		}else if (StringUtils.equalsIgnoreCase(key, "Senior")) {
			return PersonType.SENIOR;
		}else {
			return PersonType.ADULT;
		}
	}
	
	/**
	 * readSeatingPreferencesFromJson
	 * @param seatingPreferencesArray
	 * @throws JSONException
	 */
	private List<SeatingPreference> readSeatingPreferencesFromJson(JSONArray seatingPreferencesArray) throws JSONException {
		JSONObject seatingPreferencesObject;
		SeatingPreference seatingPreferencesData;
		List<SeatingPreference> seatingPreferencesDataList = new ArrayList<SeatingPreference>();
		for (int m = 0; m < seatingPreferencesArray.length(); m++) {
			seatingPreferencesObject = seatingPreferencesArray.getJSONObject(m);
			String name = seatingPreferencesObject.getString("Name");
			String id = seatingPreferencesObject.getString("Id");
			
			//create json model
			seatingPreferencesData = new SeatingPreference(name, id);
			seatingPreferencesDataList.add(seatingPreferencesData);
		}
		return seatingPreferencesDataList;
	}
	
	
	/**
	 * Converter JSON String to RestResponse by GSON.
	 * @param jsonString
	 * @return RestResponse
	 * @throws InvalidJsonError
	 */
	public RestResponse parseSearchOffer(String jsonString) throws InvalidJsonError{ 
		RestResponse restResponse;
	  //Log.d("parseJsonRestResponse", "Starting...");
	  Gson gson = new Gson();
	  try {
	   restResponse = gson.fromJson(jsonString, RestResponse.class);
	  } catch (JsonParseException e) {
		  e.printStackTrace();
	   throw new InvalidJsonError();   
	  }
 
	   return restResponse;
	  }
}