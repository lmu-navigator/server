package de.lmu.navigator.server.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Room {
	
	private int id;
	private String code;
	private String name;
	private String floorCode;
	private double posX;
	private double posY;
	private boolean hidden;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param floorId
	 * @param roomName
	 * @param x
	 * @param y
	 * @param measurements
	 */
	public Room(int id, String code, String name, String floorCode, double x, double y, boolean hidden) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.floorCode = floorCode;
		this.posX = x;
		this.posY = y;
		this.hidden = hidden;
	}
	
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public Room() {
		
	}
	
	
	

	/**
	 * Sets all member attributes to 0 or null.
	 */
	public void clear() {
		this.id = 0;
		this.code = "";
		this.name = "";
		this.floorCode = "";
		this.posX = 0;
		this.posY = 0;
		this.hidden = false;
	}
	

	
	@Override
	public String toString() {
		return "Room [id=" + id + ", code=" + code + ", name=" + name
				+ ", floorCode=" + floorCode + ", posX=" + posX + ", posY="
				+ posY + ", hidden=" + hidden + "]";
	}
	
	
	
	//@XmlTransient
	// TODO implement public boolean isEmpty() {
	
	

	/** Getters & Setters **/
	
	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String roomName) {
		this.name = roomName;
	}

	public String getFloorCode() {
		return floorCode;
	}

	public void setFloorCode(String floorCode) {
		this.floorCode = floorCode;
	}
	

	/**
	 * pixel coordinate of room on indoor map
	 */
	public double getPosX() {
		return posX;
	}

	public void setPosX(double x) {
		this.posX = x;
	}

	
	/**
	 * pixel coordinate of room on indoor map
	 */
	public double getPosY() {
		return posY;
	}

	public void setPosY(double y) {
		this.posY = y;
	}

	/**
	 * 0 = not hidden (visible), 1 = hidden
	 */
	@XmlTransient
	public boolean getVisibility() {
		return hidden;
	}

	/**
	 * 0 = not hidden (visible), 1 = hidden
	 */
	public void setVisibile(boolean hidden) {
		this.hidden = hidden;
	}

}
