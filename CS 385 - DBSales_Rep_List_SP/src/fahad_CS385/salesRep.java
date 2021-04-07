package fahad_CS385;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Class: salesRep
 * @author Fahad
 * @ForClass: CS385
 * 
 *            Returning a list of names and emails of employees from USA
 *            semicolon delimited
 *
 */

public class salesRep {

	// URL to connect to database server
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {

		// variable to contain username and password
		String user = args[0];
		String pass = args[1];

		// Initiating and declaring variables to nulls
		Connection conn = null;
		CallableStatement cs = null;
		String country = "USA";

		try {
			// Connecting to database
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connection is valid: " + conn.isValid(2));

			// calling the stored procedure
			cs = conn.prepareCall("CALL classicmodels.createSalesRepList(?, ?, ?);");

			// print which country
			System.out.println("\nReturning the list of employees from \'" + country + "\':\n");

			// set the callable statements and execute
			cs.setString(1, country);
			cs.setString(2, "");
			cs.setString(3, "");
			cs.execute();

			// store the email and name to variables
			String email = cs.getString(2);
			String name = cs.getString(3);

			// store the emails and names to a 2d array
			String[][] data = { email.split(";"), name.split(";") };

			// print the column names
			System.out.println("Name \t\t\t Email");
			// use for loop to get the index of the data and print it out
			for (int i = 0; i < data[0].length; i++) {
				System.out.println(data[1][i] + ";  " + data[0][i]);
			}
		} // catch any exceptions and return specifics from SQL
		catch (SQLException se) {

			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
			se.printStackTrace();
		} finally {
			try {
				// close all the connections and callable statements
				if (cs != null) {
					cs.close();
				}
				if (conn != null) {
					conn.close();
					System.out.println("Connection is closed: " + conn.isClosed());
				}
			} catch (SQLException se2) {
			}

		}

	}
}
