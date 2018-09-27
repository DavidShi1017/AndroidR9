package com.cfl.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cfl.model.CorporateCard;


/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:DAVID
 */
public class CorporateCardJsonSerializer implements JsonSerializer<CorporateCard> {

    public JsonElement serialize(CorporateCard corporateCard, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.add("CardType", context.serialize(corporateCard.getCardType()));
        object.add("CardNumber", context.serialize(corporateCard.getCardNumber()));       
        return null;
    }

}

