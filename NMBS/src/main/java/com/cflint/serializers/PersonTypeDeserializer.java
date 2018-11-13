package com.cflint.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.cflint.model.PartyMember.PersonType;

public class PersonTypeDeserializer implements JsonDeserializer<PersonType> {

	public PersonType deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {

		if ("KID0".equals(arg0.getAsString()) || "0".equals(arg0.getAsString())){
			return PersonType.KID0;
		}else if("KID4".equals(arg0.getAsString()) || "4".equals(arg0.getAsString())){
			return PersonType.KID4;
		}else if ("KID6".equals(arg0.getAsString()) || "6".equals(arg0.getAsString())) {
			return PersonType.KID6;
		}else if ("KID12".equals(arg0.getAsString()) || "12".equals(arg0.getAsString())){
			return PersonType.KID12;
		}else if("KID15".equals(arg0.getAsString())  || "16".equals(arg0.getAsString())){
			return PersonType.KID15;
		}else if ("YOUTH".equals(arg0.getAsString()) || "Y".equals(arg0.getAsString())) {
			return PersonType.YOUTH;
		}else if ("ADULT".equals(arg0.getAsString()) || "A".equals(arg0.getAsString())){
			return PersonType.ADULT;
		}else if("SENIOR".equals(arg0.getAsString()) || "S".equals(arg0.getAsString())){
			return PersonType.SENIOR;
		}else {
			return PersonType.ADULT;
		}		
	}

}
