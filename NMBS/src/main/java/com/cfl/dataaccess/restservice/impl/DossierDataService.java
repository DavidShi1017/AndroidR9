package com.cfl.dataaccess.restservice.impl;

import android.content.Context;
import android.util.Log;

import com.cfl.R;
import com.cfl.dataaccess.converters.CustomErrorMessager;
import com.cfl.dataaccess.converters.DossierResponseConverter;
import com.cfl.dataaccess.converters.RealTimeInfoResponseConverter;
import com.cfl.dataaccess.restservice.IDossierDataService;
import com.cfl.exceptions.BookingTimeOutError;
import com.cfl.exceptions.ConnectionError;
import com.cfl.exceptions.CustomError;
import com.cfl.exceptions.DBooking343Error;
import com.cfl.exceptions.DBookingCATError;
import com.cfl.exceptions.DBookingInvalidFtpError;
import com.cfl.exceptions.DBookingNoSeatAvailableError;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.exceptions.NoTicket;
import com.cfl.exceptions.RefreshConirmationError;
import com.cfl.exceptions.RequestFail;
import com.cfl.model.DossierParameter;
import com.cfl.model.DossierResponse;
import com.cfl.model.Message;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.RealTimeInfoResponse;
import com.cfl.model.RestResponse;
import com.cfl.services.impl.DossierService;
import com.cfl.util.DateUtils;
import com.cfl.util.HTTPRestServiceCaller;
import com.cfl.util.HttpStatusCodes;
import com.cfl.util.ObjectToJsonUtils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DossierDataService extends CustomErrorMessager implements IDossierDataService {
	
	//private static final String TAG = DossierDataService.class.getSimpleName();



	/**
	 *  Call web service asynchronously and get DossierResponse.
	 *  @return OfferResponse
	 * @throws NoTicket 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NumberFormatException 
	 * @throws ConnectionError 
	 * @throws DBookingInvalidFtpError 
	 * @throws DBookingNoSeatAvailableError 
	 * @throws DBookingCATError 
	 * @throws CustomError 
	 */
	public DossierResponse executeSearchDossier(DossierParameter dossierParameter, Context context, 
			String languageBeforSetting) throws RequestFail, IOException, InvalidJsonError, 
			NumberFormatException, JSONException, ParseException, NoTicket, ConnectionError, 
			BookingTimeOutError, RefreshConirmationError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		DossierResponse dossierResponse = null;
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		
		if(DossierService.GUID == null || "".equals(DossierService.GUID)){
			//Log.i(TAG, "Create Dossier...... " );
			String postJsonString = ObjectToJsonUtils.getPostTrainSelectionParameterStr(dossierParameter);
			String urlString = context.getString(R.string.server_url_dossier_request);
			    
			String responseDossierGUID = httpRestServiceCaller.executeHTTPRequest(context, postJsonString,urlString ,
					languageBeforSetting, HTTPRestServiceCaller.HTTP_POST_METHOD, 180000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
			
			/*String responseDossierGUID = FileManager.getInstance().readExternalStoragePrivateFile(context, null, "error.json");*/
			RestResponse restResponse = dossierResponseConverter.parseDossierGUID(responseDossierGUID);  
			super.throwErrorMessage(restResponse, context, "");
			DossierService.GUID = getGUID(restResponse);
		}
		//Log.i(TAG, "GUID is? : " + DossierService.GUID);
		String urlStringOfCreateDossier = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID;
		String responseSecond = httpRestServiceCaller.executeHTTPRequest(context, null, urlStringOfCreateDossier, 
				languageBeforSetting, HTTPRestServiceCaller.HTTP_GET_METHOD, 180000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		//Log.i(TAG, "responseSecond is? : " + responseSecond);
		
		//responseSecond = "{\"TotalDossierValue\": {\"Amount\": 81.9,\"CurrencyCode\": \"EUR\"},\"TotalDiscountPrice\": {\"Amount\": 0.0,\"CurrencyCode\": \"EUR\"},\"OriginStationName\": \"Aachen Hbf\",\"OriginStationRCode\": \"DEBDY\",\"DestinationStationName\": \"Aalst\",\"DestinationStationRCode\": \"BEAAL\",\"NrOfPassengers\": 1,\"ComfortClass\": 2,\"IsTwoWay\": true,\"Outbound\": {\"DepartureDateTime\": \"2014-09-26T10:35:00\",\"ArrivalDateTime\": \"2014-09-26T13:17:00\",\"NrOfTransfers\": 3,\"OptionalReservationsIncluded\": false,\"HasLegsWithTicketNotIncluded\": false},\"Inbound\": {\"DepartureDateTime\": \"2014-09-26T16:39:00\",\"ArrivalDateTime\": \"2014-09-26T18:36:00\",\"NrOfTransfers\": 1,\"HasLegsWithTicketNotIncluded\": false},\"DnrId\": \"JCHTDKL\",\"PnrIds\": [\"ENLDCAFG\",\"QYFMSW\"],\"TotalPrice\": {\"Amount\": 81.9,\"CurrencyCode\": \"EUR\"},\"TotalTravelPrice\": { \"Amount\": 81.9,\"CurrencyCode\": \"EUR\"},\"TotalInsurancePrice\": {\"Amount\": 0.0,\"CurrencyCode\": \"EUR\"},\"HasInsurance\": false,\"HasReservations\": true,\"HasOverbookings\": false,\"OrderItemState\": \"Provisional\",\"Passengers\": [{\"Id\": \"AA\",\"PassengerType\": \"A\",\"ReductionCards\": [],\"FtpCards\": []}],\"DeliveryOptions\": [{\"Method\": \"STAU\",\"PaymentOptions\": [{\"Method\": \"VISA\"},{\"Method\": \"ECMC\"},{\"Method\": \"AMEX\"}]}],\"SeatLocations\": [],\"Messages\": [],\"DebugMessages\": []}";
		dossierResponse = dossierResponseConverter.parseDossier(responseSecond);
		super.throwErrorMessage(dossierResponse, context, "");
		return dossierResponse;		
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
	
	public DossierResponse executeUpdateDossier(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError, DBooking343Error, CustomError, DBookingNoSeatAvailableError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = null;
		switch (optionFlag) {
			case DossierService.DOSSIER_UPDATE_INSURANCE_DELIVERYMETHOD:
				postJsonString = ObjectToJsonUtils.getPostUpdateInsuranceAndDeliveryMethodParameterStr(dossierParameter);
				break;
			case DossierService.DOSSIER_UPDATE_CUSTOMER_PAYMENT:
				postJsonString = ObjectToJsonUtils.getPostUpdateCustomerAndPaymentMethodParameterStr(dossierParameter);
				break;
		}
				
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID;
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_POST_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		super.throwErrorMessage(dossierResponse, context, "");
		return dossierResponse;
	}
	
	public DossierResponse executeRefreshPayment(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = null;
		Date date = new Date();
		String dateString = DateUtils.timeToString(date);
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID + "/refresh-payment?id=" + dateString;
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		return dossierResponse;
	}
	
	public DossierResponse executeRefreshConfirmation(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = null;

				
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID + "/refresh-for-confirmation";
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		return dossierResponse;
	}
	public DossierResponse executeCancelDossier(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = null;

				
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID;
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_DELETE_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		return dossierResponse;
	}
	
	public DossierResponse executeAbortPayment(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting, int optionFlag) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError, CustomError, DBooking343Error, DBookingNoSeatAvailableError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = null;

		//Log.i("executeAbortPayment", "executeAbortPayment......");
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID + "/payment";
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_DELETE_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		super.throwErrorMessage(dossierResponse, context, "");
		//Log.i("executeAbortPayment", "dossierResponseConverter......");
		return dossierResponse;
	}
	
	public DossierResponse executeRefreshOrderState(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting,String dossierGUID) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = null;
		Date date = new Date();
		String dateString = DateUtils.timeToString(date);
				
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + dossierGUID + "/refresh-orderstatus?id=" + dateString;
		String response = httpRestServiceCaller.executeHTTPRequest(context,postJsonString, urlString, languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_GET_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		return dossierResponse;
	}
	public Map<String, Object> executeInitPayment(
			DossierParameter dossierParameter, Context context,
			String languageBeforSetting) throws RequestFail, IOException,
			InvalidJsonError, NumberFormatException, JSONException,
			ParseException, NoTicket, ConnectionError, BookingTimeOutError, RefreshConirmationError, CustomError{
		DossierResponse dossierResponse = null;
		HTTPRestServiceCaller httpRestServiceCaller = new HTTPRestServiceCaller();
		String postJsonString = "";
		
				
		String urlString = context.getString(R.string.server_url_dossier_request)+"/" + DossierService.GUID + "/payment";
		String response = httpRestServiceCaller.executeHTTPRequest(context, postJsonString, urlString, 
				languageBeforSetting, 
				HTTPRestServiceCaller.HTTP_POST_METHOD, 50000, false, "", HTTPRestServiceCaller.API_VERSION_VALUE_3);
		
		
		DossierResponseConverter dossierResponseConverter = new DossierResponseConverter();;
		dossierResponse = dossierResponseConverter.parseDossier(response);
		super.throwCustomErrorMessage(dossierResponse, context, true);
		String paymentUrl = null;
		if (dossierResponse != null) {
			paymentUrl = dossierResponse.getPaymentUrl();
		}/*else {
			String paymentUrl = httpRestServiceCaller.getReceiveLocation();
		}*/		
		if (dossierResponse != null) {
			if (dossierResponse.getPaymentUrl() == null || 
					StringUtils.equalsIgnoreCase("", dossierResponse.getPaymentUrl())) {
				throw new RequestFail();
			}
		}
		Map<String, Object> initPaymentResponse = new HashMap<String, Object>();
		initPaymentResponse.put("dossierResponse", dossierResponse);
		initPaymentResponse.put("paymentUrl", paymentUrl);
/*		if (true) {
			throw new BookingTimeOutError();
		}*/
		return initPaymentResponse;
	}
}
