package com.cfl.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cfl.model.AdditionalConnectionsParameter;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class AdditionalConnectionsParameterSerializer implements JsonSerializer<AdditionalConnectionsParameter> {

    public JsonElement serialize(AdditionalConnectionsParameter additionalConnectionsParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("TravelId", context.serialize(additionalConnectionsParameter.getTravelId()));
        object.add("Direction", context.serialize(additionalConnectionsParameter.getDirection().ordinal()));
        return object;
    }
}
