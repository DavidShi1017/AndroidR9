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
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.model.StationBoardLastQuery;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.StationBoardResponse;

public class StationBoardConverter {

	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return StationBoardResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public StationBoardResponse parsesStationBoard(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			//Gson gson = new Gson();
			GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();
			StationBoardResponse stationBoardResponse = gson.fromJson(jsonString, StationBoardResponse.class);

			if (stationBoardResponse.getStationBoardRows() == null) {
				throw new InvalidJsonError();
			} else {
				return stationBoardResponse;
			}
		} catch (Exception e) {
			throw new InvalidJsonError();
		}

	}
	public StationBoardLastQuery parsesStationBoardLastQuery(String jsonString) throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}
		try {
			GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();
			StationBoardLastQuery stationBoardLastQuery = gson.fromJson(jsonString, StationBoardLastQuery.class);
			if (stationBoardLastQuery.getStationRCode() == null) {
				throw new InvalidJsonError();
			} else {
				return stationBoardLastQuery;
			}
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
