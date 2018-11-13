package com.cflint.util;

import java.util.Comparator;


import com.cflint.model.Station;





/**
 * Order Schedule 
 * @author David
 *
 */

public class ComparatorStationName implements Comparator<Station>{

	


	public int compare(Station lhs, Station rhs) {
		
		return lhs.getName().compareTo(rhs.getName());
	}


	
}
