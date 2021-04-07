import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * @Class: DBPreparedStatement
 * @author Fahad
 * @ForClass: CS385
 * 
 *            Interface for a customer to retrieve information about their
 *            respective company's sale representative
 *
 */
public class DBPreparedStatement {

	// URL to connect to database server
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {
		// variable to contain username and password
		String user = args[0];
		String pass = args[1];

		// Initiating and declaring variables to nulls
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		Scanner scan = new Scanner(System.in);
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connection is valid: " + conn.isValid(2));
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			// creating an infinite loop till the us
			while (true) {
				System.out.println(
						"\nWelcome to Classic Models!!\nTo continue, please enter your customer ID number or enter -1 to exit: ");
				int number = 0;
				boolean isNum = true;
				String input = scan.next();

				// checking if it is integer or not
				try {
					number = Integer.parseInt(input);
				} catch (Exception e) {
					isNum = false;
				}
				// exit when -1 is inputed
				if (number == -1) {
					break;
				}

				// if it is a number execute query with the number as the customerNumber
				if (isNum) {
					String sqlCus = "SELECT C.customerNumber, C.customerName, C.contactLastName, C.contactFirstName, "
							+ "       E.lastName, E.firstName, E.email AS EMAIL, E.extension AS PHONE "
							+ "FROM classicmodels.customers AS C "
							+ "LEFT JOIN classicmodels.employees AS E ON C.salesRepEmployeeNumber = E.employeeNumber "
							+ "WHERE customerNumber = ?";
					ps = conn.prepareStatement(sqlCus, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					ps.setInt(1, number);
					rs = ps.executeQuery();

				}

				// if it is not number let the customer know
				if (!isNum) {
					System.out.println("\nI am sorry. I did not recognize that response.\n");
					continue;
				}

				// Return name of customer based on customer number
				rs.last();
				if (rs.getRow() >= 1) {
					rs.beforeFirst();
					rs.next();

					System.out.println("\nThank you, we know you as " + rs.getString(2));
				} else {
					System.out.println("\nI am sorry, but we do not know you by that ID.");
					continue;
				}

				if (rs.getString("E.lastName") == null || rs.getString("E.email") == null) {
					System.out.println("I am sorry, " + rs.getString("C.customerName")
							+ ". Looks like we do not have a sales representative for your company in our system.\nWe will, though, have someone contact you to follow up.\n");
					continue;
				}

				// infinite loop
				while (true) {
					System.out.println(
							"\nCan you now enter all or the first part of either the last name or email address of yours sales representative?");
					String name = scan.next();
					// if name longer than 4 then reply back with the customer information and sale
					// representative information
					if (name.length() >= 4) {
						rs.beforeFirst();
						rs.next();

						if (rs.getString(5).contains(name) || rs.getString(7).contains(name)) {
							System.out.println("Welcome " + rs.getString("C.contactFirstName") + ""
									+ rs.getString("C.contactLastName") + " of " + rs.getString("C.customerName")
									+ ". How may we be of service?" + "\n\n" + rs.getString(6) + " " + rs.getString(5)
									+ "'s extension is " + rs.getString("PHONE")
									+ ", or would you like for me to have him contact you? \n");
							rs.close();
							break;
						} else {
							// use static query to look through the employee list
							stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							String sqlReps = "SELECT lastName, email FROM classicmodels.employees;";
							rs = stmt.executeQuery(sqlReps);

							boolean reported = false;
							rs.beforeFirst();

							// if name is found but not the same company
							while (rs.next()) {
								if (rs.getString("lastName").contains(name) || rs.getString("email").contains(name)) {
									System.out.println(
											"\nI am sorry. We do have a sales representative by that name, but not as your sales representative.\nWe will, though, have someone contact you to follow up.\n");
									reported = true;
									break;
								}
							}
							// if name is not found and not the same company
							if (!reported) {
								System.out.println(
										"\nI am very sorry, but we do not have a sales representative by that name.\nWe will, though, have someone contact you to follow up.\n");
								break;
							}
							break;
						}
					} else {
						System.out.println("\nI am sorry, but can you be more specific?\n");
						continue;
					}
				}

			}
			scan.close();

		}
		// catch error if there are any
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