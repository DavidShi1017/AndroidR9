package com.cfl.util;

import com.cfl.model.Connection;
import com.cfl.model.DossierTravelSegment;

import java.util.Comparator;
import java.util.List;

/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorTravelSegmentDate implements Comparator<DossierTravelSegment> {

	private List<DossierTravelSegment> travelSegments;

	public ComparatorTravelSegmentDate(List<DossierTravelSegment> travelSegments) {
		this.travelSegments = travelSegments;
	}

	public int compare(DossierTravelSegment dossierTravelSegment1, DossierTravelSegment dossierTravelSegment2) {
		
		return dossierTravelSegment1.getSortedDate(travelSegments).compareTo(dossierTravelSegment2.getSortedDate(travelSegments));

	}

}
