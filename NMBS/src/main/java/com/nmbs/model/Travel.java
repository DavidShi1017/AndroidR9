package com.nmbs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The TravelData class is the top level class in the Search and Browse domain model.
 * @author David.Shi
 *
 */
public class Travel implements Serializable{

	private static final long serialVersionUID = 1L;
	private String travelId;
	private List<Connection> connections = new ArrayList<Connection>();
	private String direction;
	
	public boolean addConnection(Connection object) {
		return connections.add(object);
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public String getDirection() {
		return direction;
	}

	public String getTravelId() {
		return travelId;
	}

	public Travel(List<Connection> connections, String direction, String travelId) {
		super();
		this.connections = connections;
		this.direction = direction;
		this.travelId = travelId;
	}
		
}
