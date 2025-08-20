package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Manages borrowed books and their database interactions.
public class BorrowedBooksManager {
    static final String url = "jdbc:mysql://localhost:3306/azure"; // Database URL
    static final String user = "root"; // MySQL username
    static final String password = "|2~mysticalfleur~7|"; // MySQL password

    // Method to fetch borrowed books for a specific user
    public static List<BorrowedBook> fetchCurrentBorrowedBooks(String username) throws SQLException {
        String sql = "SELECT b.bookId, b.bookTitle, b.bookAuthor, bb.borrowedBooksId, bb.dateBorrowed, bb.dueDate, bb.isReturned " +
                     "FROM borrowed_books bb " +
                     "JOIN books b ON bb.bookId = b.bookId " +
                     // Kinukuha lang natin yung mga HINDI pa narereturn 
                     "WHERE bb.username = ? AND bb.isReturned = 0";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            List<BorrowedBook> borrowedBooksList = new ArrayList<>();

            while (rs.next()) {
                // Create a BorrowedBook object with the correct parameters
                BorrowedBook book = new BorrowedBook(
                    rs.getInt("borrowedBooksId"),
                    username,
                    rs.getInt("bookId"),
                    rs.getString("bookTitle"),
                    rs.getString("bookAuthor"),
                    rs.getDate("dateBorrowed").toLocalDate(),
                    rs.getDate("dueDate").toLocalDate(),
                    rs.getBoolean("isReturned")
                );
                borrowedBooksList.add(book);
            }
            return borrowedBooksList;
        }
    }

    public static List<BorrowedBook> fetchBorrowedBooks(String username) throws SQLException {
        String sql = "SELECT b.bookId, b.bookTitle, b.bookAuthor, bb.borrowedBooksId, bb.dateBorrowed, bb.dueDate, bb.isReturned " +
                     "FROM borrowed_books bb " +
                     "JOIN books b ON bb.bookId = b.bookId " +
                     "WHERE bb.username = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            List<BorrowedBook> borrowedBooksList = new ArrayList<>();

            while (rs.next()) {
                // Create a BorrowedBook object with the correct parameters
                BorrowedBook book = new BorrowedBook(
                    rs.getInt("borrowedBooksId"),
                    username,
                    rs.getInt("bookId"),
                    rs.getString("bookTitle"),
                    rs.getString("bookAuthor"),
                    rs.getDate("dateBorrowed").toLocalDate(),
                    rs.getDate("dueDate").toLocalDate(),
                    rs.getBoolean("isReturned")
                );
                borrowedBooksList.add(book);
            }
            return borrowedBooksList;
        }
    }

    // Inserts a new borrowed book record into the database.
    public static void insertBorrowedBook(BorrowedBook borrowedBook) throws SQLException {
        String sql = "INSERT INTO borrowed_books (username, bookId, dateBorrowed, dueDate, isReturned) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, borrowedBook.getUsername());
            pstmt.setInt(2, borrowedBook.getBookId());
            pstmt.setDate(3, java.sql.Date.valueOf(borrowedBook.getDateBorrowed()));
            pstmt.setDate(4, java.sql.Date.valueOf(borrowedBook.getDueDate()));
            pstmt.setBoolean(5, borrowedBook.isReturned());
            pstmt.executeUpdate();
        }
    }

    // Updates the return status of a borrowed book in the database.
    public static void updateReturnStatus(BorrowedBook borrowedBook) throws SQLException {
        String sql = "UPDATE borrowed_books SET isReturned = ? WHERE username = ? AND borrowedBooksId = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, borrowedBook.isReturned());
            pstmt.setString(2, borrowedBook.getUsername());
            pstmt.setInt(3, borrowedBook.getBorrowedBooksId());
            pstmt.executeUpdate();
        }
    }

    // Updates the quantity of a book in the database.
    public static void updateBookQuantity(int bookId, int quantityChange) throws SQLException {
        String sql = "UPDATE books SET bookQuantity = bookQuantity + ? WHERE bookId = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }
    }

    // Gets the current quantity of a book from the database.
    public static int getBookQuantity(int bookId) throws SQLException {
        String sql = "SELECT bookQuantity FROM books WHERE bookId = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("bookQuantity");
            }
        }
        return 0; // Default to 0 if the book is not found
    }
}
