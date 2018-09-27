package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class BEStation implements Serializable{

	private static final long serialVersionUID = 1L;
	private static BEStation instance;
	private List<Station> stations = new ArrayList<Station>();
	
	private BEStation(){
		
	}
	
	public static BEStation getInstance() {
		if (instance == null) {
			instance = new BEStation();
			
		}
		return instance;
	}
	
	
}
