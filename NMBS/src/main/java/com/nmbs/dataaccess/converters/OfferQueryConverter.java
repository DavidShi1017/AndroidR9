package com.nmbs.dataaccess.converters;

import org.json.JSONException;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nmbs.exceptions.InvalidJsonError;


import com.nmbs.model.OfferQuery;
import com.nmbs.model.PartyMember;
import com.nmbs.model.OfferQuery.ComforClass;
import com.nmbs.model.OfferQuery.TravelType;
import com.nmbs.model.PartyMember.PersonType;
import com.nmbs.serializers.ComfortClassDeserializer;

import com.nmbs.serializers.PersonTypeDeserializer;
import com.nmbs.serializers.TravelTypeDeserializer;

public class OfferQueryConverter {

	public OfferQuery parseOfferQuery(String jsonString) throws JSONException, InvalidJsonError{
		

		//Log.d("OfferQueryConverter", "Starting..." + jsonString);
		GsonBuilder gsonBuilder = new GsonBuilder();
		
	    gsonBuilder.registerTypeAdapter(ComforClass.class, new ComfortClassDeserializer());
	    gsonBuilder.registerTypeAdapter(TravelType.class, new TravelTypeDeserializer());
	    gsonBuilder.registerTypeAdapter(PersonType.class, new PersonTypeDeserializer());
	    Gson gson = gsonBuilder.create();

		
	    OfferQuery offerQuery = gson.fromJson(jsonString, OfferQuery.class);

			
		if (offerQuery == null) {
				throw new InvalidJsonError();
		}else {
			
			if(offerQuery.getTravelPartyMembers() != null){
				for (PartyMember partyMember : offerQuery.getTravelPartyMembers()) {
					switch (partyMember.getPersonType().ordinal()) {
					case 0:
						
						partyMember.setPartMemberSortorderField(1);
						break;
					case 1:
						
						partyMember.setPartMemberSortorderField(2);
						break;
					case 2:
						
						partyMember.setPartMemberSortorderField(3);
						break;
					case 3:
						
						partyMember.setPartMemberSortorderField(5);
						break;
					case 4:
						partyMember.setPersonType(PersonType.YOUTH);
						partyMember.setPartMemberSortorderField(6);
						break;
					case 5:
						
						partyMember.setPartMemberSortorderField(6);
						break;
					case 6:
						
						partyMember.setPartMemberSortorderField(7);
						break;
					case 7:
						  
						partyMember.setPartMemberSortorderField(4);
						break;
					default:
						
						partyMember.setPartMemberSortorderField(7);
						break;
					}
					
					offerQuery.rePartyMemberCount(partyMember.getPersonType());
				}
			}
			
			return offerQuery;
		}
			
	}
}
