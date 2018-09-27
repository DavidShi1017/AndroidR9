package com.cfl.util;

import java.util.Comparator;

import com.cfl.model.OfferQuery;


/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorOfferQueryDate implements Comparator<OfferQuery> {



	public int compare(OfferQuery offerQuery1, OfferQuery offerQuery2) {

		return offerQuery2.getLastUsedDate().compareTo(offerQuery1.getLastUsedDate());		

	}

}
