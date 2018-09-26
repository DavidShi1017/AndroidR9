package com.nmbs.util;

import java.util.Comparator;


import com.nmbs.model.StationBoard;

/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorStationBoardDate implements Comparator<StationBoard> {

	

	public ComparatorStationBoardDate() {
		
	}

	public int compare(StationBoard stationBoard1, StationBoard stationBoard2) {
		
		return stationBoard1.getSortDate().compareTo(stationBoard2.getSortDate());
		
	}

}
