package com.cflint.dataaccess.converters;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.model.Dossier;
import com.cflint.model.DossierAftersalesResponse;
import com.cflint.model.DossierDetailsResponse;
import com.cflint.model.DossierResponse.OrderItemStateType;
import com.cflint.model.PartyMember.PersonType;
import com.cflint.model.SeatLocationForOD.Direction;
import com.cflint.model.StationBoardResponse;
import com.cflint.model.StationDetailResponse;
import com.cflint.serializers.DirectionDeserializer;
import com.cflint.serializers.OrderItemStateDeserializer;
import com.cflint.serializers.PersonTypeDeserializer;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parse JsonString to Object.
 * 
 */
public class DossierDetailConverter {

	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return DossierAftersalesResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public DossierDetailsResponse parsesDossierDetailResponse(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			GsonBuilder builder = new GsonBuilder();

			registerTypeAdapterForDate(builder);

			Gson gson = builder.create();
			DossierDetailsResponse dossier = gson.fromJson(jsonString, DossierDetailsResponse.class);

			//Log.d("dossier is null? ", String.valueOf(dossier == null));

			if (dossier != null && dossier.getDossier() != null){
				//Log.d("dossier is null? ", dossier.getDossier().getDossierId());
			}
			return dossier;
			
		} catch (Exception e) {
			e.printStackTrace();
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