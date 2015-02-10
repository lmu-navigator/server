package de.lmu.navigator.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutingEdge {

	private int id;
	private int floorId;
	private int source;
	private int target;
	private int weight;
	
	/**
	 * Default constructor
	 * @param id
	 * @param floorId
	 * @param source
	 * @param target
	 * @param weight
	 */
	public RoutingEdge(int id, int floorId, int source, int target, int weight) {
		super();
		this.id = id;
		this.floorId = floorId;
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public RoutingEdge() {
		
	}
	
	
	

	/**
	 * Sets all member attributes to 0 or null.
	 */
	public void clear() {
		this.id = 0;
		this.floorId = 0;
		this.source = 0;
		this.target = 0;
		this.weight = 0;
	}
	

	@Override
	public String toString() {
		return "RoutingEdges [id=" + id + ", floorId=" + floorId + ", source="
				+ source + ", target=" + target + ", weight=" + weight + "]";
	}
	
	

	/** Getters & Setters **/

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFloorId() {
		return floorId;
	}

	public void setFloorID(int floorID) {
		this.floorId = floorID;
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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
