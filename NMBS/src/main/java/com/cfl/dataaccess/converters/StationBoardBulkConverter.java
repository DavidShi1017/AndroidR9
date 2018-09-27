package com.cfl.dataaccess.converters;

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
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.model.StationBoardBulkResponse;


public class StationBoardBulkConverter {

	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return StationBoardResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public StationBoardBulkResponse parsesStationBoardBulk(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			//Gson gson = new Gson();
			GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();
			StationBoardBulkResponse stationBoardBulkResponse = gson.fromJson(jsonString, StationBoardBulkResponse.class);

			return stationBoardBulkResponse;
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

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
