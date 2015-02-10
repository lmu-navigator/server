package de.lmu.navigator.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutingNode {

	private int id;
	private int floorId;
	private double posX;
	private double posY;
	private boolean entrance;
	
	/** 
	 * Default constructor
	 * @param id
	 * @param floorId
	 * @param posX
	 * @param posY
	 * @param entrance
	 */
	public RoutingNode(int id, int floorId, double posX, double posY,
			boolean entrance) {
		super();
		this.id = id;
		this.floorId = floorId;
		this.posX = posX;
		this.posY = posY;
		this.entrance = entrance;
	}


	/**
	 * for Jersey: must have no-argument constructor
	 */
	public RoutingNode() {
		
	}
	
	
	

	/**
	 * Sets all member attributes to 0 or null.
	 */
	public void clear() {
		this.id = 0;
		this.floorId = 0;
		this.posX = 0.0;
		this.posY = 0.0;
		this.entrance = false;
	}
	
	
	@Override
	public String toString() {
		return "RoutingNodes [id=" + id + ", floorId=" + floorId + ", posX="
				+ posX + ", posY=" + posY + ", entrance=" + entrance + "]";
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

	/**
	 * pixel coordinate of room on indoor map
	 */
	public double getPosX() {
		return posX;
	}


	public void setPosX(double posX) {
		this.posX = posX;
	}

	/**
	 * pixel coordinate of room on indoor map
	 */
	public double getPosY() {
		return posY;
	}


	public void setPosY(double posY) {
		this.posY = posY;
	}


	public boolean isEntrance() {
		return entrance;
	}


	public void setEntrance(boolean entrance) {
		this.entrance = entrance;
	}

}
