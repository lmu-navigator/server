package de.lmu.navigator.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.lmu.navigator.server.model.Settings;
import de.lmu.navigator.server.model.Version;

/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/version
 * REST resource. 
 */

@Path("/version")
public class Versions {

	@GET
	@Produces("application/json")
	public Version getStreetByCode () throws Exception {
		
		Settings settings = new Settings();	
		return settings.getVersion();
	}
	
}
