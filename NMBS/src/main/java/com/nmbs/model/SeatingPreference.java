package com.nmbs.model;

import java.io.Serializable;

/**
 * The SeatingPreferences class represent the seating options (window, aisle) available for a
 * specific connection.
 * @author David.Shi
 *
 */
public class SeatingPreference implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String id;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	public SeatingPreference(String name, String id) {
		this.name = name;
		this.id = id;
	}
		
}
