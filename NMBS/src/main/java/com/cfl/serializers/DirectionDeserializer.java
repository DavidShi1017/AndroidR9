package com.cfl.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.cfl.model.SeatLocationForOD.Direction;

public class DirectionDeserializer implements JsonDeserializer<Direction> {

	public Direction deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		

		if ("Unkown".equals(arg0.getAsString())){
			return Direction.unknown;
		}else if("SingleTravel".equals(arg0.getAsString())){
			return Direction.Single;
		}else if("OutboundTravel".equals(arg0.getAsString())){
			return Direction.Outward;
		}else if("InboundTravel".equals(arg0.getAsString())){
			return Direction.Return;
		}			
		return null;
	}

}
