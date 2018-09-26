package com.nmbs.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nmbs.model.TravelRequest;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class TravelRequestJsonSerializer implements JsonSerializer<TravelRequest> {

    public JsonElement serialize(TravelRequest travelRequest, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        
        object.add("DateTime", context.serialize(travelRequest.getDateTime()));
        object.add("TimePreference", context.serialize(travelRequest.getTimePreference()));
        return object;
    }

}
