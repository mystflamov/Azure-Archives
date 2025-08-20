package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import db.UserAuth;

public class SignUpPanel extends JPanel {
    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    public SignUpPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());

        // === Background Image ===
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/background.png"));
        Image bgImage = bgIcon.getImage();

        // Panel to hold the background image and paint it
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(new BorderLayout());

        // === Header ===
        ImageIcon logo = new ImageIcon(getClass().getResource("/images/logo.png"));
        Image resizedImage = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedLogo = new ImageIcon(resizedImage);

        JLabel label1 = new JLabel("Azure Archives", resizedLogo, SwingConstants.CENTER);
        label1.setHorizontalTextPosition(JLabel.CENTER);
        label1.setVerticalTextPosition(JLabel.BOTTOM);
        label1.setForeground(new Color(29, 49, 106));
        label1.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 30f));
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label2 = new JLabel("Library Management System", SwingConstants.CENTER);
        label2.setForeground(new Color(29, 49, 106));
        label2.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 25f));
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        headerPanel.add(label1);
        headerPanel.add(label2);
        
        // === Banner ===
        JLabel label3 = new JLabel("Create an account!", SwingConstants.CENTER);
        label3.setForeground(new Color(29, 49, 106));
        label3.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 40f));

        JPanel bannerPanel = new JPanel();
        bannerPanel.setOpaque(false);
        bannerPanel.setLayout(new BoxLayout(bannerPanel, BoxLayout.Y_AXIS));
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        bannerPanel.add(label3);
        bannerPanel.add(Box.createVerticalStrut(20));

        // === Form Input Fields ===
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        Dimension fieldSize = new Dimension(290, 50);
        
        // Username and full name fields
        fullNameField = createTextField("Full Name");
        usernameField = createTextField("Username");
        
        fullNameField.setPreferredSize(fieldSize);
        fullNameField.setMaximumSize(fieldSize);
        usernameField.setPreferredSize(fieldSize);
        usernameField.setMaximumSize(fieldSize);
        
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        // Panel to hold both fields side-by-side
        JPanel nameRow = new JPanel();
        nameRow.setOpaque(false);
        nameRow.setLayout(new BoxLayout(nameRow, BoxLayout.X_AXIS));
        nameRow.setMaximumSize(new Dimension(600, 50));
        nameRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameRow.add(fullNameField);
        nameRow.add(Box.createRigidArea(new Dimension(20, 0))); // horizontal spacing
        nameRow.add(usernameField);
        
        // Email field
        emailField = createTextField("Email");
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setHorizontalAlignment(SwingConstants.LEFT);
        emailField.setPreferredSize(new Dimension(600, 50));
        emailField.setMaximumSize(new Dimension(600, 50));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        // Password and confirm password fields
        passwordField = createPasswordField("Password");
        confirmPasswordField = createPasswordField("Confirm Password");
        
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        confirmPasswordField.setPreferredSize(fieldSize);
        confirmPasswordField.setMaximumSize(fieldSize);
        
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(29, 49, 106), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        // Panel to hold both fields side-by-side
        JPanel passwordRow = new JPanel();
        passwordRow.setOpaque(false);
        passwordRow.setLayout(new BoxLayout(passwordRow, BoxLayout.X_AXIS));
        passwordRow.setMaximumSize(new Dimension(600, 50));
        passwordRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        passwordRow.add(passwordField);
        passwordRow.add(Box.createRigidArea(new Dimension(20, 0))); // horizontal spacing
        passwordRow.add(confirmPasswordField);

        // Add fields to the form panel
        formPanel.add(nameRow);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordRow);
        formPanel.add(Box.createVerticalStrut(50));

        // === Sign-Up Button ===
        JButton signupButton = new JButton("Sign Up");
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.setBackground(new Color(29, 49, 106));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        signupButton.setFocusPainted(false);
        signupButton.setMaximumSize(new Dimension(600, 50));

        signupButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            // Basic validation for empty or default placeholder text
            if (fullName.isEmpty() || fullName.equals("Full Name") ||
                username.isEmpty() || username.equals("Username") ||
                email.isEmpty() || email.equals("Email") ||
                password.isEmpty() || password.equals("Password") ||
                confirmPassword.isEmpty() || confirmPassword.equals("Confirm Password")) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean valid = UserAuth.register(fullName, username, email, password);
                if (valid) {
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                    clearFields();
                    mainFrame.onSignUpSuccess();
                } else {
                    JOptionPane.showMessageDialog(this, "Account creation failed. Enter valid credentials to continue.");
                }
            }
        });

        formPanel.add(signupButton);

        // === Login Link ===
        JLabel label4 = new JLabel("Already have an account?");
        label4.setForeground(new Color(29, 49, 106));
        label4.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 20f));

        JLabel loginLabel = new JLabel(" Login");
        loginLabel.setForeground(new Color(29, 49, 106));
        loginLabel.setFont(FontLoader.loadFont("/fonts/Poppins-Bold.ttf", 20f));
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPage("login", "");
            }
        });

        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.X_AXIS));
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(label4);
        loginPanel.add(loginLabel);

        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(loginPanel);

        // === Center Wrapper: Holds banner + form ===
        JPanel centerWrapper = new JPanel();
        centerWrapper.setOpaque(false);
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));

        centerWrapper.add(Box.createVerticalStrut(50));
        centerWrapper.add(bannerPanel);
        centerWrapper.add(Box.createVerticalStrut(10));
        centerWrapper.add(formPanel);
        centerWrapper.add(Box.createVerticalGlue());

        // Add components to background panel
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);

        // Add background to main panel
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    // Reset all input fields to their default placeholder text and appearance
    private void clearFields() {
        fullNameField.setText("Full Name");
        fullNameField.setForeground(Color.GRAY);

        usernameField.setText("Username");
        usernameField.setForeground(Color.GRAY);

        emailField.setText("Email");
        emailField.setForeground(Color.GRAY);

        passwordField.setText("Password");
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0); // Show plain text placeholder

        confirmPasswordField.setText("Confirm Password");
        confirmPasswordField.setForeground(Color.GRAY);
        confirmPasswordField.setEchoChar((char) 0); // Show plain text placeholder
    }

    // Create text field with placeholder
    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 16f));
        textField.setBorder(BorderFactory.createLineBorder(Color.decode("#1d316a"), 2));
        textField.setMaximumSize(new Dimension(600, 50));

        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }

    // Create password field with placeholder
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(placeholder);
        passwordField.setForeground(Color.GRAY);
        passwordField.setFont(FontLoader.loadFont("/fonts/Poppins-Regular.ttf", 16f));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.decode("#1d316a"), 2));
        passwordField.setMaximumSize(new Dimension(600, 50));
        passwordField.setEchoChar((char) 0);

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });

        return passwordField;
    }
    
    // Main method to launch this panel inside MainFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}