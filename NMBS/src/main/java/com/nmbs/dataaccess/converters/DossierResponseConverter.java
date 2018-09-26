package com.nmbs.dataaccess.converters;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.nmbs.exceptions.DBookingCATError;
import com.nmbs.exceptions.DBookingInvalidFtpError;
import com.nmbs.exceptions.DBookingNoSeatAvailableError;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.RefreshConirmationError;
import com.nmbs.model.DossierResponse;

import com.nmbs.model.DossierResponse.ComfortClass;



import com.nmbs.model.DossierResponse.OrderItemStateType;
import com.nmbs.model.DossierResponse.PaymentState;
import com.nmbs.model.PartyMember.PersonType;

import com.nmbs.model.RestResponse;
import com.nmbs.serializers.OrderItemStateDeserializer;
import com.nmbs.serializers.PaymentStateDeserializer;
import com.nmbs.serializers.PersonTypeDeserializer;
import com.nmbs.serializers.ResponseComfortClassDeserializer;

public class DossierResponseConverter extends CustomErrorMessager{
	/**
	 * Converter JSON String to RestResponse by GSON.
	 * 
	 * @param jsonString
	 * @return RestResponse
	 * @throws InvalidJsonError
	 * @throws DBookingCATError 
	 * @throws DBookingNoSeatAvailableError 
	 * @throws DBookingInvalidFtpError 
	 */
	public RestResponse parseDossierGUID( String jsonString)
			throws InvalidJsonError{

		RestResponse restResponse;
		// Log.d("DossierResponseConverter", "Starting...");
		Gson gson = new Gson();
		try {
			restResponse = gson.fromJson(jsonString, RestResponse.class);
		} catch (JsonParseException e) {
			throw new InvalidJsonError();
		}
		// Log.d("DossierResponseConverter", "End...");
		if (restResponse.getMessages() == null) {
			throw new InvalidJsonError();
		} else {
			return restResponse;
		}
	}

	/**
	 * Converter JSON String to Currency by GSON.
	 * 
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public DossierResponse parseDossier(String jsonString)
			throws JSONException, InvalidJsonError, JsonParseException,
			RefreshConirmationError {

		// Log.d("parseJsonAndCreateModel", "Starting...");
		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(OrderItemStateType.class, new OrderItemStateDeserializer());
		gsonBuilder.registerTypeAdapter(PaymentState.class, new PaymentStateDeserializer());
		gsonBuilder.registerTypeAdapter(PersonType.class, new PersonTypeDeserializer());
		gsonBuilder.registerTypeAdapter(ComfortClass.class, new ResponseComfortClassDeserializer());
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			public Date deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context)
					throws JsonParseException {

				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String date = (json.getAsJsonPrimitive().getAsString().replace("T", " "));
				try {
					return format.parse(date);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Gson gson = gsonBuilder.create();
		DossierResponse dossierResponse = null;
		try {

			dossierResponse = gson.fromJson(jsonString, DossierResponse.class);
	
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new InvalidJsonError();
		}
/*		if (dossierResponse != null && dossierResponse.getMessages() != null
				&& dossierResponse.getMessages().size() > 0) {
			for (Message message : dossierResponse.getMessages()) {
				if (StringUtils.equalsIgnoreCase(message.getStatusCode(), "510")) {
					throw new RefreshConirmationError();
				}
			}

		}*/

/*		if (dossierResponse.getDnrId() == null) {
			throw new InvalidJsonError();
		}*/

		return dossierResponse;

	}
}
