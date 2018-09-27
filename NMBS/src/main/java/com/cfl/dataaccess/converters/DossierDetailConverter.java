package com.cfl.dataaccess.converters;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.model.Dossier;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.DossierDetailsResponse;
import com.cfl.model.DossierResponse.OrderItemStateType;
import com.cfl.model.PartyMember.PersonType;
import com.cfl.model.SeatLocationForOD.Direction;
import com.cfl.model.StationBoardResponse;
import com.cfl.model.StationDetailResponse;
import com.cfl.serializers.DirectionDeserializer;
import com.cfl.serializers.OrderItemStateDeserializer;
import com.cfl.serializers.PersonTypeDeserializer;

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