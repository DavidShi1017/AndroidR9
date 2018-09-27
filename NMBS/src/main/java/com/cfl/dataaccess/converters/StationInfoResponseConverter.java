package com.cfl.dataaccess.converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cfl.model.StationInfoResponse;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StationInfoResponseConverter {
	public StationInfoResponse parseStationInfo(String jsonResponse) throws Exception{
		StationInfoResponse stationInfoResponse = null;
		try {
			GsonBuilder builder = new GsonBuilder();
			registerTypeAdapterForDate(builder);
			Gson gson = builder.create();
			stationInfoResponse = gson.fromJson(jsonResponse, StationInfoResponse.class);
			//System.out.println(stationInfoResponse.getStations());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
		return stationInfoResponse;
	}


	private void registerTypeAdapterForDate(GsonBuilder builder){
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			public Date deserialize(JsonElement json, Type typeOfT,
									JsonDeserializationContext context)
					throws JsonParseException {

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd");
				String date = (json.getAsJsonPrimitive().getAsString());
				if (!StringUtils.isEmpty(date)) {
					date = date.replace("T", " ");
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
				return null;
			}
		});
	}
}
