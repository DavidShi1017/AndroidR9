package com.cfl.model;

import java.io.Serializable;
/**
 * Data model implementation record Favorite .
 */

public class Favorite implements Serializable{

	private static final long serialVersionUID = 3360635161641749069L;
	private int id;
	private String name;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Favorite(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
}
