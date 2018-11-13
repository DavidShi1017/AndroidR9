package com.cflint.util;

import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;

import java.util.Comparator;

/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorDossierDate implements Comparator<DossierSummary> {


	public static final int ASC = 0;
	public static final int DESC = 0;
	private int sort;
	public ComparatorDossierDate(int sort) {
		this.sort = sort;
	}

	public int compare(DossierSummary dossierSummary1, DossierSummary dossierSummary2) {
		if(sort == ASC){
			return dossierSummary1.getEarliestTravel().compareTo(dossierSummary2.getEarliestTravel());
		}else{
			return dossierSummary2.getEarliestTravel().compareTo(dossierSummary1.getEarliestTravel());
		}

	}

}
