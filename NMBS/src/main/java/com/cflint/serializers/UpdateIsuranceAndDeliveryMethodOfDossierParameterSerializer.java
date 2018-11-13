package com.cflint.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cflint.model.DossierParameter;



/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:David
 */
public class UpdateIsuranceAndDeliveryMethodOfDossierParameterSerializer implements JsonSerializer<DossierParameter> {

    public JsonElement serialize(DossierParameter dossierParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("Insurance", context.serialize(dossierParameter.isInsurance()));
        object.add("DeliveryMethod", context.serialize(dossierParameter.getDeliveryMethod()));
      
        return object;
    }
}

