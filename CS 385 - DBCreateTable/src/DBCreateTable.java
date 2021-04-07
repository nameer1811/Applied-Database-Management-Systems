
/**
 * @Class: DBCreateTable
 * @author: Fahad
 * @ForClass: CS385
 * 
 *            Interface for a user to retrieve authors of books and find out other books in database
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBCreateTable {

	// URL to connect to database server
	static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=CST";

	public static void main(String[] args) {

		// variable to contain username and password
		String user = args[0];
		String pass = args[1];

		// Initiating and declaring variables
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null, getCatalog = null;
		PreparedStatement ps = null, psBook = null;
		String temp = null;
		createTable author = null;
		LinkedList<createTable> table = new LinkedList<>();
		File createTable = new File("CreateTable.txt");

		try {
			try {
				// reading in the file
				BufferedReader buffR = new BufferedReader(new FileReader(createTable));

				// reading each line
				while ((temp = buffR.readLine()) != null) {
					if (temp.compareTo("") == 0) {
						continue;
					}

					// if the line has author tag store it in author
					if (temp.startsWith("<Author>", 0) == true) {
						author = new createTable(temp.substring(8));
					}

					// extract the name of the book from the tags
					Pattern pattern = Pattern.compile(">(.*?)</", Pattern.DOTALL);
					Matcher matcher = pattern.matcher(temp);

					// if the line has title tag store it in the array list of books
					if (temp.startsWith("<Title>")) {
						while (matcher.find()) {
							author.getBook().add(matcher.group(1));

						}
					}

					// if the line has end author tag, add the object to the list
					if (temp.compareTo("</Author>") == 0) {
						table.add(author);
					}
				}

				buffR.close(); // close buffered reader

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			// connect to database
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connection is valid: " + conn.isValid(2));
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			DatabaseMetaData metaData = conn.getMetaData();

			// get schema name
			getCatalog = metaData.getCatalogs();
			while (getCatalog.next()) {
				String schemaName = getCatalog.getString(1);

				// if schema name exists then delete the schema
				if (schemaName.equals("books")) {
					String deleteDBSqlString = "DROP DATABASE BOOKS";
					stmt.executeUpdate(deleteDBSqlString);
				}
			}

			// create new schema and create the tables authors and books
			String createDBSql = "CREATE DATABASE BOOKS";
			stmt.executeUpdate(createDBSql);

			String createAuthorTableSql = "CREATE TABLE BOOKS.AUTHORS " + "(id INTEGER NOT NULL AUTO_INCREMENT, "
					+ " author VARCHAR(255) UNIQUE, " + " PRIMARY KEY ( id ))";

			String createBooksTableSql = "CREATE TABLE BOOKS.BOOKS " + "(id INTEGER NOT NULL AUTO_INCREMENT, "
					+ " title VARCHAR(255), " + " authorID INTEGER NOT NULL, " + " PRIMARY KEY ( id ))";

			stmt.executeUpdate(createAuthorTableSql);
			stmt.executeUpdate(createBooksTableSql);
			stmt.close();

			String insertBook = "INSERT INTO BOOKS.BOOKS (title, authorID) VALUES (?,?)";
			psBook = conn.prepareStatement(insertBook, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			 stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			// inserting data into authors and books table
			for (int i = 0; i < table.size(); i++) {
				
				 String insertAuthor = "INSERT INTO BOOKS.AUTHORS (author) VALUES (\"" +
				 table.get(i).getAuthor() +"\")";
				 stmt.executeUpdate(insertAuthor);

				String aID = "SELECT id FROM books.authors WHERE author = \"" + table.get(i).getAuthor() + "\"";
				rs = stmt.executeQuery(aID);

				int authorID;
				while (rs.next()) {
					authorID = rs.getInt(1);

					for (int j = 0; j < table.get(i).getBook().size(); j++) {

						psBook.setString(1, table.get(i).getBook().get(j));
						psBook.setInt(2, authorID);
						psBook.executeUpdate();
					}

				}
			}

			psBook.close();
			// creating a scanner object
			Scanner scan = new Scanner(System.in);
			// using an infinite loop
			while (true) {

				// scan name of book
				System.out.println("Please enter the name of the book to find author or type 'exit' to leave");
				String nameBook = scan.nextLine();

				// if exit is typed, quit the loop
				if (nameBook.equalsIgnoreCase("exit")) {
					break;
				}

				// using prepared statement to take in user value for query
				String authorBook = "SELECT A.author, A.id FROM books.authors as A JOIN books.books as B ON B.authorID = A.id  WHERE b.title = ?";

				ps = conn.prepareStatement(authorBook, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ps.setString(1, nameBook);
				rs = ps.executeQuery();

				// check if the author comes up for the book
				if (rs.next() == false) {
					System.out.println("Sorry there are no books by the name \"" + nameBook + "\" in our database.");
					continue;
				} else {
					System.out.println("\nThe book, " + nameBook + ", was written by " + rs.getString(1));
					int id = rs.getInt(2);
					rs.close();
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					String books = "Select title, authorID FROM books.books";
					rs = stmt.executeQuery(books);

					System.out.print("This author's book(s) in our database are:\n");
					rs.beforeFirst();
					while (rs.next()) {
						if (id == rs.getInt(2)) {
							System.out.println(rs.getString("title"));
						}
					}
					rs.close();
				}

				// print out all author and books from database
				String aBooks = "SELECT A.author, B.title FROM books.authors as A JOIN books.books as B ON B.authorID = A.id";
				rs = stmt.executeQuery(aBooks);
				System.out.println("\nThe books and authors in our entire database are: \n");

				while (rs.next()) {
					System.out.println("Author: " + rs.getString("A.author") + " | Book: " + rs.getString(2));
				}
				System.out.println();

			}
			scan.close();
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
				if (psBook != null) {
					psBook.close();
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
