package de.lmu.navigator.server.model;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class City {

	private int id;
    private String code;
    private String name;
    private List<Street> streets;

    public City(int id, String code, String name, List<Street> streets) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.streets = streets;
	}
    
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public City() {
		
	}

	
	@Override
	public String toString() {
		return "City [id=" + id + ", code=" + code + ", name=" + name
				+ ", streets=" + streets + "]";
	}
	

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

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public List<Street> getStreets() {
		return streets;
	}

	public void setStreets(List<Street> streets) {
		this.streets = streets;
	}

	
}
