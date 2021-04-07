package fahad_CS385;

/**
 * @Class: Transactions_1
 * @author: Fahad
 * @ForClass: CS385
 * 
 *            We will print transactions. One will be allowed to print and commit and the other will be rolled back. 
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

public class Transactions_1 {

	// URL to connect to database server
	public static final String DB_URL = "jdbc:mysql://localhost:3306?serverTimezone=UTC";

	// using random class to generate random idNumber
	private static final Random rand = new Random();

	public static void main(String[] args) throws ClassNotFoundException {

		// variable to contain username and password
		String dbUser = args[0];
		String password = args[1];

		// Initiating and declaring variables
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList<Integer> customerID = new LinkedList<Integer>();
		LinkedList<String> productID = new LinkedList<String>();

		try {

			// connect to database
			conn = DriverManager.getConnection(DB_URL, dbUser, password);
			System.out.println("Connection is valid: " + conn.isValid(2));

			// using prepared statement to store information
			stmt = conn.createStatement();
			ps = conn.prepareStatement("INSERT INTO classicmodels.orderdetails "
					+ "(orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber)"
					+ " VALUES (?,?,?,?,?);");

			System.out.println("\nGetting avaiable products and currect customers.");

			// grabbing the customer IDs from customers table of classicmodels
			rs = stmt.executeQuery("SELECT customerNumber FROM classicmodels.customers;");
			while (rs.next()) {
				customerID.add(rs.getInt(1));
			}
			rs.close();

			// grabbing the product IDs from product table of classicmodels
			rs = stmt.executeQuery("SELECT productCode FROM classicmodels.products WHERE quantityInStock > 0;");
			while (rs.next()) {
				productID.add(rs.getString(1));
			}
			rs.close();

			// randomly selecting a customer's ID
			int currentCustomer = customerID.get(rand.nextInt(customerID.size()));
			System.out.println("\nSelecting random products for customer ID: " + currentCustomer);
			int numProducts = rand.nextInt(4) + 1;
			String[] productCodes = new String[numProducts];

			// select products which are being purchased
			for (int i = 0; i < numProducts; i++) {
				int idNumber = rand.nextInt(productID.size());
				productCodes[i] = productID.get(idNumber);
				// removing the product from the list so there is no duplicates
				productID.remove(idNumber);
			}
			System.out.println("\nTransaction Starting!");

			conn.setAutoCommit(false);

			for (int i = 0; i < 2; i++) {
				// finding maximum order number
				rs = stmt.executeQuery("SELECT MAX(orderNumber) FROM classicmodels.orders;");
				rs.next();
				int orderNum = rs.getInt(1) + 1;
				rs.close();

				// showing the new order number
				System.out.println("\nOrder number: " + orderNum);

				// using the date format for dates
				SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

				// using the current date
				Date date = new Date(Calendar.getInstance().getTime().getTime());
				System.out.println("Order date:" + format.format(date));

				// using calendar to push out 7 days for the required date
				Calendar cldr = Calendar.getInstance();
				// setting today's date
				cldr.setTime(date);
				// adding 7 days to today
				cldr.add(Calendar.DAY_OF_MONTH, 7);
				Date reqDate = new Date(cldr.getTime().getTime());
				System.out.println("Required Date: " + format.format(reqDate) + "\nStatus: In Process \nComments: None"
						+ "\nCustomer ID:" + currentCustomer + "\n\nPreparing order!");

				// inserting for the order
				stmt.execute("INSERT INTO classicmodels.orders "
						+ "(orderNumber, orderDate, requiredDate, shippedDate, status, "
						+ "comments, customerNumber) VALUES " + "(" + orderNum + ",'" + format.format(date) + "','"
						+ format.format(reqDate) + "', NULL, 'In Process', NULL," + currentCustomer + ");");

				System.out.println(
						"Order was successfully sent to the server. \nAdding the products to the order details:\n");

				// setting and printing out the information for the user to view
				for (int j = 0; j < productCodes.length; j++) {
					String prod = productCodes[j];

					ps.setInt(1, orderNum);
					ps.setString(2, prod);
					ps.setInt(3, j + 1);
					ps.setInt(4, j + 1);
					ps.setInt(5, j + 1);
					ps.execute();
					System.out.println("Added line " + (j + 1) + " to order:" + "\nProduct Code: " + prod
							+ "\nQuantity Ordered: " + (j + 1) + "\nPrice For Each Unit: $" + (j + 1) + ".00\n");
				}

				if (i == 1) {
					conn.commit();
				} else {
					conn.rollback();
				}

				// getting the maximum number of orders
				rs = stmt.executeQuery("SELECT MAX(orderNumber) FROM classicmodels.orders;");
				rs.next();
				int orderNumber = rs.getInt(1);

				//check if the previous order number matches with the current order number
				if (orderNum == orderNumber) {
					//print rollback unsuccessful or product added to the order
					if (i == 0)
						System.out.println("The rollback was unsuccessful. Maximum order number: " + orderNumber);
					else {
						System.out.println("All products were sucessfully added to the order."
								+ " Transaction complete!!\nMaximum order number: " + orderNumber);
					}
					//if the order number does not match with the current order number
				} else {
					//print rollback of the insertions were successful else print commit was unsuccessful
					if (i == 0)
						System.out.println("Rollback of insertions were successful. We will retry.");
					else {
						System.out.println("The commit was unsuccessful. Maximum order number: " + orderNumber);
					}
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
				// close all the connections, result sets, prepared statements, and statements
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
				if (ps != null) {
					ps.close();
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
