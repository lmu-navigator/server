package de.lmu.navigator.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutingFloorConnection {

	private int id;
	private int buildingId;
	private int source;		// references RoutingNodes
	private int target;		// references RoutingNodes
	private String type;
	
	
	/**
	 * Default constructor
	 * @param id
	 * @param buildingId
	 * @param source
	 * @param target
	 * @param type
	 */
	public RoutingFloorConnection(int id, int buildingId, int source,
			int target, String type) {
		super();
		this.id = id;
		this.buildingId = buildingId;
		this.source = source;
		this.target = target;
		this.type = type;
	}

	/**
	 * for Jersey: must have no-argument constructor
	 */
	public RoutingFloorConnection() {
		
	}
	
	
	

	/**
	 * Sets all member attributes to 0 or null.
	 */
	public void clear() {
		this.id = 0;
		this.buildingId = 0;
		this.source = 0;
		this.target = 0;
		this.type = "";
	}

	@Override
	public String toString() {
		return "RoutingFloorConnections [id=" + id + ", buildingId="
				+ buildingId + ", source=" + source + ", target=" + target
				+ ", text=" + type + "]";
	}


	/** Getters & Setters **/
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String text) {
		this.type = text;
	}

}
