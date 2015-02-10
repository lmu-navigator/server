package de.lmu.navigator.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class Ping {
	
	public Ping () {
		super();
	}

	@GET
	@Path("/ping")
	public Response HelloWorld() {
		String output = "Pong";
		return Response.status(200).entity(output).build();
	}

}
