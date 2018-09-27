package com.cfl.dataaccess.restservice.impl;

import java.io.IOException;

import java.text.ParseException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import android.content.Context;


import com.cfl.R;
import com.cfl.dataaccess.converters.CorporateCardResponseConverter;
import com.cfl.dataaccess.converters.CustomErrorMessager;
import com.cfl.dataaccess.converters.OfferResponseConverter;
import com.cfl.dataaccess.restservice.IOfferDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.RequestFail;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.TimeOutError;
import com.cfl.model.AdditionalConnectionsParameter;
import com.cfl.model.GreenPointsResponse;
import com.cfl.model.Message;
import com.cfl.model.OfferQuery;
import com.cfl.model.OfferQuery.ComforClass;
import com.cfl.model.OfferResponse;
import com.cfl.model.RestResponse;
import com.cfl.model.Travel;
import com.cfl.services.impl.OfferService;

import com.cfl.util.FileManager;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.HttpStatusCodes;
import com.cfl.util.ObjectToJsonUtils;

/**
 * Call web service get OfferResponse.
 */
public class OfferDataService extends CustomErrorMessager implements IOfferDataService{

	HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
	OfferResponseConverter offerResponseConverter = new OfferResponseConverter();
	CorporateCardResponseConverter converter = new CorporateCardResponseConverter();
	
	static String travelOutboundID;
	static String travelInboundID;
	String outbound = "Outbound";
	String inbound = "Inbound";
	/**
	 *  Call web service asynchronously and get OfferResponse.
	 *  @param OfferQuery
	 *  @param Context
	 *  @return OfferResponse
	 * @throws NoTicket 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NumberFormatException 
	 * @throws ConnectionError 
	 * @throws ConnectTimeoutException 
	 * @throws DBookingNoSeatAvailableError 
	 * @throws CustomError 
	 * @throws DBooking343Error 
	 */
	public OfferResponse executeSearchOffer(OfferQuery offerQuery, Context context, String languageBeforSetting, 
			ComforClass comforClass) throws RequestFail, InvalidJsonError, NumberFormatException, JSONException, 
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, ConnectTimeoutException, 
			DBooking343Error, CustomError, DBookingNoSeatAvailableError{

		OfferResponse offerResponse = null;
		String postJsonString = ObjectToJsonUtils.getPostOfferQueryStr(offerQuery, null, false);
		String urlStringOfOfferGuid = context.getString(R.string.server_url_offer_queries);
		
		//String urlStringOfOfferGuid = "https://api.accept-1.b-europe.com/offer-queriesV4?dofferResponseMockFile=%5CMockData%5CBooking2%5CTrainSelection%5CXml%5CDOfferResponse-Mechelen-London-2D.xml";
		
		//FileManager.getInstance().writeToSdCardFromString("/OfferQuery.json", postJsonString);
		//FileManager.getInstance().createExternalStoragePrivateFileFromString(context, "OfferQuery.json", postJsonString); 
			String responseFirst = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlStringOfOfferGuid, 
					languageBeforSetting, HTTPRestServiceCaller.HTTP_POST_METHOD, 60000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_4);
		
		OfferResponseConverter offerResponseConverter = new OfferResponseConverter();
		RestResponse restResponse = offerResponseConverter.parseSearchOffer(responseFirst);
		super.throwErrorMessage(restResponse, context);
		OfferService.GUID = getGUID(restResponse);
		
		String urlStringOfOfferQueries = context.getString(R.string.server_url_offer_queries) +"/" + OfferService.GUID;
		String responseSecond = httpRestServiceCaller.executeHTTPRequest(context, null, urlStringOfOfferQueries, 
				languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 60000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_4);
		//FileManager.getInstance().createExternalStoragePrivateFileFromString(context, "OfferQuery.json", responseSecond);

