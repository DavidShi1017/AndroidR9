package com.nmbs.serializers;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.nmbs.model.Promo;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class PromoCodeJsonSerializer implements JsonSerializer<Promo> {

    public JsonElement serialize(Promo promo, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        if (promo != null) {
        	if (promo.getCode() != null && !StringUtils.isEmpty(promo.getCode())) {
        		object.add("Code", context.serialize(promo.getCode()));
			}        	
            object.add("AddPromotion", context.serialize(promo.isAddpromotion()));
		}
        
        return object;
    }
}
