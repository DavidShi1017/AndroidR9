package com.nmbs.dataaccess.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.exceptions.NoTicket;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.RealTimeConnectionResponse;
import com.nmbs.model.RestResponse;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Parse JsonString to Object. 
 *
 */
public class RealTimeConnectionConverter {

	/**
	 * @return value: OfferResponse
	 * @throws ParseException
	 * @throws NumberFormatException
	 * @throws NoTicket
	 * @Description Parse json to OfferResponse
	 */
	public RealTimeConnectionResponse parseConnection(String jsonString) throws JSONException, InvalidJsonError{
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}
		try {
			GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();
			RealTimeConnectionResponse realTimeConnectionResponse = gson.fromJson(jsonString, RealTimeConnectionResponse.class);
			return realTimeConnectionResponse;
		} catch (Exception e) {
			throw new InvalidJsonError();
		}
	}
	public RealTimeConnection parseRealTimeConnection(String jsonString) throws JSONException, InvalidJsonError{
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}
		try {
			GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();
			RealTimeConnection realTimeConnection = gson.fromJson(jsonString, RealTimeConnection.class);
			return realTimeConnection;
		} catch (Exception e) {
			throw new InvalidJsonError();
		}
	}

	/**
	 * Converter JSON String to RestResponse by GSON.
	 * @param jsonString
	 * @return RestResponse
	 * @throws InvalidJsonError
	 */
	public RestResponse parseRefreshConnection(String jsonString) throws InvalidJsonError{
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


	private void registerTypeAdapterForDate(GsonBuilder builder){
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			public Date deserialize(JsonElement json, Type typeOfT,
									JsonDeserializationContext context)
					throws JsonParseException {

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String date = (json.getAsJsonPrimitive().getAsString().replace(
						"T", " "));
				if(date.indexOf("+") != -1){

					date = date.substring(0, date.indexOf("+"));
				}
				if (date.indexOf("-") == -1) {
					format = new SimpleDateFormat(
							"HH:mm:ss");
				}
				//Log.e("date", "==" + date);
				try {
					return format.parse(date);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}