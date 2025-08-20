package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Main class for running the library management system application.
// Handles user interaction, displays available books, and delegates borrowing-related actions to InquiryManager.
public class App {
    // Shared scanner instance for user input
    static Scanner scanner = new Scanner(System.in);

    // Database connection credentials
    static final String url = "jdbc:mysql://localhost:3306/azure";
    static final String user = "root";
    static final String password = "|2~mysticalfleur~7|";

    public void run() {
        // Initialize borrowedBooks list to hold borrowed book info
        List<BorrowedBook> borrowedBooks = new ArrayList<>();

        int exit = 1;
        while (exit == 1) {
            // Fetch latest borrowed books from DB after login or prior changes
            try {
                borrowedBooks = BorrowedBooksManager.fetchBorrowedBooks(UserAuth.currentUsername);
            } catch (SQLException e) {
                System.out.println("Error fetching borrowed books.");
                e.printStackTrace();
            }
            
            // Display available books in library
            displayBooks();

            // Display main action menu
            System.out.println("=========================================================");
            System.out.println("1. Inquire (Borrow, Return, Check borrowed books)");
            System.out.println("2. Logout");
            System.out.println("=========================================================");
            System.out.print("Enter your choice: ");

            int answer;
            try {
                answer = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (Exception e) {
                System.out.println("Invalid input; please enter a number 1 or 2.");
                scanner.nextLine(); // consume invalid input
                continue;
            }
            
            if (answer == 1) {
                // Handle inquiry operations (borrow, return, check)
                borrowedBooks = InquiryManager.inquire(borrowedBooks, UserAuth.currentUsername);
            } else if (answer == 2) {
                // Logout current user and restart login flow
                UserAuth.logout();
                borrowedBooks.clear(); // Clear borrowed books list
                UserAuth.loginOrRegister(this);
                continue; // restart main loop after re-login
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
                continue;
            }
        }
        
        System.out.println("Thank you for using Azure Archive. Goodbye!");
        scanner.close();
    }

    // Displays all available books in the library with id, title, and quantity.
    // Connects to the database to retrieve current book data.
    public static List<Book> displayBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("bookId");
                String title = rs.getString("bookTitle");
                String author = rs.getString("bookAuthor");
                int quantity = rs.getInt("bookQuantity");
                books.add(new Book(id, title, author, quantity));
            }

            return books;
        } catch (SQLException e) {
            System.out.println("Connection failed while fetching books!");
            e.printStackTrace();
        }

        return null;
    }

    public static List<Book> searchedBooks(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE bookTitle LIKE ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("bookId");
                title = rs.getString("bookTitle");
                String author = rs.getString("bookAuthor");
                int quantity = rs.getInt("bookQuantity");
                books.add(new Book(id, title, author, quantity));
            }

            return books;
        } catch (SQLException e) {
            System.out.println("Connection failed while fetching books!");
            e.printStackTrace();
        }

        return null;
    }
}
