package com.cflint.dataaccess.converters;

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

import com.cflint.exceptions.DBookingCATError;
import com.cflint.exceptions.DBookingInvalidFtpError;
import com.cflint.exceptions.DBookingNoSeatAvailableError;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.exceptions.RefreshConirmationError;
import com.cflint.model.DossierResponse;

import com.cflint.model.DossierResponse.ComfortClass;



import com.cflint.model.DossierResponse.OrderItemStateType;
import com.cflint.model.DossierResponse.PaymentState;
import com.cflint.model.PartyMember.PersonType;

import com.cflint.model.RestResponse;
import com.cflint.serializers.OrderItemStateDeserializer;
import com.cflint.serializers.PaymentStateDeserializer;
import com.cflint.serializers.PersonTypeDeserializer;
import com.cflint.serializers.ResponseComfortClassDeserializer;

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
