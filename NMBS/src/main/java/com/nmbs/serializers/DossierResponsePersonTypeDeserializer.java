package com.nmbs.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.nmbs.model.PartyMember.PersonType;

public class DossierResponsePersonTypeDeserializer implements JsonDeserializer<PersonType> {

	public PersonType deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {

		if ("0".equals(arg0.getAsString())){
			return PersonType.KID0;
		}else if("4".equals(arg0.getAsString())){
			return PersonType.KID4;
		}else if ("6".equals(arg0.getAsString())) {
			return PersonType.KID6;
		}else if ("12".equals(arg0.getAsString())){
			return PersonType.KID12;
		}else if("15".equals(arg0.getAsString())){
			return PersonType.KID15;
		}else if ("Y".equals(arg0.getAsString())) {
			return PersonType.YOUTH;
		}else if ("A".equals(arg0.getAsString())){
			return PersonType.ADULT;
		}else if("S".equals(arg0.getAsString())){
			return PersonType.SENIOR;
		}else {
			return PersonType.ADULT;
		}		
	}

}
