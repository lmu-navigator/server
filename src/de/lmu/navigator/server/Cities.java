package de.lmu.navigator.server;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.lmu.navigator.server.database.CityMySQL;
import de.lmu.navigator.server.model.City;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/cities
 * REST resource. 
 */

@Path("/cities")
public class Cities {
	
	private CityMySQL db;	
	
	
	/**
	 * Request a filtered list of cities. 
	 * 
	 * @param City
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces("application/json")
	public ArrayList<City> getCitiesByRange()
			throws Exception {

		db = new CityMySQL();
		ArrayList<City> list = new ArrayList<City>();

		list = db.loadAllCities();
		
		db.close();
		
		return list;
	}
}
