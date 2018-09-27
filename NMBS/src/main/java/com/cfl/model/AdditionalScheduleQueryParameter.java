package com.cfl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * To get earlier or later trains for a specific offer query
 *@author:Alice
 */
public class AdditionalScheduleQueryParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	public enum Direction{
		Forward,
		Backward
	}
	@SerializedName("Direction")
	Direction direction;
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
