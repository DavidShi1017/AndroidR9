package com.cfl.dataaccess.converters;

import org.json.JSONException;
import com.google.gson.Gson;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.model.GreenPointsResponse;

/**
 * Parse JsonString to Object.
 * 
 */
public class CorporateCardResponseConverter {

	
	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return CorporateCardResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public GreenPointsResponse parsesCorporateCard(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			Gson gson = new Gson();
			/*GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();*/
			GreenPointsResponse corporateCardResponse = gson.fromJson(jsonString, GreenPointsResponse.class);
			if (corporateCardResponse.getCorporateCards() == null) {
				throw new InvalidJsonError();
			} else {
				return corporateCardResponse;				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidJsonError();
		}

	}

}