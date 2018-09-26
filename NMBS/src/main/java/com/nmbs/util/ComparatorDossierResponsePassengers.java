package com.nmbs.util;

import java.util.Comparator;


import com.nmbs.model.Passenger;

public class ComparatorDossierResponsePassengers implements Comparator<Passenger>{

	public int compare(Passenger lhs, Passenger rhs) {		
		
		return rhs.getPassengerSortorderField().compareTo(lhs.getPassengerSortorderField());
	}

}
