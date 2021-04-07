package fahad_CS385;

/**
 * @Class: Transactions_SP
 * @author: Fahad
 * @ForClass: CS385
 * 
 *            We will print transactions. There will be 4 outputs to show transactions before and after rollback and commits.  
 */
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

public class Transactions_SP {

	// URL to connect to database server
	public static final String DB_URL = "jdbc:mysql://localhost:3306?serverTimezone=UTC";

	// using random class to generate random idNumber
	private static final Random rand = new Random();

	public static void main(String[] args) {

		// variable to contain username and password
		String dbUser = args[0];
		String password = args[1];

		// Initiating and declaring variables
		Connection conn = null;
		Statement stmt = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		LinkedList<Integer> customerID = new LinkedList<Integer>();
		LinkedList<String> productID = new LinkedList<String>();

		try {
			// connect to database
			conn = DriverManager.getConnection(DB_URL, dbUser, password);
			System.out.println("Connection is valid: " + conn.isValid(2));

			// calling the stored procedure
			cs = conn.prepareCall("CALL classicmodels.AddOrder(?,?,?,?,?,?);");

			stmt = conn.createStatement();
			// getting the customer IDs from table customers of classicmodels
			rs = stmt.executeQuery("SELECT customerNumber FROM classicmodels.customers");
			while (rs.next()) {
				customerID.add(rs.getInt(1));
			}
			rs.close();

			// getting the product IDs from table products of classicmodels
			rs = stmt.executeQuery("SELECT productCode FROM classicmodels.products WHERE quantityInStock > 0");
			while (rs.next()) {
				productID.add(rs.getString(1));
			}
			rs.close();

			// randomly selecting a customerID
			int currentCustomer = customerID.get(rand.nextInt(customerID.size()));
			System.out.println("Selecting random products for customer ID: " + currentCustomer);
			int productNumbers = rand.nextInt(4) + 1;
			String codes = "";
			// selecting products that are being purchased
			for (int i = 0; i < productNumbers; i++) {
				int id = rand.nextInt(productID.size());

				if (i == productNumbers - 1) {
					codes += productID.get(id) + "";
				} else {
					codes += productID.get(id) + ", ";
				}
				// removing products from list so there is no duplicate products
				productID.remove(id);
			}

			System.out.println("\nCreating new order:");

			// creating the current date
			Date date = new Date(Calendar.getInstance().getTime().getTime());
			// need to use the calendar to push out 7 days
			Calendar calen = Calendar.getInstance();
			// this will give us todays date
			calen.setTime(date);
			// adds 7 days from today
			calen.add(Calendar.DAY_OF_MONTH, 7);
			Date requiredDate = new Date(calen.getTime().getTime());
			String[] statusNames = { "rollback", "commit" };

			// grabs the maximum of order number and increases the cursor by one and then
			// stores that orderNumber + 1
			rs = stmt.executeQuery("SELECT MAX(orderNumber) FROM classicmodels.orders;");
			rs.next();
			int orderNumber = rs.getInt(1) + 1;

			// prints rollbacks and commits based on their name stored on array statusNames
			for (int j = 0; j < statusNames.length; j++) {

				String status = statusNames[j];
				System.out.println("Next order number (before " + status + "): " + orderNumber);

				// sets values for the callable statements
				cs.setDate(1, date);
				cs.setDate(2, requiredDate);
				cs.setInt(3, currentCustomer);
				cs.setString(4, codes);
				cs.setString(5, status);
				cs.setInt(6, -1);
				cs.execute();

				rs = stmt.executeQuery("SELECT MAX(orderNumber) FROM classicmodels.orders;");
				rs.next();
				orderNumber = rs.getInt(1) + 1;
				System.out.println("Next order number (after " + status + "): " + orderNumber);
			}
		} // catch any exceptions and return specifics from SQL
		catch (SQLException se) {

			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
			se.printStackTrace();
		} finally {
			try {
				// close all the connections, result sets, prepared statements, and statements
				if (rs != null) {
					rs.close();
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
