package com.cfl.util;

import java.util.Comparator;

import com.cfl.model.PartyMember;

public class ComparatorPassengers implements Comparator<PartyMember>{

	public int compare(PartyMember lhs, PartyMember rhs) {		
		
		return rhs.getPartMemberSortorderField().compareTo(lhs.getPartMemberSortorderField());
	}

}
