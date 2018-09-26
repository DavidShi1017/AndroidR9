package com.nmbs.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nmbs.model.RealTimeInfoRequestParameter;

import java.lang.reflect.Type;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class RealTimeInfoJsonSerializer implements JsonSerializer<RealTimeInfoRequestParameter> {

	public RealTimeInfoJsonSerializer(){
	}

    public JsonElement serialize(RealTimeInfoRequestParameter realTimeInfoRequest, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject object1 = new JsonObject();
        JsonObject object2 = new JsonObject();
        object1.add("mytest",context.serialize("asdfasd"));
        object2.add("mytest2",context.serialize("asdfas12312d"));
        array.add(object1);
        array.add(object2);
        object.add("test",context.serialize(array));
        return object;
    }
}
