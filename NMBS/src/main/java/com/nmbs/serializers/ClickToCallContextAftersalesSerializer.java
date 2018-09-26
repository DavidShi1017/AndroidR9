package com.nmbs.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nmbs.model.ClickToCallAftersalesParameter;
import com.nmbs.model.ClickToCallContext;
import com.nmbs.model.ClickToCallParameter;
import com.nmbs.util.DateUtils;

import java.lang.reflect.Type;


/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:David
 */
public class ClickToCallContextAftersalesSerializer implements JsonSerializer<ClickToCallAftersalesParameter> {

    public JsonElement serialize(ClickToCallAftersalesParameter clickToCallAftersalesParameter, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("Dnr", context.serialize(clickToCallAftersalesParameter.getDnr() == null? "" : clickToCallAftersalesParameter.getDnr()));
        object.add("Control", context.serialize(clickToCallAftersalesParameter.getControl() == null? "" : clickToCallAftersalesParameter.getControl()));
        object.add("Context", context.serialize(clickToCallAftersalesParameter.getContext() == null? "" : clickToCallAftersalesParameter.getContext()));
        if(clickToCallAftersalesParameter.getReturnUrlWithRefresh() != null){
            object.add("ReturnUrlWithRefresh", context.serialize(clickToCallAftersalesParameter.getReturnUrlWithRefresh()));
        }
        if(clickToCallAftersalesParameter.getReturnUrlWithoutRefresh() != null){
            object.add("ReturnUrlWithoutRefresh", context.serialize(clickToCallAftersalesParameter.getReturnUrlWithoutRefresh()));
        }
        return object;
    }

}
