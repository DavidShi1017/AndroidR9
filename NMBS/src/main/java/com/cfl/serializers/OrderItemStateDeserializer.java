package com.cfl.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.cfl.model.DossierResponse.OrderItemStateType;

public class OrderItemStateDeserializer implements JsonDeserializer<OrderItemStateType> {

	public OrderItemStateType deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		// TODO Auto-generated method stub

		if ("Provisional".equals(arg0.getAsString())){
			return OrderItemStateType.OrderItemStateTypeProvisional;
		}else if("Cancelled".equals(arg0.getAsString())){
			return OrderItemStateType.OrderItemStateTypeCancelled;
		}else if("Confirmed".equals(arg0.getAsString())){
			return OrderItemStateType.OrderItemStateTypeConfirmed;
		}else if("Unknown".equals(arg0.getAsString())){
			return OrderItemStateType.OrderItemStateTypeUnknown;
		}			
		return null;
	}

}
