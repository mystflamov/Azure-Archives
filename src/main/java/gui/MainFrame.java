package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import db.UserAuth;

public class MainFrame extends JFrame {
    // Layouts for managing different sets of panels
    private CardLayout cardLayoutWithSidebar;
    private JPanel mainWithSidebarPanel; // Contains the sidebar and the content panel

    private CardLayout cardLayoutNoSidebar;
    private JPanel mainNoSidebarPanel; // Contains login and signup screens only

    // Panels for different parts of the application
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private HomePanel homePanel;
    private BooksPanel booksPanel;
    private BorrowPanel borrowPanel;
    JPanel contentPanel = new JPanel(cardLayoutWithSidebar);

    // Sidebar panel
    private SidebarPanel sidebarPanel;

    // Current user
    private String currentUser;
    
    // Username and full name
    private String username;
    private String fullName;

    // Constructor to initialize the main frame and its components
    public MainFrame() {
        setTitle("Azure Archives");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        // === Section 1: Screens without sidebar (Login & Signup) ===
        cardLayoutNoSidebar = new CardLayout();
        mainNoSidebarPanel = new JPanel(cardLayoutNoSidebar);

        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);

        mainNoSidebarPanel.add(loginPanel, "login");
        mainNoSidebarPanel.add(signUpPanel, "signup");

        // === Section 2: Screens with sidebar (Home, Books, Borrow) ===
        cardLayoutWithSidebar = new CardLayout();
        

        // Initialize the sidebar—defaulting to the "home" section
        sidebarPanel = new SidebarPanel(this, "home");

        // Combine sidebar and content into one container
        mainWithSidebarPanel = new JPanel(new BorderLayout());
        mainWithSidebarPanel.add(sidebarPanel, BorderLayout.WEST);
        mainWithSidebarPanel.add(contentPanel, BorderLayout.CENTER);

        // Show the login screen by default on app launch
        setContentPane(mainNoSidebarPanel);
        cardLayoutNoSidebar.show(mainNoSidebarPanel, "login");

        setVisible(true);
    }
    
    public MainFrame(String username, String fullName) {
        this(); // Calls the no-arg constructor to set up all panels

        // Store them in fields
        this.username = username;
        this.fullName = fullName;
        UserAuth.setFullName(fullName); // Optional but ensures sync
        onLoginSuccess(username);       // Load user-specific panels after login
    }

    // Method to show page by name
    public void showPage(String pageName, String searchedBook) {
        reloadPage(pageName, searchedBook);

        if (pageName.equals("login") || pageName.equals("signup")) {
            // Show no-sidebar container
            setContentPane(mainNoSidebarPanel);
            cardLayoutNoSidebar.show(mainNoSidebarPanel, pageName);
        } else {
            // Show sidebar container
            setContentPane(mainWithSidebarPanel);
            cardLayoutWithSidebar.show((JPanel)mainWithSidebarPanel.getComponent(1), pageName);  // The content panel is the 2nd child
            // Update sidebar selection highlight
            sidebarPanel.updateSelectedSection(pageName);
        }

        revalidate();
        repaint();
    }

    public void reloadPage(String pageName, String searchedBook) {
        String username = null;

        try {
            username = UserAuth.getSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (username != null) {
            if (pageName.equals("books")) {
                booksPanel = new BooksPanel(this, searchedBook);
                contentPanel.add(booksPanel, "books");
            } else if (pageName.equals("borrow")) {
                borrowPanel = new BorrowPanel(this);
                contentPanel.add(borrowPanel, "borrow");
            } else if (pageName.equals("home")) {
                String fullName = UserAuth.getFullName();
                if (fullName == null) {
                    fullName = UserAuth.fetchFullNameFromDatabase(username);
                    UserAuth.setFullName(fullName);
                }

                homePanel = new HomePanel(this, username, fullName);
                contentPanel.add(homePanel, "home");
            }
        }
    }
    
    // Redirect user to login screen after signing up
    public void onSignUpSuccess() {
        showPage("login", "");
    }

    // Load user-specific panels after logging in
    public void onLoginSuccess(String name) {
        try {
            String username = name;

            UserAuth.setSession(username);

            String fullName = UserAuth.fetchFullNameFromDatabase(username);
            UserAuth.setFullName(fullName);
            this.fullName = fullName;

            booksPanel = new BooksPanel(this, username);
            borrowPanel = new BorrowPanel(this);
            homePanel = new HomePanel(this, username, fullName);

            contentPanel = new JPanel(cardLayoutWithSidebar);
            contentPanel.add(homePanel, "home");
            contentPanel.add(booksPanel, "books");
            contentPanel.add(borrowPanel, "borrow");

            sidebarPanel = new SidebarPanel(this, "home");

            mainWithSidebarPanel.removeAll();
            mainWithSidebarPanel.add(sidebarPanel, BorderLayout.WEST);
            mainWithSidebarPanel.add(contentPanel, BorderLayout.CENTER);

            setContentPane(mainWithSidebarPanel);
            cardLayoutWithSidebar.show(contentPanel, "home");

            revalidate();
            repaint();

        } catch (SQLException e) {
            // Show a friendly error message to the user
            JOptionPane.showMessageDialog(this,
                "Failed to log in due to a database error.\n" + e.getMessage(),
                "Login Error",
                JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }
    
    public void logout() {
        // Clear session (optional)
        try {
            UserAuth.clearSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Reset panels (optional, to free memory or refresh)
        homePanel = null;
        booksPanel = null;
        borrowPanel = null;

        // Switch to login screen
        setContentPane(mainNoSidebarPanel);
        cardLayoutNoSidebar.show(mainNoSidebarPanel, "login");

        revalidate();
        repaint();
    }
    
    public void resetUI() {
        contentPanel.removeAll();
        contentPanel = new JPanel(cardLayoutWithSidebar);

        homePanel = null;
        booksPanel = null;
        borrowPanel = null;

        setContentPane(mainNoSidebarPanel);
        cardLayoutNoSidebar.show(mainNoSidebarPanel, "login");

        revalidate();
        repaint();
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}