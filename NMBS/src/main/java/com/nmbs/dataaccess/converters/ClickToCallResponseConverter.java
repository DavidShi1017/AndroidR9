package com.nmbs.dataaccess.converters;




import android.util.Log;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.nmbs.exceptions.InvalidJsonError;

import com.nmbs.model.ClickToCallAftersalesResponse;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.RestResponse;

import org.json.JSONException;


public class ClickToCallResponseConverter {
	/**
	 * Converter JSON String to RestResponse by GSON.
	 * 
	 * @param jsonString
	 * @return RestResponse
	 * @throws InvalidJsonError
	 */
	public RestResponse parseClickToCall(String jsonString)
			throws InvalidJsonError {

		RestResponse restResponse;
		//Log.d("ClickToCallResponseConverter", "Starting...");
		Gson gson = new Gson();
		try {
			restResponse = gson.fromJson(jsonString, RestResponse.class);
		} catch (JsonParseException e) {
			throw new InvalidJsonError();
		}
		
		//Log.d("ClickToCallResponseConverter", "End...");

		if (restResponse.getMessages().size() > 0) {
			throw new InvalidJsonError();
		} else {
			return restResponse;
		}
	}


	public ClickToCallAftersalesResponse parsesClickToCallAftersalesResponse(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			GsonBuilder builder = new GsonBuilder();


			Gson gson = builder.create();
			ClickToCallAftersalesResponse aftersalesResponse = gson.fromJson(jsonString, ClickToCallAftersalesResponse.class);

			//Log.d("dossier is null? ", String.valueOf(dossier == null));

			return aftersalesResponse;

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidJsonError();
		}

	}
	
}
