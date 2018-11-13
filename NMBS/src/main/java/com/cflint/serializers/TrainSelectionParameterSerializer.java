package com.cflint.serializers;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.cflint.model.TrainSelectionParameter;


/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:David
 */
public class TrainSelectionParameterSerializer implements JsonSerializer<TrainSelectionParameter> {

    public JsonElement serialize(TrainSelectionParameter trainSelectionParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        
        object.add("Departure", context.serialize(trainSelectionParameter.getDepartureOfferId()));
        object.add("AddOptionalReservationsForDeparture", context.serialize(trainSelectionParameter.isAddOptionalReservationsForDeparture()));
        if(!StringUtils.equalsIgnoreCase("", trainSelectionParameter.getReturnOfferId())){
            object.add("Return", context.serialize(trainSelectionParameter.getReturnOfferId()));
            object.add("AddOptionalReservationsForReturn", context.serialize(trainSelectionParameter.isAddOptionalReservationsForReturn()));
        }
        object.add("OfferQueryId", context.serialize(trainSelectionParameter.getOfferQueryId()));
        
        return object;
    }
}
