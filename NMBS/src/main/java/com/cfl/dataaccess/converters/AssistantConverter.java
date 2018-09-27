package com.cfl.dataaccess.converters;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cfl.exceptions.InvalidJsonError;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.StationBoardResponse;
import com.cfl.model.StationDetailResponse;
import com.cfl.model.DossierResponse.OrderItemStateType;
import com.cfl.model.PartyMember.PersonType;
import com.cfl.model.SeatLocationForOD.Direction;
import com.cfl.serializers.DirectionDeserializer;
import com.cfl.serializers.OrderItemStateDeserializer;
import com.cfl.serializers.PersonTypeDeserializer;

/**
 * Parse JsonString to Object.
 * 
 */
public class AssistantConverter {

	
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

	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return StationDetailResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public StationDetailResponse parsesStationDetail(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			Gson gson = new Gson();
			/*GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();*/
			StationDetailResponse stationDetailResponse = gson.fromJson(jsonString, StationDetailResponse.class);

			
			if (stationDetailResponse.getStationDetail() == null) {
				throw new InvalidJsonError();
			} else {
				return stationDetailResponse;				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidJsonError();
		}

	}

	public StationDetailResponse parsesStationDetailQuery(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			Gson gson = new Gson();
			/*GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();*/
			StationDetailResponse stationDetailResponse = gson.fromJson(jsonString, StationDetailResponse.class);


			if (stationDetailResponse.getStationDetail() == null) {
				throw new InvalidJsonError();
			} else {
				return stationDetailResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidJsonError();
		}

	}
	
	/**
	 * Parse JsonString to object
	 * 
	 * @param jsonString
	 * @return DossierAftersalesResponse
	 * @throws JSONException
	 * @throws InvalidJsonError
	 */
	public DossierAftersalesResponse parsesDossierAftersalesResponse(String jsonString)
			throws JSONException, InvalidJsonError {
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		}

		try {
			GsonBuilder builder = new GsonBuilder();
			
			builder.registerTypeAdapter(OrderItemStateType.class, new OrderItemStateDeserializer());
			builder.registerTypeAdapter(Direction.class, new DirectionDeserializer());
			builder.registerTypeAdapter(PersonType.class, new PersonTypeDeserializer());
			registerTypeAdapterForDate(builder);

			Gson gson = builder.create();
			DossierAftersalesResponse dossierAftersalesResponse = gson.fromJson(jsonString, DossierAftersalesResponse.class);

			//Log.d("dossierAftersalesResponse is null? ", String.valueOf(dossierAftersalesResponse == null));

			
				
			return dossierAftersalesResponse;
			
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