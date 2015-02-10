package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private static final boolean PRODUCTION_SERVER = false; 

	public Connection Get_Connection() throws Exception {
		
		try {
			String connectionURL, server, database, user, password = null;
			
			if (PRODUCTION_SERVER) {
				server = "localhost";
				database = "lmu_navigator";
				user = "navigator";
				password = "b-G?brfe";
//				user = "root";
//				password = "!root?";
			} else  { 
				server = "localhost";
				database = "lmu_navigator";
				user = "lmu_navigator";
				password = "Q35dvmVLQGmfu7vG";
			}

			connectionURL = "jdbc:mysql://"+server+":3306/"+database;
			Connection connection = null;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			connection = DriverManager.getConnection(connectionURL, user, password);
			
			return connection;
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

}
