
/**
 * @Class: Employee_Location
 * @author: Fahad
 * @ForClass: CS385
 * 
 *            Print all employees and their office address using static query and store procedures. 
 */
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class Employee_Location {

	// URL to connect to database server
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {

		// variable to contain username and password
		String user = args[0];
		String pass = args[1];

		// Initiating and declaring variables
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null, rsSP = null;
		CallableStatement cs = null;
		LinkedList<Integer> employeeID = new LinkedList<Integer>();

		try {
			// connect to database
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connection is valid: " + conn.isValid(2));

			// create static query and execute it
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT employeeNumber AS ID, CONCAT(E.firstName,\" \", E.lastName) AS Name, CONCAT(O.City, IF (O.state IS NULL, \", \", CONCAT(\", \", O.state,\", \")), O.Country) AS Location\r\n"
					+ "FROM classicmodels.employees AS E\r\n"
					+ "JOIN classicmodels.offices AS O ON E.officeCode = O.officeCode;";
			rs = stmt.executeQuery(sql);

			// retrieve data from result set and print all outputs from static query
			System.out.println("\nMySQL Output via Static Query\n");
			while (rs.next()) {
				System.out.println(
						"ID: " + rs.getInt(1) + " | Name : " + rs.getString(2) + " | Location: " + rs.getString(3));

				// add the employee id to the list
				employeeID.add(rs.getInt(1));

			}

			// set data to store procedure, go through a loop, and print all outputs from
			// store procedure
			System.out.println("\nMySQL Output via Store Procedure\n");
			String storeProcedure = "Call classicmodels.EmployeeLocation(?)";
			// creating connection to callable statement
			cs = conn.prepareCall(storeProcedure);

			// going through loop to find employee id in list and set for store procedure
			for (int i = 0; i < employeeID.size(); i++) {
				cs.setInt(1, employeeID.get(i));
				rsSP = cs.executeQuery();

				// retrieve data from result set and print all outputs from store procedure
				while (rsSP.next()) {
					System.out.println("ID: " + rsSP.getInt(1) + " | Name : " + rsSP.getString(2) + " | Location: "
							+ rsSP.getString(3));
				}
			}
		}

		// catch any exceptions and return specifics from SQL
		catch (SQLException se) {

			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
			se.printStackTrace();
		} finally {
			try {
				// close all the connections, result sets, callable statements and statements
				if (rs != null) {
					rs.close();
				}
				if (rsSP != null) {
					rsSP.close();
				}
				if (stmt != null) {
					stmt.close();
				}
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
