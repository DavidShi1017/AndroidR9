package com.nmbs.serializers;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nmbs.model.PartyMember;

/**
 * Object to Json , Make the Json format comply with requirement. 
 *@author:Alice
 */
public class PartyMemberJsonSerializer implements JsonSerializer<PartyMember> {

    public JsonElement serialize(PartyMember partyMember, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("PersonType", context.serialize(partyMember.getPersonType()));
        if (partyMember.getEurostarFtpCard() != null && StringUtils.isNotEmpty(partyMember.getEurostarFtpCard())) {
        	object.add("EurostarFtpCard", context.serialize(partyMember.getEurostarFtpCard()));
		}
        if (partyMember.getThalysFtpCard() != null && StringUtils.isNotEmpty(partyMember.getThalysFtpCard())) {
        	 object.add("ThalysFtpCard", context.serialize(partyMember.getThalysFtpCard()));
		}
       
        
        if (partyMember.getReductionCards() != null && partyMember.getReductionCards().size() > 0) {
			if (StringUtils.isEmpty(partyMember.getReductionCards().get(0).getType())) {
				 //object.add("ReductionCards", context.serialize(partyMember.getReductionCards()));
			}else {
				object.add("ReductionCards", context.serialize(partyMember.getReductionCards()));
			}
		}
        
       // object.add("ReductionCards", context.serialize(partyMember.getReductionCards()));
        return object;
    }

}
