package com.creapar.creativestone.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Alvaro
 *
 */
public class ConnectionFactory {
	
	/**
	 * Return connection with database
	 * 
	 * @return - Connection
	 */
	public Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost/creativestonedb?useServerPrepStmts=false&rewriteBatchedStatements=true","root", "root");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}