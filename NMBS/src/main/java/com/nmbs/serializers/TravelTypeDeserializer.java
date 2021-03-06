package com.nmbs.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.nmbs.model.OfferQuery.TravelType;

public class TravelTypeDeserializer implements JsonDeserializer<TravelType> {

	public TravelType deserialize(JsonElement arg0, Type arg1,
			
			JsonDeserializationContext arg2) throws JsonParseException {
		if ("ONEWAY".equals(arg0.getAsString())){
			return TravelType.ONEWAY;
		}else {
			return TravelType.RETURN;
		}
	}
}
