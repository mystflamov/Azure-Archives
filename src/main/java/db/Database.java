package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.ResultSet;

public class Database {

    public static void main(String[] args) {
        // Load database configuration from config.properties
        Properties props = new Properties();
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Unable to find config.properties");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            System.out.println("Error loading config.properties");
            e.printStackTrace();
            return;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        String databaseName = props.getProperty("db.name");

        // Connect to MySQL server (no database chosen) and create database if not exists
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            stmt.executeUpdate(createDatabaseSQL);
            System.out.println("Database created successfully (if it didn't exist).");

        } catch (SQLException e) {
            System.out.println("Error connecting to MySQL server or creating database!");
            e.printStackTrace();
            return;
        }

        // Connect to the newly created database to create tables
        String dbUrl = url + databaseName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement()) {

            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(255) PRIMARY KEY, " +
                    "fullName VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "age INT NOT NULL" +
                    ");";
            stmt.executeUpdate(createUsersTableSQL);
            System.out.println("Users table created successfully.");

            String createBooksTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                    "bookId INT AUTO_INCREMENT PRIMARY KEY, " +
                    "bookTitle VARCHAR(255) NOT NULL, " +
                    "bookAuthor VARCHAR(255) NOT NULL, " +
                    "bookQuantity INT NOT NULL DEFAULT 0" +
                    ");";
            stmt.executeUpdate(createBooksTableSQL);
            System.out.println("Books table created successfully.");
            
            // Insert default books only if the books table is empty
            String checkBooksSQL = "SELECT COUNT(*) FROM books;";
            ResultSet rs = stmt.executeQuery(checkBooksSQL);
            rs.next();
            int bookCount = rs.getInt(1);

            if (bookCount == 0) {
                String insertBooksSQL = "INSERT INTO books (bookTitle, bookAuthor, bookQuantity) VALUES " +
                    "('The Great Gatsby', 'F. Scott Fitzgerald', 5), " +
                    "('To Kill a Mockingbird', 'Harper Lee', 6), " +
                    "('1984', 'George Orwell', 4), " +
                    "('Pride and Prejudice', 'Jane Austen', 5), " +
                    "('The Catcher in the Rye', 'J.D. Salinger', 4), " +
                    "('Moby Dick', 'Herman Melville', 3), " +
                    "('War and Peace', 'Leo Tolstoy', 3), " +
                    "('The Odyssey', 'Homer', 4), " +
                    "('The Hobbit', 'J.R.R. Tolkien', 7), " +
                    "('Fahrenheit 451', 'Ray Bradbury', 5), " +
                    "('Brave New World', 'Aldous Huxley', 4), " +
                    "('The Picture of Dorian Gray', 'Oscar Wilde', 3), " +
                    "('Jane Eyre', 'Charlotte Brontë', 6), " +
                    "('Wuthering Heights', 'Emily Brontë', 4), " +
                    "('The Chronicles of Narnia', 'C.S. Lewis', 6), " +
                    "('The Lord of the Rings', 'J.R.R. Tolkien', 5), " +
                    "('The Kite Runner', 'Khaled Hosseini', 6), " +
                    "('The Alchemist', 'Paulo Coelho', 5), " +
                    "('The Fault in Our Stars', 'John Green', 7), " +
                    "('The Book Thief', 'Markus Zusak', 5), " +
                    "('The Road', 'Cormac McCarthy', 3), " +
                    "('The Handmaid''s Tale', 'Margaret Atwood', 4), " +
                    "('The Bell Jar', 'Sylvia Plath', 3), " +
                    "('The Secret Garden', 'Frances Hodgson Burnett', 5), " +
                    "('Little Women', 'Louisa May Alcott', 4), " +
                    "('The Grapes of Wrath', 'John Steinbeck', 3), " +
                    "('The Old Man and the Sea', 'Ernest Hemingway', 4), " +
                    "('The Da Vinci Code', 'Dan Brown', 6), " +
                    "('The Hunger Games', 'Suzanne Collins', 7), " +
                    "('Dune', 'Frank Herbert', 5), " +
                    "('The Martian', 'Andy Weir', 4), " +
                    "('The Perks of Being a Wallflower', 'Stephen Chbosky', 5), " +
                    "('The Glass Castle', 'Jeannette Walls', 3), " +
                    "('Educated', 'Tara Westover', 4), " +
                    "('Becoming', 'Michelle Obama', 6), " +
                    "('The Immortal Life of Henrietta Lacks', 'Rebecca Skloot', 3), " +
                    "('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 6), " +
                    "('Thinking, Fast and Slow', 'Daniel Kahneman', 5), " +
                    "('The Power of Habit', 'Charles Duhigg', 4), " +
                    "('Atomic Habits', 'James Clear', 7);";

                stmt.executeUpdate(insertBooksSQL);
                System.out.println("Sample books inserted successfully.");
            } else {
                System.out.println("Books already exist in the database. Skipping insert.");
            }

            String createBorrowedBooksTableSQL = "CREATE TABLE IF NOT EXISTS borrowed_books (" +
                    "borrowedBooksId INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "bookId INT NOT NULL, " +
                    "dateBorrowed DATE NOT NULL, " +
                    "dueDate DATE NOT NULL, " +
                    "isReturned BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE, " +
                    "FOREIGN KEY (bookId) REFERENCES books(bookId) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(createBorrowedBooksTableSQL);
            System.out.println("Borrowed_books table created successfully.");

            String createSessionTableSQL = "CREATE TABLE IF NOT EXISTS session (" +
                    "sessionId VARCHAR(255) PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "loginTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(createSessionTableSQL);
            System.out.println("Session table created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating tables in the database!");
            e.printStackTrace();
        }
    }
}