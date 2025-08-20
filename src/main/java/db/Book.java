package db;

public class Book {
  public int bookId;
  public String bookTitle;
  public String bookAuthor;
  public int bookQuantity;
  public boolean isAvailable = false;

  Book(int id, String title, String author, int quantity){
    this.bookId = id;
    this.bookTitle = title;
    this.bookAuthor = author;
    this.bookQuantity = quantity;

    if (this.bookQuantity > 0) {
      this.isAvailable = true;
    }
  }
}