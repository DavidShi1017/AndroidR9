package com.cflint.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.cflint.model.Account;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class AccountJsonSerializer implements JsonSerializer<Account> {

    public JsonElement serialize(Account account, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("EmailAddress", context.serialize(account.getEmailAddress()));
        object.add("Password", context.serialize(account.getPassword()));
        object.add("Salutation", context.serialize(account.getSalutation()));
        object.add("FirstName", context.serialize(account.getFirstName()));
        object.add("LastName", context.serialize(account.getLastName()));
        object.add("DateOfBirth", context.serialize(account.getDateOfBirth()));
        object.add("Language", context.serialize(account.getLanguage()));
        object.add("TelephoneNumber", context.serialize(account.getTelephoneNumber()));
        object.add("Mobile", context.serialize(account.getMobile()));
        object.add("Street", context.serialize(account.getStreet()));
        object.add("HouseNumber", context.serialize(account.getHouseNumber()));
        object.add("PostBox", context.serialize(account.getPostBox()));
        object.add("ZipCode", context.serialize(account.getZipCode()));
        object.add("City", context.serialize(account.getCity()));
        object.add("Country", context.serialize(account.getCountry()));
        return object;
    }
}
