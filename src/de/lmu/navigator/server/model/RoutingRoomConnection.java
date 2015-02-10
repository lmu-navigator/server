package de.lmu.navigator.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutingRoomConnection {

	private int id;
	private int nodeId;
	private int roomId;
	
	/**
	 * default constructor
	 * @param id
	 * @param nodeId
	 * @param roomId
	 */
	public RoutingRoomConnection(int id, int nodeId, int roomId) {
		super();
		this.id = id;
		this.nodeId = nodeId;
		this.roomId = roomId;
	}
	
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public RoutingRoomConnection() {
		
	}
	
	
	

	/**
	 * Sets all member attributes to 0 or null.
	 */
	public void clear() {
		this.id = 0;
		this.nodeId = 0;
		this.roomId = 0;
	}

	@Override
	public String toString() {
		return "RoutingRoomConnections [id=" + id + ", nodeId=" + nodeId
				+ ", roomId=" + roomId + "]";
	}
	

	/** Getters & Setters **/

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
