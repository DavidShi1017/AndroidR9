package com.nmbs.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nmbs.model.DossierResponse.ComfortClass;

public class ResponseComfortClassDeserializer implements JsonDeserializer<ComfortClass> {

	public ComfortClass deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		if ("SECOND".equals(arg0.getAsString())){
			return ComfortClass.SECOND;
		}else if("2".equals(arg0.getAsString())){
			return ComfortClass.SECOND;
		}else{
			return ComfortClass.FIRST;
		}
	}

}
