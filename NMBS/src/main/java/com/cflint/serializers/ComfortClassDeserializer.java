package com.cflint.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cflint.model.OfferQuery.ComforClass;

public class ComfortClassDeserializer implements JsonDeserializer<ComforClass> {

	public ComforClass deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		if ("SECOND".equals(arg0.getAsString())){
			return ComforClass.SECOND;
		}else if("2".equals(arg0.getAsString())){
			return ComforClass.SECOND;
		}else{
			return ComforClass.FIRST;
		}
	}

}
