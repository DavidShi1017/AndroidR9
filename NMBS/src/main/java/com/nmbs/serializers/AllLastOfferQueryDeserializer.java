package com.nmbs.serializers;

import java.lang.reflect.Type;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.nmbs.model.AllLastOfferQuery;



public class AllLastOfferQueryDeserializer implements JsonSerializer<AllLastOfferQuery> {

    public JsonElement serialize(AllLastOfferQuery allLastOfferQuery, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("LastOfferQueries", context.serialize(allLastOfferQuery.getLastOfferQueries()));
        
        return object;
    }

}