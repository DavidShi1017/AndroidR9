package com.nmbs.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nmbs.model.DeliveryMethodParameter;



/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:David
 */
public class UpdateIsuranceAndDeliveryMethodPrintWaySerializer implements JsonSerializer<DeliveryMethodParameter> {

    public JsonElement serialize(DeliveryMethodParameter deliveryMethodParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("Method", context.serialize(deliveryMethodParameter.getMethod()));
        object.add("Passengers", context.serialize(deliveryMethodParameter.getPassengers()));       
        return object;
    }
}
