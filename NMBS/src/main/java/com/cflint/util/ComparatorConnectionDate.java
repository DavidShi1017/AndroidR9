package com.cflint.util;

import com.cflint.model.Connection;
import com.cflint.model.StationBoard;

import java.util.Comparator;

/**
 * Order Schedule
 * 
 * @author David
 * 
 */

public class ComparatorConnectionDate implements Comparator<Connection> {



	public ComparatorConnectionDate() {
		
	}

	public int compare(Connection connection1, Connection connection2) {
		
		return connection1.getDeparture().compareTo(connection2.getDeparture());

	}

}