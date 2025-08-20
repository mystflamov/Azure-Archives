package db;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Represents a borrowed book with borrowing details.
public class BorrowedBook {
    private int borrowedBooksId;     // Unique ID for this borrowing record
    private String username;         // User who borrowed the book
    private int bookId;              // ID of the borrowed book
    private String title;
    private String author;
    private LocalDate dateBorrowed;  // Date when borrowed
    private LocalDate dueDate;       // Due date for returning
    private boolean isReturned;      // Return status

    // Constructor for BorrowedBook.
    public BorrowedBook(int borrowedBooksId, String username, int bookId, String title, String author,
                        LocalDate dateBorrowed, LocalDate dueDate, boolean isReturned) {
        this.borrowedBooksId = borrowedBooksId;
        this.username = username;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.dateBorrowed = dateBorrowed;
        this.dueDate = dueDate;
        this.isReturned = isReturned;
    }

    public BorrowedBook(String username, int bookId, LocalDate dateBorrowed, LocalDate dueDate, boolean isReturned) {
        this.username = username;
        this.bookId = bookId;
        this.dateBorrowed = dateBorrowed;
        this.dueDate = dueDate;
        this.isReturned = isReturned;
    }

    // Getters and setters for encapsulation
    public int getBorrowedBooksId() {
        return borrowedBooksId;
    }
    
    public String getUsername() {
        return username;
    }
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getDateBorrowed() {
        return dateBorrowed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return isReturned;
    }
    
    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    // Return a readable string with book details for display to user.
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "BookID: " + bookId
                + ", Borrowed: " + dateBorrowed.format(formatter)
                + ", Due: " + dueDate.format(formatter)
                + ", Returned: " + (isReturned ? "Yes" : "No");
    }
}
