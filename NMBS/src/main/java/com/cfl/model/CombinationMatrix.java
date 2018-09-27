package com.cfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The CombinationMatrix is used to keep track of the dependencies between offer
 */
public class CombinationMatrix implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<CombinationMatrixRow> rows = new ArrayList<CombinationMatrixRow>();

	/**
	 * @return the combinationMatrixRow
	 */
	public List<CombinationMatrixRow> getCombinationMatrixRow() {
		return rows;
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addCombinationMatrixRow(CombinationMatrixRow object) {
		return rows.add(object);
	}

	public CombinationMatrix(List<CombinationMatrixRow> rows) {
		this.rows = rows;
	}
	
	
}
