package com.cflint.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cflint.model.CustomerParameter;



/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Tony
 */
public class UpdateCustomerSerializer implements JsonSerializer<CustomerParameter> {

    public JsonElement serialize(CustomerParameter customerParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("Gender", context.serialize(customerParameter.getGender()));
        object.add("Salutation", context.serialize(customerParameter.getSalutation()));        
        object.add("FirstName", context.serialize(customerParameter.getFirstName()));
        object.add("LastName", context.serialize(customerParameter.getLastName()));
        object.add("Email", context.serialize(customerParameter.getEmail()));
        object.add("AdditionalEmails", context.serialize(customerParameter.getAdditionalEmails()));
        object.add("TelephoneNumberForSMS", context.serialize(customerParameter.getTelephoneNumberForSMS()));
        object.add("CustomPaymentServiceProviderTemplate", context.serialize(customerParameter.getCustomPaymentServiceProviderTemplate()));

        return object;
    }
}

