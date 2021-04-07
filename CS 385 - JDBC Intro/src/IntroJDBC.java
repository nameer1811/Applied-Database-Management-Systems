import java.sql.*;

/**
 * @Class: IntroJDBC
 * @author Fahad
 * @ForClass: CS385
 * 
 *            Retrieves information on the SQL server and performs queries on
 *            the database to return information.
 *
 */
public class IntroJDBC {

	// URL to connect to database server
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {

		// variable to contain username and password
		String user = args[0];
		String pass = args[1];

		// Initiating and declaring variables to nulls
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null, resultSetGet = null, resultSetDBMD = null;

		try {
			System.out.println("Connecting to database!");
			conn = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connection is valid: " + conn.isValid(2));

			DatabaseMetaData metaData = conn.getMetaData();
			resultSetGet = metaData.getCatalogs();
			String table[] = { "TABLE" };
			resultSetDBMD = metaData.getTables(null, null, null, table);

			// Question 1
			System.out.println("\n DatabaseMetaData Schema Names");
			while (resultSetGet.next()) {
				System.out.println(" " + resultSetGet.getString(1));
			}

			// Question 2
			System.out.println("\nSchema Name \t\t Table Name");

			while (resultSetDBMD.next()) {
				System.out.println(" " + resultSetDBMD.getString(1) + " , \t" + resultSetDBMD.getString(3));
			}

			// Question 3

			ResultSet columns = metaData.getColumns(null, "classicmodels", "customers", null);
			System.out.println("Customer MetaData");

			while (columns.next()) {
				System.out.print("Column Position: " + columns.getInt("ORDINAL_POSITION"));
				System.out.print(", Column Name: " + columns.getString("COLUMN_NAME"));
				System.out.print(", Column Type: " + columns.getString("TYPE_NAME") + "\n");
			}

			// Question 4

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql1 = "SELECT employeeNumber, lastName, firstName, email FROM classicmodels.employees WHERE officeCode IN (SELECT officeCode FROM classicmodels.offices WHERE(city = 'Paris' OR city = 'San Francisco'))";
			rs = stmt.executeQuery(sql1);

			rs.last();
			System.out.println("\nNumber of records in ResultSet for the following query is " + rs.getRow());

			rs.beforeFirst();
			System.out.println("\nResultSet from query:");
			System.out.println("ID: \tFirst:  Last: \tEmail: ");

			while (rs.next()) {

				int id = rs.getInt("employeeNumber");
				String last = rs.getString("lastName");
				String first = rs.getString("firstName");
				String email = rs.getString("email");

				System.out.println(id + " \t" + first + " \t" + last + " \t" + email);
			}
			System.out.println("ResultSet cursor is past the end: " + rs.isAfterLast() + "\n");

			// Question 5

			while (rs.previous()) {
				int id = rs.getInt(1);
				String last = rs.getString(2);
				String first = rs.getString(3);
				String email = rs.getString(4);

				System.out.println("ID: " + id + ",\tFirst: " + first + ",\tLast: " + last + ",\tEmail: " + email);
			}
			System.out.println("ResultSet cursor is past the first: " + rs.isBeforeFirst());

		} catch (SQLException se) {

			// Question 7
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
			se.printStackTrace();
		} finally {
			try {
				// Question 6
				if (rs != null) {
					rs.close();
				}
				if (resultSetDBMD != null) {
					resultSetDBMD.close();
				}
				if (resultSetGet != null) {
					resultSetGet.close();
				}
				if (stmt != null) {
					stmt.close();
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
