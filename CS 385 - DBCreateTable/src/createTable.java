
/**
 * @Class: createTable
 * @author: Fahad
 * @ForClass: CS385
 * 
 *            A class to hold information and used as objects in main
 */

import java.util.ArrayList;

public class createTable {

	private String author;
	private ArrayList<String> book;

	// Constructor
	public createTable(String author) {
		this.author = author;
		this.book = new ArrayList<>();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ArrayList<String> getBook() {
		return book;
	}

	public void setBook(ArrayList<String> book) {
		this.book = book;
	}

}
