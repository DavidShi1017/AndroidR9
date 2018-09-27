package com.cfl.dataaccess.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.model.CheckAppUpdate;
import com.cfl.model.DossiersUpToDateResponse;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DossierUpToDateConverter {

	public DossiersUpToDateResponse parse(String jsonString) throws JSONException, InvalidJsonError{
		

		GsonBuilder gsonBuilder = new GsonBuilder();
		registerTypeAdapterForDate(gsonBuilder);
	    Gson gson = gsonBuilder.create();

		DossiersUpToDateResponse dossiersUpToDateResponse = gson.fromJson(jsonString, DossiersUpToDateResponse.class);
			
		if (dossiersUpToDateResponse == null || dossiersUpToDateResponse.getElements() == null) {
				throw new InvalidJsonError();
		}
		return dossiersUpToDateResponse;
	}

	private void registerTypeAdapterForDate(GsonBuilder builder){
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = (json.getAsJsonPrimitive().getAsString().replace("T", " "));
				if(date.indexOf("+") != -1){
					date = date.substring(0, date.indexOf("+"));
				}
				if (date.indexOf("-") == -1) {
					format = new SimpleDateFormat("HH:mm:ss");
				}
				try {
					return format.parse(date);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
