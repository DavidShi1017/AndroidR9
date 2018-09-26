package com.nmbs.dataaccess.converters;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nmbs.exceptions.InvalidJsonError;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierResponse.OrderItemStateType;
import com.nmbs.model.PartyMember.PersonType;
import com.nmbs.model.SeatLocationForOD.Direction;
import com.nmbs.model.StationBoardResponse;
import com.nmbs.model.StationDetailResponse;
import com.nmbs.serializers.DirectionDeserializer;
import com.nmbs.serializers.OrderItemStateDeserializer;
import com.nmbs.serializers.PersonTypeDeserializer;

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