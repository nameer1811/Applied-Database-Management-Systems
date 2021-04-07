package fahad_CS385;


/**
 * @Class: productPerCode
 * @author: Fahad
 * @ForClass: CS385
 * 
 *            Select random product code of a random set of 2-6 and print the data out
 */

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class productPerCode {

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
		LinkedList<String> productCodes = new LinkedList<>();
		String pCodeComDel = "";
		int randomSize = (int) ((Math.random() * 5) + 2);

		try {
			//Connecting to database
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connection is valid: " + conn.isValid(2));
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			//Static query to retrieve all the product codes
			String productSQL = "SELECT productCode FROM classicmodels.products";
			rs = stmt.executeQuery(productSQL);

			//storing all the product codes in an linked list
			while (rs.next()) {
				productCodes.add(rs.getString(1));
			}

			//randomly select a random size set of 2 to 6 of those product codes
			for (int i = 0; i < randomSize; i++) {
				int randomSet = (int) (Math.random() * productCodes.size());
				pCodeComDel += productCodes.get(randomSet) + ",";
			}
			System.out.println(pCodeComDel);

			//using stored procedure
			String storeProcedure = "Call classicmodels.getProductsPerCode(?)";
			// creating connection to callable statement
			cs = conn.prepareCall(storeProcedure);
			//set comma delimited string and execute query
			cs.setString(1, pCodeComDel);
			rsSP = cs.executeQuery();


				// retrieve data from result set and print outputs from store procedure
				while (rsSP.next()) {
					System.out.println("Product Code: " + rsSP.getString(1) + " | Product Name: " + rsSP.getString(2)
							+ " | Product Line: " + rsSP.getString(3) + " | Product Vendor: " + rsSP.getString(4)
							+ " | Quantity in Stock: " + rsSP.getInt(5));
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
