package db;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


// Manages inquiries related to borrowed books.
public class InquiryManager {
    static Scanner scanner = new Scanner(System.in);

    // Handles user inquiries about borrowing, returning, and checking borrowed books.
    // borrowedBooks list of currently borrowed books
    // username current user's username
    // updated list of borrowed books
    public static List<BorrowedBook> inquire(List<BorrowedBook> borrowedBooks, String username) {
        int back = 1;
        while (back != 0) {
            System.out.println("=========================================================");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Check borrowed books");
            System.out.println("4. Return to Main Menu");
            System.out.println("=========================================================");
            System.out.print("Enter your choice: ");
            int operation;
            try {
                operation = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input, please enter a number.");
                scanner.nextLine();
                return borrowedBooks;
            }
            System.out.println("=========================================================");

            switch (operation) {
                case 1:
                    borrowedBooks = borrowBook(borrowedBooks, username, 0);
                    break;
                case 2:
                    borrowedBooks = returnBook(borrowedBooks, username, 0, 0);
                    break;
                case 3:
                    displayBorrowedBooks(borrowedBooks);
                    break;
                case 4:
                    back = 0; // Return to main menu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        return borrowedBooks;
    }

    // Allows user to borrow a book if they have fewer than 3 books unreturned
    public static List<BorrowedBook> borrowBook(List<BorrowedBook> borrowedBooks, String username, int bookId) {
        // Count books not returned
        try {
            if (BorrowedBooksManager.getBookQuantity(bookId) <= 0) {
                System.out.println("Sorry, the book with ID " + bookId + " is not available.");
                return borrowedBooks;
            }
        } catch (SQLException e) {
            System.out.println("Error checking book quantity.");
            e.printStackTrace();
            return borrowedBooks;
        }

        LocalDate dateBorrowed = LocalDate.now();
        LocalDate dueDate = dateBorrowed.plusDays(14);
        boolean isReturned = false;

        try {
            BorrowedBook newBook = new BorrowedBook(username, bookId, dateBorrowed, dueDate, isReturned);
            BorrowedBooksManager.insertBorrowedBook(newBook);
            BorrowedBooksManager.updateBookQuantity(bookId, -1);
            borrowedBooks.add(newBook);
        } catch (SQLException e) {
            System.out.println("Error while borrowing book.");
            e.printStackTrace();
        }

        return borrowedBooks;
    }


    // Allows user to return a borrowed book
    public static List<BorrowedBook> returnBook(List<BorrowedBook> borrowedBooks, String username, int bookId, int borrowedBooksId) {
        for (BorrowedBook book : borrowedBooks) {
            if (!book.isReturned() && book.getBorrowedBooksId() == borrowedBooksId) {
                try {
                    book.setReturned(true);
                    BorrowedBooksManager.updateReturnStatus(book);
                    BorrowedBooksManager.updateBookQuantity(bookId, 1);
                    break;
                } catch (SQLException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        return borrowedBooks;
    }

    // Displays currently borrowed books
    private static void displayBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        System.out.println("Your borrowed books:");
        if (borrowedBooks == null || borrowedBooks.isEmpty()) {
            System.out.println("Empty");
        } else {
            boolean anyBorrowed = false;
            for (BorrowedBook book : borrowedBooks) {
                if (book != null && !book.isReturned()) {
                    System.out.println(book);
                    anyBorrowed = true;
                }
            }
            if (!anyBorrowed) {
                System.out.println("No currently borrowed books found.");
            }
        }
    }

}
