package com.cfl.serializers;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class DateDeserializer implements JsonDeserializer<Date> {

	public java.util.Date deserialize(JsonElement json, Type typeOfT,

	JsonDeserializationContext context) throws JsonParseException {
		final java.text.DateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		if (!(json instanceof JsonPrimitive)) {
			throw new JsonParseException("The date should be a string value");
		}

		try {
			Date date = format.parse(json.getAsString().replace("T", ""));
			return date;
		} catch (ParseException e) {
			throw new JsonParseException(e);
		}
	}

}