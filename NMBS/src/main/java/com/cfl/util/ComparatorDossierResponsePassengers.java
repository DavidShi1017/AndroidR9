package com.cfl.util;

import java.util.Comparator;


import com.cfl.model.Passenger;

public class ComparatorDossierResponsePassengers implements Comparator<Passenger>{

	public int compare(Passenger lhs, Passenger rhs) {		
		
		return rhs.getPassengerSortorderField().compareTo(lhs.getPassengerSortorderField());
	}

}
