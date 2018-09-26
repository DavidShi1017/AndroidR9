package com.nmbs.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nmbs.model.DossierParameter;



/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Tony
 */
public class UpdateCustomerAndPaymentMethodOfDossierParameterSerializer implements JsonSerializer<DossierParameter> {

    public JsonElement serialize(DossierParameter dossierParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("Customer", context.serialize(dossierParameter.getCustomer()));
        object.add("PaymentMethod", context.serialize(dossierParameter.getPaymentMethod()));
       
        return object;
    }
}

