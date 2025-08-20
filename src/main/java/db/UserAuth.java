package db;

import gui.MainFrame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

//Manages user authentication and registration.
public class UserAuth {
    private static final Properties configProps = new Properties();
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream input = UserAuth.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Unable to find config.properties");
            } else {
                configProps.load(input);
                url = configProps.getProperty("db.url") + configProps.getProperty("db.name");
                user = configProps.getProperty("db.user");
                password = configProps.getProperty("db.password");
            }
        } catch (Exception e) {
            System.out.println("Error loading config.properties");
            e.printStackTrace();
        }
    }
    
    static Scanner scanner = new Scanner(System.in);
    
    static String currentUsername = null;
    static String loggedInFullName = null;

    public static void main(String[] args) {
        App app = new App();
        // Run login or registration flow
        loginOrRegister(app);
    }

    /**
     * Prompts user to login or register.
     */
    public static void loginOrRegister(App app) {
        int choice;
        do {
            System.out.println("=========================================================");
            System.out.println("         Azure Archive: Library Management System        ");
            System.out.println("=========================================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("=========================================================");
            System.out.print("Choose an option: ");
            if (!scanner.hasNextInt()) { // Check if the input is an integer before reading
                System.out.println("Invalid input, Please try again.");
                scanner.nextLine(); // Consume invalid input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login(app);
                    break;
                case 2:
                    //register();
                    break;
                case 3:
                    if (confirmExit() == 0) { // If user confirms exit, terminate
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    public static int confirmExit() {
        int exit;
        
        while (true) {
            System.out.print("Are you sure you want to exit? (1 = Yes / 0 = No): ");
            
            if (!scanner.hasNextInt()) { // Check if the input is an integer before reading
                System.out.println("Invalid input, please enter 1 for Yes or 0 for No.");
                scanner.nextLine(); // Consume invalid input
                continue;
            }
            exit = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (exit == 1) {
                System.out.println("Thank you for using Azure Archive. Goodbye!");
                return 0; // Confirm exit
            } else if (exit == 0) {
                System.out.println("Returning to the main menu...");
                return 1; // Do not exit
            } else {
                System.out.println("Invalid input, please enter 1 for Yes or 0 for No.");
            }
        }
    }

    public static void logout() {
        try {
            clearSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        currentUsername = null;
        loggedInFullName = null;

        System.out.println("=========================================================");
        System.out.println("Logging out...");
        System.out.println("You have been logged out.");
    }
    
    // Logs out the user
    public static void logout(MainFrame frame) {
        try {
            UserAuth.clearSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear static user info
        loggedInFullName = null;
        currentUsername = null;

        // Call MainFrame to reset UI
        frame.resetUI();

        System.out.println("=========================================================");
        System.out.println("Logging out...");
        System.out.println("You have been logged out.");

        loginOrRegister(new App());
    }

    // Registers a new user.
    public static boolean register(String full_name, String user_name, String user_email, String user_password) {
        String username = user_name;
        String inputPassword = user_password;
        String email = user_email;
        String fullName = full_name;
        // TEMPORARY: Para lang maging successful yung pag-POST sa database during testing
        int age = 20;

        while (true) {
            // Validate email format and domain
            if (!isValidEmail(email)) {
                return false;
            }
            // Check if username already exists
            if (isUsernameTaken(username)) {
                return false;
            } else {
                break; // All validations passed
            }
        }
        // Insert the users info into the database
        String sql = "INSERT INTO users (username, password, email, fullName, age) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, inputPassword);
            pstmt.setString(3, email);
            // TEMPORARY: full name is the same as username para lang gumana during testing
            pstmt.setString(4, fullName);
            pstmt.setInt(5, age);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Registration failed! Possible duplicate username or database issue.");
            e.printStackTrace();
        }
        //loginOrRegister(new App());
        return false;
    }

    // Method to validate username for duplication
    private static boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count is greater than 0, username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if there's an error
    }

    // method to validate the email
    private static boolean isValidEmail(String email) {
        // Check if email ends with @gmail.com
        return email.endsWith("@gmail.com");
    }

    // Logs in an existing user.
    private static void login(App app) {
        while (true) {
            System.out.println("=========================================================");
            System.out.println("            Login your Azure Archive Account             ");
            System.out.println("=========================================================");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String inputPassword = scanner.nextLine();
            System.out.println("=========================================================");
            try {
                username = validateAccount(username, inputPassword);
                if (username != null) {
                    setSession(username);
                    System.out.println("Login successful!");
                    System.out.println("=========================================================");
                    currentUsername = username;
                    loggedInFullName = fetchFullNameFromDatabase(username);
                    System.out.println("Full name from DB: " + loggedInFullName);
                    UserAuth.setFullName(loggedInFullName);
                    System.out.println("Launching GUI...");
                    MainFrame frame = new MainFrame(username, loggedInFullName);
                    frame.setVisible(true);
                    return; // Exit the login method
                } else {
                    System.out.println("Login failed! Invalid username or password. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while trying to log in.");
                e.printStackTrace();
            }

            System.out.print("Would you like to register instead? (yes/no): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("yes")) {
                //register();
                System.out.println("pass");
            }
        }
    }
 
     // Validates user credentials against the database.
     // username the username to validate
     // userPassword the password to validate
     // return true if valid, false otherwise
     // throws SQLException if a database access error occurs
    public static String validateAccount(String email, String userPassword) throws SQLException {
        String sql = "SELECT username FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, userPassword);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                return rs.getString("username");
            }
        }
        return null;
    }

    public static void setSession(String username) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM session WHERE sessionId = 1";
        String updateSql = "UPDATE session SET username = ? WHERE sessionId = 1";
        String insertSql = "INSERT INTO session (sessionId, username) VALUES (1, ?)";
    
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);
            int count = 0;

            // Check if session row exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

            if (count > 0) {
                // Row exists → update
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                }
            } else {
                // Row doesn't exist → insert
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("Session updated/created for user: " + username);
        }
    }

    public static String getSession() throws SQLException {
        String sql = "SELECT username FROM session WHERE sessionId = 1";

        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("username");
            }
        }

        return null;
    }

    public static String getUsername() {
        String username = null;

        try {
            username = UserAuth.getSession();
            return username;
        } catch (Exception e) {
            System.out.println(e);
        }

        return username;
    }
    
    public static void clearSession() throws SQLException {
        String sql = "UPDATE session SET username = NULL WHERE sessionId = 1";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Session cleared in database.");
        }

        currentUsername = null;
        loggedInFullName = null;
    }

    public static void setFullName(String fullName) {
        loggedInFullName = fullName;
    }

    public static String getFullName() {
        return loggedInFullName;
    }
    
    public static String fetchFullNameFromDatabase(String username) {
        String sql = "SELECT fullName FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("fullName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}