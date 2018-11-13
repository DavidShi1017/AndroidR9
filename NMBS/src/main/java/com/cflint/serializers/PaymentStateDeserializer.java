package com.cflint.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.cflint.model.DossierResponse.PaymentState;

public class PaymentStateDeserializer implements JsonDeserializer<PaymentState> {

	public PaymentState deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
	
		if ("InProgress".equals(arg0.getAsString()))
			return PaymentState.InProgress;
		else if("Success".equals(arg0.getAsString())){
			return PaymentState.Success;
		}else{
			return PaymentState.Failure;
		}		
	}

}