		restResponse = offerResponseConverter.parseSearchOffer(responseSecond);
		super.throwErrorMessage(restResponse, context);
		offerResponse = offerResponseConverter.parseOffer(responseSecond, offerQuery.getTravelType(), comforClass);
		super.throwErrorMessage(offerResponse, context);
		List<Travel> traveList = offerResponse.getTravelList();
		if (traveList != null) {
			
			for (int i = 0; i < traveList.size(); i++) {
				Travel travel = traveList.get(i);
				if (outbound.equals(travel.getDirection())) {
					travelOutboundID = travel.getTravelId();
				}else if (inbound.equals(travel.getDirection())) {
					travelInboundID = travel.getTravelId();
				} 
			}
		}
		return offerResponse;		
	}

	/**
	 *  Call web service asynchronously and get OfferResponse.
	 *  @param OfferQuery
	 *  @param Context
	 *  @return OfferResponse
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 * @throws ConnectionError 
	 * @throws DBookingNoSeatAvailableError 
	 * @throws CustomError 
	 * @throws DBooking343Error 
	 */
	public OfferResponse executeSearchTrains(Context context,AdditionalConnectionsParameter 
			additionalConnectionsParameter, String languageBeforSetting, ComforClass comforClass) 
	throws InvalidJsonError, RequestFail, IOException, JSONException, NoTicket, NumberFormatException, 
	ParseException, ConnectionError, BookingTimeOutError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{
		
		if (additionalConnectionsParameter.isDeparture()) {
			additionalConnectionsParameter.setTravelId(travelOutboundID);
		}else {
			additionalConnectionsParameter.setTravelId(travelInboundID);
		}
		
		OfferResponse offerResponse = null;
		String postJsonString = ObjectToJsonUtils.getPostAdditionalConnectionsParameterStr(additionalConnectionsParameter);
		String urlString = context.getString(R.string.server_url_offer_queries) + "/" + OfferService.GUID + "/additionalconnections";
		
		String response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString ,languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_POST_METHOD, 60000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_4);
		
		//String response = FileManager.getInstance().readExternalStoragePrivateFile(context, null, "error.json");
		
		OfferResponseConverter offerResponseConverter = new OfferResponseConverter();
		RestResponse restResponse = offerResponseConverter.parseSearchOffer(response);
		super.throwErrorMessage(restResponse, context);
		offerResponse = offerResponseConverter.parseOffer(response, additionalConnectionsParameter.getTravelType(), comforClass);
		
		super.throwErrorMessage(offerResponse, context);
		return offerResponse;		
	}

	/**
	 * Separate response , get GUID String.
	 * @param restResponse
	 * @return String
	 */
	private String getGUID(RestResponse restResponse) {
		List<Message> messages = restResponse.getMessages();
		String GUID = "";
		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			String statusCode = message.getStatusCode();
			if (HttpStatusCodes.SC_CREATED == Integer.parseInt(statusCode)) {
				String GUIDTemp = message.getDescription();
				GUID = GUIDTemp.substring(GUIDTemp.lastIndexOf(" ")+1);
				break;
			}
		}
		return GUID;
	}	
	
	public GreenPointsResponse executeCorporateCard(String greenpointsNumber,
			String language, Context context) throws InvalidJsonError,
			JSONException, TimeOutError, RequestFail, IOException, ConnectionError, BookingTimeOutError{
		String httpResponseString = null;
		GreenPointsResponse corporateCardResponse = null;
		try {			 
			//https://api.b-europe.com/greenpoints-lookup/
			String urlString = context.getString(R.string.server_url_lookup_greenpoints);
			urlString = urlString + greenpointsNumber;						
			httpResponseString = httpRestServiceCaller.executeHTTPRequest(context.getApplicationContext(), null
					, urlString ,language, HTTPRestServiceCaller.HTTP_GET_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		} catch (RequestFail e) {
			//Log.d("executeCorporateCard", "RequestFail", e);
			e.printStackTrace();
		}		
		if (httpResponseString == null || "".equals(httpResponseString)) {
			throw new RequestFail();
		}	
		
		corporateCardResponse = converter.parsesCorporateCard(httpResponseString);			
		return corporateCardResponse;
	}
	
}
